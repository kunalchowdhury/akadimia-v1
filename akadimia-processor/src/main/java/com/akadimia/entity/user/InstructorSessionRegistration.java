package com.akadimia.entity.user;

public class InstructorSessionRegistration extends Entity {

    private long sessionStartTime;
    private long sessionEndTime ;
    private String subject;
    private String name;
    private String address;
    private long sentAt ;
    private String emailId;


    public InstructorSessionRegistration() {
    }

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(long sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public long getSessionEndTime() {
        return sessionEndTime;
    }

    public void setSessionEndTime(long sessionEndTime) {
        this.sessionEndTime = sessionEndTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @Override
    public String toString() {
        return "InstructorSessionRegistration{" +
                "sessionStartTime=" + sessionStartTime +
                ", sessionEndTime=" + sessionEndTime +
                ", subject='" + subject + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", sentAt=" + sentAt +
                ", emailId='" + emailId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
