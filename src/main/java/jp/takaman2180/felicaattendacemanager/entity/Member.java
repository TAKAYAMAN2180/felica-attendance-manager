package jp.takaman2180.felicaattendacemanager.entity;

public class Member {
    private String idm;
    private boolean is_entry;

    public void setValues(String idm, boolean is_entry) {
        this.idm = idm;
        this.is_entry = is_entry;
    }

    public String getIdm() {
        return idm;
    }

    public boolean getIs_entry(){
        return is_entry;
    }


}
