package com.jinchao.registration.dbtable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by user on 2017/3/19.
 */
@Table(name="person")
public class PersonTable {

    @Column(name = "ID", isId = true, autoGen = true)
    private int id;

    @Column(name = "pk_guid")
    private String pk_guid;

    @Column(name = "idcard")
    private String idcard;

    @Column(name = "name")
    private String name;

    @Column(name = "sex")
    private String sex;

    @Column(name = "nation")
    private String nation;

    @Column(name = "birth")
    private String birth;

    @Column(name = "address")
    private String address;

    @Column(name = "photo")
    private String photo;

    @Column(name = "phone")
    private String phone;

    public PersonTable() {
    }

    public PersonTable(String pk_guid, String idcard, String name, String sex, String nation, String birth, String address, String photo, String phone) {
        this.pk_guid = pk_guid;
        this.idcard = idcard;
        this.name = name;
        this.sex = sex;
        this.nation = nation;
        this.birth = birth;
        this.address = address;
        this.photo = photo;
        this.phone = phone;
    }

    public String getPk_guid() {
        return pk_guid;
    }

    public void setPk_guid(String pk_guid) {
        this.pk_guid = pk_guid;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
