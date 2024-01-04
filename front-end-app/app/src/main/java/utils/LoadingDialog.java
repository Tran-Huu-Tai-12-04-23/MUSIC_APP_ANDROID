package utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.example.musicplayer.R;


public class LoadingDialog {
    private Activity activity;
    private AlertDialog alertDialog;
    private Handler handler;

    public LoadingDialog(Activity activity){
        this.activity = activity;
        this.handler = new Handler();
    }


    public void startLoading(){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.loadingDialogStyle);
            View view = LayoutInflater.from(activity).inflate(R.layout.loading_layout, null, false);
            builder.setView(view);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.show();
    }


    public void stopLoading() {
        if(alertDialog != null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

    public void destroyDialog() {
        if (alertDialog != null ) {
            alertDialog.dismiss();
        }
    }



}
