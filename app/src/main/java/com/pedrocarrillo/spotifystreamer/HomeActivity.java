package com.pedrocarrillo.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;

import com.pedrocarrillo.spotifystreamer.ui.BaseActivity;
import com.pedrocarrillo.spotifystreamer.ui.HomeFragment;
import com.pedrocarrillo.spotifystreamer.ui.TopTracksActivity;
import com.pedrocarrillo.spotifystreamer.ui.TopTracksFragment;


public class HomeActivity extends BaseActivity implements HomeFragment.OnArtistListener{

    private boolean mTwoPane;
    public static String TOP_TRACKS_FRG_TAG = "toptracksfrag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (findViewById(R.id.container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new TopTracksFragment(), TOP_TRACKS_FRG_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        setTitle(R.string.app_name);
    }

    @Override
    public void serviceReady(){
        invalidateOptionsMenu();
    }

    @Override
    public void onArtistItemClick(String artistId, String artistName) {
        if(mTwoPane){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TopTracksFragment.newInstance(artistId,artistName), TOP_TRACKS_FRG_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(HomeActivity.this, TopTracksActivity.class);
            intent.putExtra(HomeFragment.ARTIST_ID, artistId);
            intent.putExtra(HomeFragment.ARTIST_NAME, artistName);
            startActivity(intent);
        }
    }
}
