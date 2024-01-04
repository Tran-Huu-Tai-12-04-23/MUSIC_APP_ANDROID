package DTO;


import Model.User;

public class UserResponse {
    private User data;
    private String message;

    public void setData(User data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

}
