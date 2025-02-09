package School1337.helper;

public class LoginRecord {
    private String date;
    private String time;
    private String ip;

    public LoginRecord(String date, String time, String ip) {
        this.date = date;
        this.time = time;
        this.ip = ip;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getIp() {
        return ip;
    }
}
