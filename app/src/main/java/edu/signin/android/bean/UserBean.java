package edu.signin.android.bean;

public class UserBean {

    private int id;

    private String sname;

    private String scode;

    private String sclass;

    public UserBean(int id, String sname, String scode, String sclass) {
        this.id = id;
        this.sname = sname;
        this.scode = scode;
        this.sclass = sclass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getScode() {
        return scode;
    }

    public void setScode(String scode) {
        this.scode = scode;
    }

    public String getSclass() {
        return sclass;
    }

    public void setSclass(String sclass) {
        this.sclass = sclass;
    }
}
