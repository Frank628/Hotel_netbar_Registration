package com.jinchao.registration.jsonbean;

import java.util.List;

/**
 * Created by user on 2017/3/23.
 */

public class UnAvailableRoomResult {
    public int code;
    public String msg="";
    public List<UnAvailableRoomOne> data;
    public static class UnAvailableRoomOne{
        public String order_id="";
        public String start_time="";
        public String roomName="";
        public String r_id="";
        public List<PersonInRoom> peopleData;
    }
    public static class PersonInRoom{
        public String sname="";
        public String sfz="";
    }
}
