package com.jinchao.registration.setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jinchao.registration.Base.BaseActivity;
import com.jinchao.registration.R;
import com.jinchao.registration.widget.NavigationLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by user on 2017/3/24.
 */
@ContentView(R.layout.activity_aboutus)
public class AboutActivity extends BaseActivity {
    @ViewInject(R.id.tv_version)
    TextView tv_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationLayout navigationLayout =(NavigationLayout) findViewById(R.id.navgation_top);
        navigationLayout.setCenterText("关于我们");
        navigationLayout.setLeftTextOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_version.setText(getVersion());
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return "版本号：v" + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    @Event(value = R.id.rl_tel)
    private void telClick(View view) {
           Intent intent = new Intent(Intent.ACTION_CALL);
           Uri data = Uri.parse("tel:" + "051265288818");
           intent.setData(data);
           startActivity(intent);
    }
}
