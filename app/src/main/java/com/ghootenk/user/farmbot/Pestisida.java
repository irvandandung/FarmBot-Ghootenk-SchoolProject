package com.ghootenk.user.farmbot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ghootenk.user.farmbot.sync.AsyncResult;
import com.ghootenk.user.farmbot.sync.DownloadWebpageTask;
import com.ghootenk.user.farmbot.sync.HTTPAsyncGPIO;

import org.json.JSONException;
import org.json.JSONObject;

import static com.ghootenk.user.farmbot.BuildConfig.DEVICE_IC;
import static com.ghootenk.user.farmbot.BuildConfig.URL_API;

public class Pestisida extends AppCompatActivity {
    private String mURL = URL_API + "/" + DEVICE_IC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pestisida);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //cek status lampu
        cekStatus();

        //cek realtime status lampu
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cekStatus();
                Log.d("MainActivity", "runDelay");
            }
        }, 3000);
    }

    private void cekStatus() {
        new DownloadWebpageTask(new AsyncResult() {
            @Override
            public void onResult(JSONObject object) {
                getStatus(object);
            }
        }).execute( mURL + "/gpio/data");
    }

    private void getStatus(JSONObject object) {
        try {
            JSONObject rows = object.getJSONObject("data");
            JSONObject result = rows.getJSONObject("result");
            String pin_5 = result.getString("5");
            Log.d("coba", pin_5);
            if (pin_5.equals("0")) {
                Toast.makeText(getBaseContext(), "Relay Menyala", Toast.LENGTH_SHORT).show();

            }
            if (pin_5.equals("1")) {
                Toast.makeText(getBaseContext(), "Relay Mati", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getBaseContext(), "Pembacaan status gagal...", Toast.LENGTH_SHORT).show();
//                tombolmati.setVisibility(View.GONE);
//                tombol.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean setRelay(String pin, String status) {
        new HTTPAsyncGPIO(this).execute(mURL + "/gpio/control", pin, status);
        if (pin.equals("5") && status.equals("0")){
//            tombolmati.setVisibility(View.VISIBLE);
//            tombol.setVisibility(View.GONE);
        }
        if (pin.equals("5") && status.equals("1")){
//            tombol.setVisibility(View.VISIBLE);
//            tombolmati.setVisibility(View.GONE);
        }
        return false;
    }

    public void backhome (View view){
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
