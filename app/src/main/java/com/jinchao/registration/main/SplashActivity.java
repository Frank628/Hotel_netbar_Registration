package com.jinchao.registration.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Space;
import android.widget.TextView;

import com.jinchao.registration.Base.BaseActivity;
import com.jinchao.registration.R;
import com.jinchao.registration.utils.CommonUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by user on 2017/3/17.
 */
@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {
    @ViewInject(R.id.tv_version)TextView tv_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_version.setText("v"+ CommonUtils.getVersionName(this));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        },200);
    }
}
