package com.pedrocarrillo.spotifystreamer.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.pedrocarrillo.spotifystreamer.HomeActivity;
import com.pedrocarrillo.spotifystreamer.entities.Track;

import java.io.IOException;
import java.util.List;

/**
 * Created by Pedro on 27/06/15.
 */
public class PreviewPlayerService extends Service implements MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{

    public static String TRACK_SELECTED_POSITION = "selected_position";
    public static String TRACK_LIST = "track_list";
    public static String WIFI_LOCK = "wifiLock";

    public enum PlayerState{
        STATE_PLAY, STATE_PAUSE, STATE_STOP
    }

    public PlayerState playerState = PlayerState.STATE_STOP;

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
    private Integer selectedTrackPosition;
    private Track trackSelected;
    public List<Track> trackList;
    public boolean sameSong = false;

    public static String ACTION_UPDATE_UI = "action_update_ui";
    public static String SONG_CHANGED_TAG = "song_changed";
    public static String SONG_POSITION_UPDATE = "song_position_update";
    private final Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public void onDestroy() {
        if(wifiLock != null) wifiLock.release();
        if (mediaPlayer != null) mediaPlayer.release();
        handler.removeCallbacks(sendUpdatesTrackUI);
        cancelNotification();
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(playerState == PlayerState.STATE_PLAY) mp.start();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    public Integer getSelectedTrackPosition() {
        return selectedTrackPosition;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String playerAction = intent.getAction();
        playerState.toString();
        if(playerAction.equalsIgnoreCase(ACTION_INIT)){
            trackList = intent.getParcelableArrayListExtra(TRACK_LIST);
        }else if(playerAction.equalsIgnoreCase(ACTION_PREPARE_TRACK)){
            selectedTrackPosition = intent.getIntExtra(TRACK_SELECTED_POSITION,0);
            prepareTrack();
        }else if( playerAction.equalsIgnoreCase(ACTION_PLAY)) {
            playTrack();
        }else if( playerAction.equalsIgnoreCase(ACTION_PAUSE)) {
            playerState = PlayerState.STATE_PAUSE;
            mediaPlayer.pause();
        }else if( playerAction.equalsIgnoreCase(ACTION_NEXT)){
            startNextSong();
        }else if( playerAction.equalsIgnoreCase(ACTION_PREVIOUS)) {
            startPreviousSong();
        }
//        setupHandler();
        return START_STICKY;
    }

    private void prepareTrack(){
        Track newTrackSelected = trackList.get(selectedTrackPosition);
        if ( trackSelected != null ) sameSong = (trackSelected == newTrackSelected);
        trackSelected = newTrackSelected;
//        preparePlayer();
    }

    private void playTrack(){
//        playerState = PlayerState.STATE_PLAY;
//        if(playerState == PlayerState.STATE_PLAY) {
//            preparePlayer();
//        } else{
//            mediaPlayer.start();
//        }
        preparePlayer();
        playerState = PlayerState.STATE_PLAY;
        startNotification();
    }

    private void startNextSong(){
        selectedTrackPosition++;
        if(selectedTrackPosition >= trackList.size()){
            selectedTrackPosition = 0;
        }
        preparePlayer();
        notifySongChange();
    }

    private void startPreviousSong(){
        selectedTrackPosition--;
        if (selectedTrackPosition < 0) {
            selectedTrackPosition = trackList.size()-1;
        }
        preparePlayer();
        notifySongChange();
    }

    public void setupHandler(){
        handler.removeCallbacks(sendUpdatesTrackUI);
        handler.postDelayed(sendUpdatesTrackUI,1000);
    }

    private Runnable sendUpdatesTrackUI = new Runnable() {
        @Override
        public void run() {
            notifyTrackPosition();
            handler.postDelayed(sendUpdatesTrackUI,1000);
        }
    };

    public static String SONG_ACTUAL_POSITION = "song_actual_pos";
    private void notifyTrackPosition(){
        if(mediaPlayer.isPlaying()) {
            Intent intent = new Intent();
            intent.putExtra(SONG_POSITION_UPDATE, true);
            intent.putExtra(SONG_ACTUAL_POSITION, mediaPlayer.getCurrentPosition());
            intent.putExtra(SONG_ACTUAL_POSITION, mediaPlayer.getCurrentPosition());
            intent.setAction(ACTION_UPDATE_UI);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void notifySongChange(){
        Intent intent = new Intent();
        intent.putExtra(SONG_CHANGED_TAG,true);
        intent.setAction(ACTION_UPDATE_UI);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent) ;
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


    private void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void initMusicPlayer(){
        wifiLock = ((WifiManager)getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK);
        wifiLock.acquire();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playerState = PlayerState.STATE_STOP;
        notifySongChange();
    }

    public void preparePlayer(){
        Track trackSelected = trackList.get(selectedTrackPosition);
        String url = trackSelected.getPreviewUrl();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
         } catch (IOException e) {
            Toast.makeText(this, "Song couldn't be found", Toast.LENGTH_SHORT).show();
        }
        mediaPlayer.prepareAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    public class MusicPlayerBinder extends Binder {
        public PreviewPlayerService getService(){
            return PreviewPlayerService.this;
         }
    }

}