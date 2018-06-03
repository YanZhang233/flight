package com.zz.flight.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.lang.annotation.Documented;
import java.util.Date;

@Entity
@Table(name = "request",schema = "targetSchemaName")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestUserName;

    private String takenUserName;

    private Long requestUserId;

    private Long takenUserId;

    private String airport;

    private Integer baggage;

    private Integer numOfPeople;

    private String destination;

    private String time;

    private String flightInfo;

    private String description;

    private Integer status;

    private Date updateTime;

    private Date createTime;

    public Request(){}


    public Request(Long id,Integer baggage,Integer numOfPeople ,String requestUserName, String airport, String takenUserName, Long requestUserId, Long takenUserId, String destination, String description, String flightInfo, Integer status, String time, Date updateTime, Date createTime){
        this.id = id;
        this.baggage = baggage;
        this.numOfPeople = numOfPeople;
        this.requestUserId = requestUserId;
        this.description = description;
        this.takenUserName = takenUserName;
        this.takenUserId = takenUserId;
        this.requestUserName = requestUserName;
        this.airport = airport;
        this.destination = destination;
        this.flightInfo = flightInfo;
        this.status = status;
        this.time = time;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestUserName() {
        return requestUserName;
    }

    public void setRequestUserName(String requestUserName) {
        this.requestUserName = requestUserName;
    }

    public String getTakenUserName() {
        return takenUserName;
    }

    public void setTakenUserName(String takenUserName) {
        this.takenUserName = takenUserName;
    }

    public Long getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(Long requestUserId) {
        this.requestUserId = requestUserId;
    }

    public Long getTakenUserId() {
        return takenUserId;
    }

    public void setTakenUserId(Long takenUserId) {
        this.takenUserId = takenUserId;
    }

    public String getAirport() {
        return airport;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFlightInfo() {
        return flightInfo;
    }

    public void setFlightInfo(String flightInfo) {
        this.flightInfo = flightInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getBaggage() {
        return baggage;
    }

    public void setBaggage(Integer baggage) {
        this.baggage = baggage;
    }

    public Integer getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(Integer numOfPeople) {
        this.numOfPeople = numOfPeople;
    }
}
