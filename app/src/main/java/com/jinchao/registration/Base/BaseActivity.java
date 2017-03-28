package com.jinchao.registration.Base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.jinchao.registration.MyApplication;
import com.jinchao.registration.SysApplication;
import com.jinchao.registration.utils.CommonUtils;

import org.xutils.x;

/**
 * Created by user on 2017/3/17.
 */

public class BaseActivity extends AppCompatActivity {
    public ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        SysApplication.getInstance().addExIndexActivity(this);
        CommonUtils.changeUserType(this,((MyApplication)getApplication()).accountType);

    }
    public void showProcessDialog(String msg) {
        dialog = new ProgressDialog(this);
        dialog.setMessage(msg);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.show();
    }
    public void hideProcessDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
