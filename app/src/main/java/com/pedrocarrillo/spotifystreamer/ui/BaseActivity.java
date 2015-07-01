package com.pedrocarrillo.spotifystreamer.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;

import com.pedrocarrillo.spotifystreamer.entities.Track;
import com.pedrocarrillo.spotifystreamer.services.OnMediaPlayerListener;
import com.pedrocarrillo.spotifystreamer.services.PreviewPlayerService;

/**
 * Created by Pedro on 29/06/15.
 */
public class BaseActivity extends ActionBarActivity implements OnMediaPlayerListener {

    public PreviewPlayerService previewPlayerService;
    public boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, PreviewPlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public Track getTrackSelected() {
        if(mBound) return previewPlayerService.trackList.get(previewPlayerService.selectedTrackPosition);
        return null;
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        if(mBound) return previewPlayerService.mediaPlayer;
        return null;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PreviewPlayerService.MusicPlayerBinder binder = (PreviewPlayerService.MusicPlayerBinder) service;
            previewPlayerService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}