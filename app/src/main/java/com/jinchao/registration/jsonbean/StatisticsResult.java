package com.jinchao.registration.jsonbean;

/**
 * Created by user on 2017/3/23.
 */

public class StatisticsResult {
    public int code;
    public String msg="";
    public StatisticsDetail data;
    public static class StatisticsDetail{
        public String price="";
        public String person="";
    }
}
