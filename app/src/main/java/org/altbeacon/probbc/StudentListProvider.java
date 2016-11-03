package org.altbeacon.probbc;



/**
 * Created by Harshad Shinde on 2/29/2016.
 */


public class StudentListProvider {
    private String name;
    private String status;
    private String macid;


    public StudentListProvider( String name, String status,String macid) {

        this.setStatus(status);
        this.setName(name);
        this.setMacid(macid);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacid() {
        return macid;
    }

    public void setMacid(String macid) {
        this.macid = macid;
    }
}

