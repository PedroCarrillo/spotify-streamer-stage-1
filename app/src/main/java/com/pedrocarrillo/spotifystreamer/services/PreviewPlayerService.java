package com.pedrocarrillo.spotifystreamer.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.pedrocarrillo.spotifystreamer.HomeActivity;
import com.pedrocarrillo.spotifystreamer.R;
import com.pedrocarrillo.spotifystreamer.entities.Track;
import com.pedrocarrillo.spotifystreamer.ui.BaseActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
    public static String ACTION_UNPAUSE = "action_unpause";
    public static String ACTION_NEXT = "action_next";
    public static String ACTION_PREVIOUS = "action_previous";
    public static String SONG_ACTUAL_POSITION = "song_actual_pos";
    private static int NOTIFICATION_ID = 1;

    public MediaPlayer mediaPlayer = null;
    private WifiManager.WifiLock wifiLock;
    private final IBinder mBinder = new MusicPlayerBinder();
    private Integer selectedTrackPosition;
    private Track trackSelected, playingTrack;
    public List<Track> trackList;

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

    public Track getPlayingTrack(){
        return playingTrack;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String playerAction = intent.getAction();
        if(playerAction.equalsIgnoreCase(ACTION_INIT)){
            trackList = intent.getParcelableArrayListExtra(TRACK_LIST);
        }else if(playerAction.equalsIgnoreCase(ACTION_PREPARE_TRACK)){
            selectedTrackPosition = intent.getIntExtra(TRACK_SELECTED_POSITION,0);
            prepareTrack();
            notifySongChange();
        }else if( playerAction.equalsIgnoreCase(ACTION_PLAY)) {
            playTrack();
        }else if( playerAction.equalsIgnoreCase(ACTION_PAUSE)) {
            startNotification();
            setPlayerState(PlayerState.STATE_PAUSE);
            mediaPlayer.pause();
        }else if( playerAction.equalsIgnoreCase(ACTION_UNPAUSE)) {
            setPlayerState(PlayerState.STATE_PLAY);
            mediaPlayer.start();
            startNotification();
        }else if( playerAction.equalsIgnoreCase(ACTION_NEXT)){
            startNextSong();
            startNotification();
        }else if( playerAction.equalsIgnoreCase(ACTION_PREVIOUS)) {
            startPreviousSong();
            startNotification();
        }
        setupHandler();
        return START_STICKY;
    }

    private void setPlayerState(PlayerState playerState){
        this.playerState = playerState;
        notifyPlayerStateChange();
    }

    private void prepareTrack(){
        trackSelected = trackList.get(selectedTrackPosition);
    }

    private void playTrack(){
//        if( isSameSong() )
        preparePlayer();
        setPlayerState(PlayerState.STATE_PLAY);
        startNotification();
    }

    private void startNextSong(){
        selectedTrackPosition++;
        if(selectedTrackPosition >= trackList.size()){
            selectedTrackPosition = 0;
        }
        prepareTrack();
        preparePlayer();
        notifySongChange();
    }

    private void startPreviousSong(){
        selectedTrackPosition--;
        if (selectedTrackPosition < 0) {
            selectedTrackPosition = trackList.size()-1;
        }
        prepareTrack();
        preparePlayer();
        notifySongChange();
    }

    public void setupHandler(){
        handler.removeCallbacks(sendUpdatesTrackUI);
        handler.postDelayed(sendUpdatesTrackUI,0);
    }

    private Runnable sendUpdatesTrackUI = new Runnable() {
        @Override
        public void run() {
            notifyTrackPosition();
            handler.postDelayed(sendUpdatesTrackUI,0);
        }
    };

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

    private void notifyPlayerStateChange(){
        Intent intent = new Intent();
        intent.putExtra(BaseActivity.HAS_CHANGED_PLAYER_STATE,true);
        intent.setAction(BaseActivity.ACTION_STATE_PLAYER);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent) ;
    }

    Bitmap bigAlbumThump;
    Drawable smallAlbumThump;
    private Target target = new Target() {
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bigAlbumThump = bitmap;
        }
    };

    public void startNotification(){
        Picasso.with(getApplicationContext()).load(playingTrack.getImageUrl()).into(target);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0,
        new Intent(getApplicationContext(), HomeActivity.class),
        PendingIntent.FLAG_UPDATE_CURRENT);
        Intent playerServiceIntent = new Intent(getApplicationContext(), PreviewPlayerService.class);
        playerServiceIntent.setAction(ACTION_PREVIOUS);
        PendingIntent previousIntent = PendingIntent.getService(getApplicationContext(), 0, playerServiceIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);
        if(playerState == PlayerState.STATE_PLAY) {
            playerServiceIntent.setAction(ACTION_PAUSE);
        }else{
            playerServiceIntent.setAction(ACTION_PLAY);
        }
        PendingIntent playpauseIntent = PendingIntent.getService(getApplicationContext(), 0, playerServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        playerServiceIntent.setAction(ACTION_NEXT);
        PendingIntent nextIntent = PendingIntent.getService(getApplicationContext(), 0,
                playerServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificationOnLockScreen = sharedPreferences.getBoolean(getResources().getString(R.string.notifications_lock_screen_key), false);
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this);
        notiBuilder
            .setContentTitle("Now Playing")
            .setStyle(new NotificationCompat.MediaStyle())
//                .setShowActionsInCompactView(1)
//                .setMediaSession(mediaPlayer.getSessionToken())
            .setContentText(playingTrack.getName())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(bigAlbumThump)
            .setContentIntent(pIntent)
            .setAutoCancel(true)
//                        .setWhen(0)
            .addAction(android.R.drawable.ic_media_previous, "", previousIntent);
        if (notificationOnLockScreen) notiBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        if(playerState == PlayerState.STATE_PLAY) {
            notiBuilder.addAction(android.R.drawable.ic_media_pause, "", playpauseIntent);
        }else{
            notiBuilder.addAction(android.R.drawable.ic_media_play, "", playpauseIntent);
        }
        notiBuilder.addAction(android.R.drawable.ic_media_next, "", nextIntent).build();
        Notification n  = notiBuilder.build();

        n.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_ID, n);
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
        setPlayerState(PlayerState.STATE_STOP);
        notifySongChange();
    }

    public boolean isSameSong(){
        return playingTrack != null && trackSelected.equals(playingTrack);
    }

    public void preparePlayer(){
        playingTrack = trackSelected;
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
        return false;
    }

    public class MusicPlayerBinder extends Binder {
        public PreviewPlayerService getService(){
            return PreviewPlayerService.this;
         }
    }

}
