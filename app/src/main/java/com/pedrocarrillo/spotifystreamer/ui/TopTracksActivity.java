package com.pedrocarrillo.spotifystreamer.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.pedrocarrillo.spotifystreamer.R;

public class TopTracksActivity extends BaseActivity {

    TopTracksFragment topTracksFragment;
    TrackDetailFragment trackDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);
        String artistId = getIntent().getStringExtra(HomeFragment.ARTIST_ID);
        String artistName = getIntent().getStringExtra(HomeFragment.ARTIST_NAME);
        getSupportActionBar().setSubtitle(artistName);
        topTracksFragment = TopTracksFragment.newInstance(artistId, artistName);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.container, topTracksFragment)
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
