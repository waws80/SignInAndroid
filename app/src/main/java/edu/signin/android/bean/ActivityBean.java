package edu.signin.android.bean;

public class ActivityBean {

    private int id;

    private String name;

    private long date;

    private String location;

    private long createTime;


    public ActivityBean(int id, String name, long date, String location, long createTime) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.location = location;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
