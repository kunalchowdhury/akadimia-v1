package com.akadimia.entity.user;

public class ReservationRequest{

    private String id;  //session Id
    private String instructorId;
    private String userId ;
    private long sentAt ;
    private String userEmailId;
    private String instructorEmailId;
    private Boolean shouldProcess ;
    private String classLink;
    private String subject;
    private String instructorName;
    private String startTime;
    private String endTime;


    public ReservationRequest() {
    }


    public ReservationRequest(String id, String instructorId, String userId, long sentAt,
                              String userEmailId, String instructorEmailId,
                              Boolean shouldProcess, String classLink, String subject,
                              String instructorName, String startTime, String endTime) {
        this.id = id;
        this.instructorId = instructorId;
        this.userId = userId;
        this.sentAt = sentAt;
        this.userEmailId = userEmailId;
        this.instructorEmailId = instructorEmailId;
        this.shouldProcess = shouldProcess;
        this.classLink = classLink;
        this.subject = subject;
        this.instructorName = instructorName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstructorEmailId() {
        return instructorEmailId;
    }

    public void setInstructorEmailId(String instructorEmailId) {
        this.instructorEmailId = instructorEmailId;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }

    public String getUserEmailId() {
        return userEmailId;
    }

    public void setUserEmailId(String userEmailId) {
        this.userEmailId = userEmailId;
    }

    public Boolean getShouldProcess() {
        return shouldProcess;
    }

    public void setShouldProcess(Boolean shouldProcess) {
        this.shouldProcess = shouldProcess;
    }


    public String getClassLink() {
        return classLink;
    }

    public void setClassLink(String classLink) {
        this.classLink = classLink;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "ReservationRequest{" +
                "id=" + id +
                ", instructorId=" + instructorId +
                ", userId=" + userId +
                ", sentAt=" + sentAt +
                ", userEmailId='" + userEmailId + '\'' +
                ", shouldProcess=" + shouldProcess +
                ", subject=" + subject +
                ", instructorNameject=" + instructorName +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
