package DTO;


import com.google.gson.annotations.SerializedName;

public class ResponseData<T> {
    @SerializedName("data")
    private T data;
    @SerializedName("message")
    private String message;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public class Data {
        private int id;
        private String username;
        private String password;
        private String phoneNumber;

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }


}
