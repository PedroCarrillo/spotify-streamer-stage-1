package com.pedrocarrillo.spotifystreamer.ui;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.pedrocarrillo.spotifystreamer.R;
import com.pedrocarrillo.spotifystreamer.entities.Track;
import com.pedrocarrillo.spotifystreamer.services.OnMediaPlayerListener;
import com.pedrocarrillo.spotifystreamer.services.PreviewPlayerService;

/**
 * Created by Pedro on 29/06/15.
 */
public class BaseActivity extends ActionBarActivity implements OnMediaPlayerListener {

    protected PreviewPlayerService previewPlayerService;
    public boolean mBound = false;
    private PlayerStateReceiver playerStateReceiver = new PlayerStateReceiver();
    public static String HAS_CHANGED_PLAYER_STATE = "has_changed_state";
    public static String ACTION_STATE_PLAYER = "action_state_player";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onStart() {
        super.onStart();
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
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(playerStateReceiver, new IntentFilter(ACTION_STATE_PLAYER));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerStateReceiver);
    }

    @Override
    public Track getTrackSelected() {
        if(mBound) return previewPlayerService.trackList.get(previewPlayerService.getSelectedTrackPosition());
        return null;
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        if(mBound) return previewPlayerService.mediaPlayer;
        return null;
    }

    @Override
    public boolean isSameSong() {
        if(mBound) return previewPlayerService.isSameSong();
        return false;
    }

    @Override
    public PreviewPlayerService.PlayerState getPlayerState() {
        if(mBound) return previewPlayerService.playerState;
        return null;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            PreviewPlayerService.MusicPlayerBinder binder = (PreviewPlayerService.MusicPlayerBinder) service;
            previewPlayerService = binder.getService();
            mBound = true;
            serviceReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    protected void serviceReady(){

    }

    public class PlayerStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getExtras() != null) {
                if(intent.hasExtra(HAS_CHANGED_PLAYER_STATE)){
                    invalidateOptionsMenu();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if( getPlayerState() != PreviewPlayerService.PlayerState.STATE_STOP) {
            menu.findItem(R.id.action_now_playing).setVisible(true);
            MenuItem share = menu.findItem(R.id.action_share);
            share.setVisible(true);
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(share);
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareCurrentTrackIntent());
            }
        }else{
            menu.findItem(R.id.action_now_playing).setVisible(false);
            menu.findItem(R.id.action_share).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public Intent createShareCurrentTrackIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, previewPlayerService.getPlayingTrack().getPreviewUrl());
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_now_playing) {
            boolean mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
            TrackDetailFragment trackDetailFragment = TrackDetailFragment.newInstance(previewPlayerService.getSelectedTrackPosition());
//            trackDetailFragment.showCurentSong();
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (mIsLargeLayout) {
                trackDetailFragment.show(fragmentManager, "dialog");
            } else {
                fragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.container, trackDetailFragment)
                        .addToBackStack(null)
                        .commit();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}