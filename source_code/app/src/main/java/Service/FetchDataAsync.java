package Service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import java.io.IOException;

import constanst.Constanst;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FetchDataAsync extends AsyncTaskLoader<String> {
    private String json = "";
    private String linkAPI = "";
    public FetchDataAsync(@NonNull Context context) {
        super(context);
    }
    public void setJson(String json) {
        this.json = json;
    }
    public void setLinkAPI(String linkAPI) {
        this.linkAPI = linkAPI;
    }

    @Nullable
    @Override
    public String loadInBackground() {
        if( json.trim().equals("") || linkAPI.trim().equals("")) return null;

        RequestBody postBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), json
        );


        Request request = new Request.Builder().url(linkAPI)
                .post(postBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);

        Response response = null;
        try {
            response = call.execute();
            String serverRes = response.body().string();
            Log.i("error", serverRes);
            if (response != null) {
                return serverRes;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deliverResult(String data) {
        super.deliverResult(data);
    }
}
