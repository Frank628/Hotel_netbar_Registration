package com.jinchao.registration.dbtable;

import org.xutils.DbManager;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.ex.DbException;

import java.io.Serializable;
import java.util.List;

/**
 * 房间号/座席 表/订单表
 * Created by user on 2017/3/19.
 */
@Table(name="seat")
public class SeatTable implements Serializable{
    @Column(name = "ID", isId = true, autoGen = true)
    private int id;

    @Column(name = "seatid")
    private String seatid;

    @Column(name = "seatcode")
    private String seatcode;
    /**
     * 唯一识别号
     */
    @Column(name = "guid")
    private String guid;


    @Column(name = "starttime")
    private String starttime;

    @Column(name = "endtime")
    private String endtime;
    /**
     * 类型（黑网吧：1，民宿2）
     */
    @Column(name = "type")
    private String type;
    /**
     * 此次入住到离店消费金额
     */
    @Column(name = "price")
    private String price;

    /**
     * 此订单是否以生产并发送至服务器
     */
    @Column(name = "issubmit")
    private String issubmit;

    public SeatTable() {
    }

    public SeatTable(String seatid,String seatcode, String guid, String type) {
        this.seatid=seatid;
        this.seatcode = seatcode;
        this.guid = guid;
        this.type = type;
    }

    public List<PersonTable> getPersonsInSeat(DbManager db) throws DbException{
        return db.selector(PersonTable.class).where("pk_guid", "=", this.guid).findAll();
    }

    public String getSeatid() {
        return seatid;
    }

    public void setSeatid(String seatid) {
        this.seatid = seatid;
    }

    public String getSeatcode() {
        return seatcode;
    }

    public void setSeatcode(String seatcode) {
        this.seatcode = seatcode;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIssubmit() {
        return issubmit;
    }

    public void setIssubmit(String issubmit) {
        this.issubmit = issubmit;
    }
}
