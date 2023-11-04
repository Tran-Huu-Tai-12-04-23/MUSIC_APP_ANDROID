package Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SendRequest {
    OkHttpClient client;
    String url;
    Request request;


    public SendRequest(String url) {
        this.url = url;
        client = new OkHttpClient();
        request = new Request.Builder()
                .url(url)
                .build();

    }

    public String get() {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                return responseBody != null ? responseBody.string() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method for sending a POST request
    public String post(RequestBody requestBody) {
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                return responseBody != null ? responseBody.string() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method for sending a PUT request
    public String put(RequestBody requestBody) {
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                return responseBody != null ? responseBody.string() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method for sending a DELETE request
    public String delete() {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                return responseBody != null ? responseBody.string() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
