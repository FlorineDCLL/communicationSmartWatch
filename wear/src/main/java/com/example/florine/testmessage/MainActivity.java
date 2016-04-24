package com.example.florine.testmessage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
public class MainActivity extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks{


    private static String WEAR_MESSAGE_PATH = "/message";
    private final static String START_ACTIVITY = "/start/activity";

    private GoogleApiClient mApiTelephone, mApiWear;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        mApiTelephone= new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
            mApiTelephone.connect();

        mApiWear= new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
            mApiWear.connect();

    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public void onConnected(Bundle bundle) {
        sendMessage(START_ACTIVITY, "");
        Wearable.MessageApi.addListener(mApiTelephone, this);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mApiWear.disconnect();
    }

    private void sendMessage(final String path, final String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiWear).await();
                for(Node node : nodes.getNodes()){
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiWear, node.getId(), path, message.getBytes()).await();
                }
            }
        }).start();
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

    public void sonOn(View view){
        sendMessage(WEAR_MESSAGE_PATH,"allumer");
    }

    public void sonOff(View view){
        System.out.println("son off");
        sendMessage(WEAR_MESSAGE_PATH, "eteindre");
    }
}
