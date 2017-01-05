package com.infinitescrolling;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;


/**
 * Created by Android on 2/24/2016.
 */
public class CommonAppcompatActivity extends AppCompatActivity {

    public Animation animation_slide_in_right, animation_top_to_bottom,animation_down_from_top,animation_up_from_bottom;
    public Typeface typeface;
    public ConnectionDetector connectionDetector;
    public boolean isInternetAvailable;
    public ProgressDialog progressDialog;
    private LinearLayout layout;
    private static final String PREF_NAME = "preference";
    private Locale locale;
    protected int screenHeight, screenWidth;
    public Typeface tfDroidSans;
    private Dialog customLoading;
    public long MIN_TIME_INTERVAL = 60 * 1000L;
    private SharedPreferences preferences;
    private Button send_button;
    private int year;
    private int month;
    private int day, pickerId, pos;
    private EditText editText,write_comment;
    Context mContext;
    DatePickerDialog datePickerDialog;


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionDetector = new ConnectionDetector(getApplicationContext());
        isInternetAvailable = getNetworkState().isConnectingToInternet();
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            switch (msg.what) {

                case 1:
                    layout.setVisibility(View.GONE);
                    break;

                case 2:
                    layout.setVisibility(View.GONE);
                    break;
            }
        }
    };



    public void hideKeyBoard(Context context) {
        if (getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        /*inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);*/
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }

    }

    public void showUserAlert(LinearLayout linearLayout, TextView txt_title, TextView txt_close, String title, String close) {
        this.layout = linearLayout;
        layout.setVisibility(View.VISIBLE);
        txt_title.setText(title);
        txt_close.setText(close);
        handler.sendEmptyMessageDelayed(1, 2000);

    }


    public boolean isMobileNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            return true;
        } else {
            return false;
        }
    }


    public void showInternetAlert(LinearLayout linearLayout, TextView txt_title, TextView txt_close) {
        this.layout = linearLayout;
        layout.setVisibility(View.VISIBLE);
        txt_title.setText("Kindly check your internet connection and try again...");
        txt_close.setText("Close");
        handler.sendEmptyMessageDelayed(2, 3000);

    }

    public boolean Communication() {
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        switch (manager.getSimState()) {
            case TelephonyManager.SIM_STATE_ABSENT:
                return false;
            case TelephonyManager.SIM_STATE_READY:
                return true;
            default:
                break;

        }
        return false;
    }

    public ConnectionDetector getNetworkState() {
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        return connectionDetector;
    }

    public void showProgressDialog(Context context) {
        progressDialog = new ProgressDialog(context);
        //progressDialog.setTitle("MyAPP");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}