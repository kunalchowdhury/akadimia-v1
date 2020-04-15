package com.akadimia.microservices.core.reservationreqservice.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="confirmed_reservations")
public class ConfirmedReservationEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private String crid;

    private String userId;
    private String instId;
    private String subject;
    private String startTime;
    private String endTime;
    private String instructorName;
    private String sessionId;
    private long fromDate ;
    private long toDate;


    public ConfirmedReservationEntity() {
    }

    public ConfirmedReservationEntity(String id, Integer version, String crid, String userId, String instId,
                                      String subject, String startTime, String endTime,
                                      String instructorName, String sessionId, long fromDate, long toDate) {
        this.id = id;
        this.version = version;
        this.crid = crid;
        this.userId = userId;
        this.instId = instId;
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
        this.instructorName = instructorName;
        this.sessionId = sessionId;
        this.fromDate = fromDate;
        this.toDate = toDate;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCrid() {
        return crid;
    }

    public void setCrid(String crid) {
        this.crid = crid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }
}
