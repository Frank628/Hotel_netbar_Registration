package com.jinchao.registration;

import android.app.Application;
import android.graphics.Typeface;

import com.Common;
import com.jinchao.registration.utils.GlobalPref;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;


/**
 * Created by user on 2017/3/17.
 */

public class MyApplication extends Application {
    public String accountType="zh";
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
        Common.init(this);//南京读卡初始化
        GlobalPref.init(this);//南京读卡sharedpre初始化
    }

    public void setAccountType(String sta){
        this.accountType=sta;
    }

}
