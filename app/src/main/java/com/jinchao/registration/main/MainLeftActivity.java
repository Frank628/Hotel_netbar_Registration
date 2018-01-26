package com.jinchao.registration.main;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.Common;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.jinchao.registration.Base.BaseActivity;
import com.jinchao.registration.Base.BaseFragment;
import com.jinchao.registration.Base.BaseReaderActiviy;
import com.jinchao.registration.R;
import com.jinchao.registration.SysApplication;
import com.jinchao.registration.checkin.CheckInFragment;
import com.jinchao.registration.config.Constants;
import com.jinchao.registration.config.MyInforManager;
import com.jinchao.registration.informanagement.InforManagementFragment;
import com.jinchao.registration.logoff.LogOffFragment;
import com.jinchao.registration.setting.SettingFragment;
import com.jinchao.registration.utils.SharePrefUtil;
import com.jinchao.registration.widget.CanaroRadioButton;
import com.jinchao.registration.widget.CanaroTextView;
import com.jinchao.registration.widget.DrawerArrowDrawable;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import org.xutils.view.annotation.ViewInject;

import java.util.List;

import static android.view.Gravity.START;

/**
 * Created by user on 2017/6/26.
 */

public class MainLeftActivity extends BaseReaderActiviy implements View.OnClickListener{
    @ViewInject(R.id.title)CanaroTextView title;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private long mExitTime;
    RadioGroup rg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Common.init(this)){
            Toast.makeText(this, "身份证云终端开发包初始化失败！", Toast.LENGTH_SHORT).show();
            finish();
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ImageView imageView = (ImageView) findViewById(R.id.drawer_indicator);
        final Resources resources = getResources();

        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));
        imageView.setImageDrawable(drawerArrowDrawable);

        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;

                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(flipped);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(flipped);
                }
                drawerArrowDrawable.setParameter(offset);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (drawer.isDrawerVisible(START)) {
                    drawer.closeDrawer(START);
                } else {
                    drawer.openDrawer(START);
                }
            }
        });

        showGuid();
    }

    private void showGuid(){
        if (!SharePrefUtil.getBoolean(this, Constants.IS_NEED_GUID,true))return;
        SharePrefUtil.saveBoolean(this,Constants.IS_NEED_GUID,false);
        TapTargetView.showFor(this, TapTarget.forView(findViewById(R.id.drawer_indicator), "使用教程", "点击闪烁区域的按钮弹出菜单栏")
                .cancelable(false)
                .textTypeface(Typeface.MONOSPACE)
                .descriptionTextSize(18)
                .descriptionTextColor(R.color.white)
                .targetCircleColor(R.color.colorAccent)
                .outerCircleColor(R.color.guillotine_background_dark)
                .outerCircleAlpha(0.95f)
                .drawShadow(true)
                .tintTarget(false), new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
//                guillotineAnimation.open();
            }

            @Override
            public void onOuterCircleClick(TapTargetView view) {
                super.onOuterCircleClick(view);
            }

            @Override
            public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
            }
        });
    }
    public void changeFragment(Fragment fragment){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.container,fragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        List<Fragment> fragments=getSupportFragmentManager().getFragments();
        if (fragments==null)return;
        for (int i=0;i<fragments.size();i++){
            if (((BaseFragment)fragments.get(i))!=null) {
                ((BaseFragment) fragments.get(i)).onNewIntent(intent);
            }
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                SysApplication.getInstance().exit();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cb_checkin:
                ((CanaroRadioButton)rg.getChildAt(0)).setChecked(true);
                changeFragment(new CheckInFragment());
                title.setText(getString(R.string.chekin_value));
//                guillotineAnimation.close();
                break;
            case R.id.cb_logoff:
                ((CanaroRadioButton)rg.getChildAt(1)).setChecked(true);
                changeFragment(new LogOffFragment());
                title.setText(getString(R.string.logoff_value));
//                guillotineAnimation.close();
                break;
            case R.id.cb_info:
                ((CanaroRadioButton)rg.getChildAt(2)).setChecked(true);
                changeFragment(new InforManagementFragment());
                title.setText(getString(R.string.infomanagement_value));
//                guillotineAnimation.close();
                break;
            case R.id.cb_setting:
                ((CanaroRadioButton)rg.getChildAt(3)).setChecked(true);
                changeFragment(new SettingFragment());
                title.setText(getString(R.string.setting));
//                guillotineAnimation.close();
                break;
        }
    }
}
