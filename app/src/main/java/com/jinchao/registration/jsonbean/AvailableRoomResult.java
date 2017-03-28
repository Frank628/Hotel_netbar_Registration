package com.jinchao.registration.jsonbean;

import java.util.List;

/**
 * Created by user on 2017/3/23.
 */

public class AvailableRoomResult {
    public int code;
    public String msg="";
    public List<AvailableRoomOne> data;
    public static class AvailableRoomOne{
        public String r_beds="";
        public String r_enable="";//0可用1不可用
        public String r_id="";
        public String r_name="";
        public String r_price="";

    }
}
