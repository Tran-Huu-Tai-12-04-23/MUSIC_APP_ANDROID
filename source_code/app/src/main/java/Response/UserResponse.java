package Response;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    private Data data;
    private String message;

    public Data getData() {
        return data;
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
