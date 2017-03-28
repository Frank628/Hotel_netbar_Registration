package com.jinchao.registration.jsonbean;

/**
 * Created by user on 2017/3/23.
 */

public class ADEOperationResult {
    public int code;
    public String msg="";
    public ADEOne data;
    public static class ADEOne{
        public boolean success=false;
    }
}
