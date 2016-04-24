package com.example.florine.testmessage;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by florine on 4/24/16.
 */
public class PhoneMessageListenerService extends WearableListenerService {

    private static final String START_ACTIVITY = "/start_activity";

    @Override
    public void onMessageReceived(MessageEvent messageEvent){
        if(messageEvent.getPath().equalsIgnoreCase(START_ACTIVITY)){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else{
            super.onMessageReceived(messageEvent);
        }
    }
}
