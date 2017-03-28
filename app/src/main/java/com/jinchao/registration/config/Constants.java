package com.jinchao.registration.config;

import android.os.Environment;

/**
 * Created by user on 2017/3/19.
 */

public class Constants {
    public static final String CANARO_EXTRA_BOLD_PATH = "fonts/XQN.otf";
    public static final String DB_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/";

    public static final String URL="http://222.92.144.66:91/population/";

    public static final String USER_NAME= "USER_NAME";
    public static final String USER_ID= "USER_ID";
    public static final String SRV_TIME= "SRV_TIME";//服务截至时间
    public static final String PASSWORD= "PASSWORD";//密码
    public static final String DP_NAME= "DP_NAME";//店铺名称
    public static final String ACCOUNT_TYPE= "ACCOUNT_TYPE";//店铺类型
    public static final String HRS_NAME= "HRS_NAME";//业主名字
}
