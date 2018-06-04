package com.zz.flight.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 申请volunteer的请求，需要admin同意
 */
@Entity
@Table(name = "volunteer" ,schema = "targetSchemaName")
public class Volunteer{
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String applyUserName;

    private Long applyUserId;

    private Integer emailValidate;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    public Volunteer(){}

    public Volunteer(Long id,Integer emailValidate,String applyUserName,Long applyUserId,Integer status,Date createTime,Date updateTime){
        this.id = id;
        this.emailValidate = emailValidate;
        this.applyUserId = applyUserId;
        this.applyUserName = applyUserName;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public Long getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Long applyUserId) {
        this.applyUserId = applyUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getEmailValidate() {
        return emailValidate;
    }

    public void setEmailValidate(Integer emailValidate) {
        this.emailValidate = emailValidate;
    }
}
