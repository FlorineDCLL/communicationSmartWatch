package com.example.florine.testmessage;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Timer;

public class MainActivity extends Activity implements MessageApi.MessageListener,GoogleApiClient.ConnectionCallbacks{

    private final static String START_ACTIVITY = "/start/activity";
    private final static String WEAR_MESSAGE_PATH = "/message";
    private MediaPlayer mp;
    private GoogleApiClient mApiTelephone, mApiWear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApiTelephone= new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
            mApiTelephone.connect();

        mApiWear= new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
            mApiWear.connect();

        mp = MediaPlayer.create(this, R.raw.sound);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mApiTelephone.disconnect();
    }

    private void sendMessage(final String path, final String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiTelephone).await();
                for(Node node : nodes.getNodes()){
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiTelephone, node.getId(), path, message.getBytes()).await();
                }
            }
        }).start();
    }

    @Override
    public void onConnected(Bundle bundle) {
        sendMessage(START_ACTIVITY, "");
        Wearable.MessageApi.addListener(mApiWear, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void toutesAlarmes(View view){
        System.out.println("toutes alarmes");
        sendMessage(WEAR_MESSAGE_PATH, "toutes");
        mp.start();
        int i = 0;
        while(i<1000000000){
            i++;
        }
        mp.stop();
    }

    public void alarmeVisuelle(View view){
        System.out.println("alarme visuelle");
        sendMessage(WEAR_MESSAGE_PATH,"visuelle");
    }

    public void alarmeVibrante(View view){
        System.out.println("alarme vibrante");
        sendMessage(WEAR_MESSAGE_PATH, "vibrante");
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        System.out.println("message received");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(messageEvent.getPath().equalsIgnoreCase(WEAR_MESSAGE_PATH)){
                    System.out.println(messageEvent.getPath().toString());
                    String s = new String(messageEvent.getData());
                    System.out.println(s);

                }
            }
        });
    }
}
