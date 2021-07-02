package com.screenlocked;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;

import androidx.annotation.Nullable;

public class ScreenLockedModule extends ReactContextBaseJavaModule {

    AudioManager manager;
    private final ReactApplicationContext reactContext;

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    public ScreenLockedModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        manager = (AudioManager) reactContext.getSystemService(Context.AUDIO_SERVICE);
        registerBroadcastReceiver();
    }

    @Override
    public String getName() {
        return "ScreenLocked";
    }

    private final BroadcastReceiver mHeadsetPlugReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WritableMap params = Arguments.createMap();
            String action = "";
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
                action="ACTION_USER_PRESENT";
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                manager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                action="ACTION_SCREEN_OFF";
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                manager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                action="ACTION_SCREEN_ON";
            }
            params.putString("action", action);
            sendEvent(reactContext, "EventReminder", params);

        }
    };

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        reactContext.registerReceiver(mHeadsetPlugReceiver, filter);
    }
}
