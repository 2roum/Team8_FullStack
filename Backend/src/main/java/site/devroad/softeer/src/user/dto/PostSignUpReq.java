package site.devroad.softeer.src.user.dto;

public class PostSignUpReq {
    private String email;
    private String name;
    private String phoneNumber;
    private String password;

    public PostSignUpReq(String email, String name, String phoneNumber, String password) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean hasNull() {
        if (email == null || name == null || phoneNumber == null || password == null)
            return true;
        if (email.trim().equals("") || name.trim().equals("") || phoneNumber.trim().equals("") || password.trim().equals(""))
            return true;
        return false;
    }
}
