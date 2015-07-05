package com.pedrocarrillo.spotifystreamer.ui;

import android.os.Bundle;

import com.pedrocarrillo.spotifystreamer.R;

public class TrackDetailActivity extends BaseActivity {

    TrackDetailFragment trackDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_detail);
        int positionTrackSelected = getIntent().getExtras().getInt(TopTracksFragment.TRACK_KEY_POSITION);
        trackDetailFragment = TrackDetailFragment.newInstance(positionTrackSelected);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.container, trackDetailFragment)
            .commit();
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
