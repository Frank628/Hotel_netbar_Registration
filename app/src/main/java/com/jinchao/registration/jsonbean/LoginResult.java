package com.jinchao.registration.jsonbean;

/**
 * Created by user on 2017/3/23.
 */

public class LoginResult {
    public int code;
    public String msg="";
    public LoginResultInfor data;
    public static class LoginResultInfor{
        public String dp_name="";
        public String hrs_name="";
        public String hrs_type="";
        public String id="";
        public String login_name="";
        public String srv_enable=""; //1可用
        public String srv_endtime="";
    }
}
