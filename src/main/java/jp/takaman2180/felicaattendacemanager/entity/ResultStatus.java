package jp.takaman2180.felicaattendacemanager.entity;

public enum ResultStatus {
    ATTEND("attend"),
    EXIT("exit"),
    ERROR("error");

    private String msg;
    ResultStatus(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
