package com.jinchao.registration.config;

import android.content.Context;

import com.jinchao.registration.utils.SharePrefUtil;

/**
 * Created by user on 2017/3/20.
 */

public class MyInforManager {
    public static String getCurrentAccountType(Context context){
        return SharePrefUtil.getString(context,Constants.ACCOUNT_TYPE,"");
    }
    public static void setCurrentAccountType(Context context,String str){
        SharePrefUtil.saveString(context,Constants.ACCOUNT_TYPE,str);
    }

    public static String getUserID(Context context){
        return SharePrefUtil.getString(context,Constants.USER_ID,"");
    }
    public static void setUserID(Context context,String str){
        SharePrefUtil.saveString(context,Constants.USER_ID,str);
    }

    public static String getUserName(Context context){
        return SharePrefUtil.getString(context,Constants.USER_NAME,"");
    }
    public static void setUserName(Context context,String str){
        SharePrefUtil.saveString(context,Constants.USER_NAME,str);
    }

    public static String getPassword(Context context){
        return SharePrefUtil.getString(context,Constants.PASSWORD,"");
    }
    public static void setPassword(Context context,String str){
        SharePrefUtil.saveString(context,Constants.PASSWORD,str);
    }

    public static String getDpName(Context context){
        return SharePrefUtil.getString(context,Constants.DP_NAME,"");
    }
    public static void setDpName(Context context,String str){
        SharePrefUtil.saveString(context,Constants.DP_NAME,str);
    }

    public static String getSrvTime(Context context){
        return SharePrefUtil.getString(context,Constants.SRV_TIME,"");
    }
    public static void setSrvTime(Context context,String str){
        SharePrefUtil.saveString(context,Constants.SRV_TIME,str);
    }

    public static String getHrsName(Context context){
        return SharePrefUtil.getString(context,Constants.HRS_NAME,"");
    }
    public static void setHrsName(Context context,String str){
        SharePrefUtil.saveString(context,Constants.HRS_NAME,str);
    }
}
