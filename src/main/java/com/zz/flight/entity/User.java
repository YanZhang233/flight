package com.zz.flight.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user" ,schema = "targetSchemaName")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    private String gender;

    private String password;

    private String email;

    private Integer emailChecked;

    private String phone;

    private Integer role;

    private Date createTime;

    private Date updateTime;

    public User(){}


    public User(Long id, String userName,String gender ,String email,Integer emailChecked ,String password,Integer role, String phone, Date createTime, Date updateTime){
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.gender = gender;
        this.password = password;
        this.emailChecked = emailChecked;
        this.phone = phone;
        this.role = role;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getEmailChecked() {
        return emailChecked;
    }

    public void setEmailChecked(Integer emailChecked) {
        this.emailChecked = emailChecked;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
