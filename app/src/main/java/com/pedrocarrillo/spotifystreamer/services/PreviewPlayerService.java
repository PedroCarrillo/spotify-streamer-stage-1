package com.pedrocarrillo.spotifystreamer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import com.pedrocarrillo.spotifystreamer.HomeActivity;
import com.pedrocarrillo.spotifystreamer.entities.Track;

import java.io.IOException;
import java.util.List;

/**
 * Created by Pedro on 27/06/15.
 */
public class PreviewPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{

    public static String TRACK_SELECTED_POSITION = "selected_position";
    public static String TRACK_LIST = "track_list";
    public static String WIFI_LOCK = "wifiLock";
    public static String ACTION_INIT = "action_init";
    public static String ACTION_PREPARE_TRACK = "action_prepare";
    public static String ACTION_PLAY = "action_play";
    public static String ACTION_PAUSE = "action_pause";
    public static String ACTION_STOP = "action_stop";
    public static String ACTION_NEXT = "action_next";
    public static String ACTION_PREVIOUS = "action_previous";
    private static int NOTIFICATION_ID = 1;

    public MediaPlayer mediaPlayer = null;
    private WifiManager.WifiLock wifiLock;
    private final IBinder mBinder = new MusicPlayerBinder();
    public int selectedTrackPosition;
    public List<Track> trackList;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String playerAction = intent.getAction();
        wifiLock = ((WifiManager)getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK);
        wifiLock.acquire();
        if(playerAction.equalsIgnoreCase(ACTION_INIT)){
            trackList = intent.getParcelableArrayListExtra(TRACK_LIST);
        }else if(playerAction.equalsIgnoreCase(ACTION_PREPARE_TRACK)){
            selectedTrackPosition = intent.getIntExtra(TRACK_SELECTED_POSITION,0);
            preparePlayer();
        }else if( playerAction.equalsIgnoreCase(ACTION_PLAY)) {
            startNotification();
            mediaPlayer.start();
        }else if( playerAction.equalsIgnoreCase(ACTION_PAUSE)) {
            mediaPlayer.pause();
        }else if( playerAction.equalsIgnoreCase(ACTION_STOP)){
            mediaPlayer.stop();
        }else if( playerAction.equalsIgnoreCase(ACTION_NEXT)){
            selectedTrackPosition++;
            if(selectedTrackPosition >= trackList.size()){
                selectedTrackPosition = 0;
            }
            preparePlayer();
        }else if( playerAction.equalsIgnoreCase(ACTION_PREVIOUS)) {
            selectedTrackPosition--;
            if (selectedTrackPosition <= 0) {
                selectedTrackPosition = trackList.size();
            }
            preparePlayer();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    public void startNotification(){

        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), HomeActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification();
        notification.tickerText = trackList.get(selectedTrackPosition).getName();
//            notification.icon = R.drawable.play0;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(getApplicationContext(), "MusicPlayerSample",
                "Playing: " + trackList.get(selectedTrackPosition).getName(), pi);

        startForeground(NOTIFICATION_ID, notification);
    }
    public void preparePlayer(){
        Track trackSelected = trackList.get(selectedTrackPosition);
        String url = trackSelected.getPreviewUrl();
        if( mediaPlayer == null ){
            mediaPlayer = new MediaPlayer();
        }else{
            mediaPlayer.reset();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
         } catch (IOException e) {
            Toast.makeText(this, "Song couldn't be found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onDestroy() {
        if(wifiLock != null) wifiLock.release();
        if (mediaPlayer != null) mediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MusicPlayerBinder extends Binder {
        public PreviewPlayerService getService(){
            return PreviewPlayerService.this;
        }
    }

}
