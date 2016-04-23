package com.example.florine.testmessage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks{


    private static String WEAR_MESSAGE_PATH = "/message";

    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        if(mApiClient != null && !(mApiClient.isConnected() || mApiClient.isConnecting())){
            mApiClient.connect();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mApiClient, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)){
                    System.out.println(messageEvent.getPath().toString());
                    String s = new String(messageEvent.getData());
                    System.out.println(s);
                    if(s.equalsIgnoreCase("vibrante")){
                        alarmeVibrante();
                    }
                    if(s.equalsIgnoreCase("visuelle")){
                        alarmeVisuelle();
                    }
                    if(s.equalsIgnoreCase("toutes")){
                        toutesAlarmes();
                    }

                }
            }
        });
    }

    public void alarmeVibrante(){
        System.out.println("je vibre");
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
    }

    public void alarmeVisuelle(){
        setContentView(R.layout.warning);
    }

    public void toutesAlarmes(){
        alarmeVibrante();
        alarmeVisuelle();
    }

    public void changeLayout(View view) {
        setContentView(R.layout.activity_main);
    }
}
