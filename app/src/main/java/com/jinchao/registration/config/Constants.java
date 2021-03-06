package com.jinchao.registration.config;

import android.os.Environment;

/**
 * Created by user on 2017/3/19.
 */

public class Constants {
    public static final String CANARO_EXTRA_BOLD_PATH = "fonts/TT.ttf";
    public static final String CANARO_EXTRA_BOLD_PATH2 = "fonts/fzct2.otf";
    public static final String DB_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/";

    public static final String URL="http://222.92.144.66:91/population/";

    public static final String USER_NAME= "USER_NAME";
    public static final String USER_ID= "USER_ID";
    public static final String SRV_TIME= "SRV_TIME";//服务截至时间
    public static final String PASSWORD= "PASSWORD";//密码
    public static final String DP_NAME= "DP_NAME";//店铺名称
    public static final String ACCOUNT_TYPE= "ACCOUNT_TYPE";//店铺类型
    public static final String HRS_NAME= "HRS_NAME";//业主名字
    public static final String LAST_VERSION= "LAST_VERSION";//最新版本
    public static final String FORCE_UPDATE= "FORCE_UPDATE";//是否强制更新
    public static final String APK_URL= "APK_URL";//下载地址
    public static final String IS_NEED_GUID= "IS_NEED_GUID";//是否需要启用向导
}
