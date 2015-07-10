package com.pedrocarrillo.spotifystreamer.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.pedrocarrillo.spotifystreamer.R;

public class TrackDetailActivity extends BaseActivity {

    TrackDetailFragment trackDetailFragment;
    boolean mIsLargeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);
        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
        int positionTrackSelected = getIntent().getExtras().getInt(TopTracksFragment.TRACK_KEY_POSITION);
        trackDetailFragment = TrackDetailFragment.newInstance(positionTrackSelected);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (mIsLargeLayout) {
            trackDetailFragment.show(fragmentManager, "dialog");
        } else {
            fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, trackDetailFragment)
                .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void serviceReady(){
        if( trackDetailFragment != null ) trackDetailFragment.updateSongDetail();
    }

}
