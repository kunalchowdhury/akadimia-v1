package com.akadimia.core.session;

public class OnlineSession {
    private String subject;
    private String coachName ;
    private String startDateTime;
    private String endDateTime;
    private String videoLink ;
    private String sessionId;

    public OnlineSession() {
    }

    public OnlineSession(String subject, String coachName, String startDateTime,
                         String endDateTime, String videoLink, String sessionId) {
        this.subject = subject;
        this.coachName = coachName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.videoLink = videoLink;
        this.sessionId = sessionId;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "OnlineSession{" +
                "subject='" + subject + '\'' +
                ", coachName='" + coachName + '\'' +
                ", startDateTime='" + startDateTime + '\'' +
                ", endDateTime='" + endDateTime + '\'' +
                ", videoLink='" + videoLink + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
