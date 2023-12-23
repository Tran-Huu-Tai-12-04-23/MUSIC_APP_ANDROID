package DTO;

public class ChangePasswordRequest {
    private String userPhone;
    private String password;
    private String confirmPassword;

    public String getUserPhone() {
        return userPhone;
    }

    public ChangePasswordRequest() {
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
