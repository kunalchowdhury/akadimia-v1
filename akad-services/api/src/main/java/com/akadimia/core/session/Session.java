package com.akadimia.core.session;

public class Session {
    private String subject;
    private long fromDate ;
    private long toDate;
    private String userId;
    private String area;
    private double[] position;
    private String youTubeURL;
    private String sessionId;
    private String reserved ;


    public Session() {
    }

    public Session(String subject, long fromDate, long toDate, String userId,
                   String area, double[] position, String youTubeURL, String sessionId, String reserved) {
        this.subject = subject;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.userId = userId;
        this.area = area;
        this.position = position;
        this.youTubeURL = youTubeURL;
        this.sessionId = sessionId;
        this.reserved = reserved;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public String getYouTubeURL() {
        return youTubeURL;
    }

    public void setYouTubeURL(String youTubeURL) {
        this.youTubeURL = youTubeURL;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    @Override
    public int hashCode() {
        return sessionId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return ((Session)obj).sessionId.equals(sessionId);
    }


}
