package models;

public class enrollment {
    private int id;
    private int userId;
    private int activityId;
    private String status;

    public enrollment() {
    }

    public enrollment(int id, int userId, int activityId, String status) {
        this.id = id;
        this.userId = userId;
        this.activityId = activityId;
        this.status = status;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getActivityId() {
        return activityId;
    }
    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
