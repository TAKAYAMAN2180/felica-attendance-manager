package jp.takaman2180.felicaattendacemanager.entity;

public enum RequestStatus {

    SUCCESS("success"),
    ERROR("error"),
    BAD_REQUEST("bad request");

    private String statusMsg;

    RequestStatus(String msg) {
        this.statusMsg = msg;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String msg) {
        this.statusMsg = msg;
    }
}
