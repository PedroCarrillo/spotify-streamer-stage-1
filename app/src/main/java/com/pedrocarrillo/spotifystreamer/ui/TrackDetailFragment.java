package com.pedrocarrillo.spotifystreamer.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pedrocarrillo.spotifystreamer.R;
import com.pedrocarrillo.spotifystreamer.entities.Track;
import com.pedrocarrillo.spotifystreamer.services.OnMediaPlayerListener;
import com.pedrocarrillo.spotifystreamer.services.PreviewPlayerService;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackDetailFragment extends Fragment {

    private TextView tvAlbumTitle, tvSongTitle, tvArtistName, tvSongPosition;
    private ImageView ivAlbumImage;
    private Button btnPlay, btnPrevious, btnNext;
    private SeekBar pbSongStatus;

    private int positionTrackSelected;
    private Intent playerServiceIntent;
    private OnMediaPlayerListener onMediaPlayerListener;
    private PlayerReceiver playerReceiver = new PlayerReceiver();

    public static TrackDetailFragment newInstance(int positionTrackSelected){
        TrackDetailFragment fragment = new TrackDetailFragment();
        Bundle args = new Bundle();
        args.putInt(TopTracksFragment.TRACK_KEY_POSITION, positionTrackSelected);
        fragment.setArguments(args);
        return fragment;
    }

    public TrackDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            positionTrackSelected = getArguments().getInt(TopTracksFragment.TRACK_KEY_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_detail, container, false);
        tvAlbumTitle = (TextView)rootView.findViewById(R.id.tvAlbumName);
        tvSongTitle = (TextView)rootView.findViewById(R.id.tvSongTitle);
        tvArtistName = (TextView)rootView.findViewById(R.id.tvAuthor);
        ivAlbumImage = (ImageView)rootView.findViewById(R.id.ivAlbum);
        btnNext = (Button)rootView.findViewById(R.id.btnNext);
        btnPlay = (Button)rootView.findViewById(R.id.btnPlay);
        btnPrevious = (Button)rootView.findViewById(R.id.btnPrevious);
        pbSongStatus = (SeekBar)rootView.findViewById(R.id.pbLoading);
        tvSongPosition = (TextView)rootView.findViewById(R.id.tvSongPosition);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onMediaPlayerListener = (OnMediaPlayerListener)getActivity();
        Intent playerServiceIntent = getNewPlayerIntent();
        playerServiceIntent.setAction(PreviewPlayerService.ACTION_PREPARE_TRACK);
        playerServiceIntent.putExtra(PreviewPlayerService.TRACK_SELECTED_POSITION,positionTrackSelected);
        getActivity().startService(playerServiceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(playerReceiver, new IntentFilter(PreviewPlayerService.ACTION_UPDATE_UI));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(playerReceiver);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void updateSongDetail(){
        final Track trackSelected = onMediaPlayerListener.getTrackSelected();
        boolean sameSong = onMediaPlayerListener.isSameSong();
        PreviewPlayerService.PlayerState playerState= onMediaPlayerListener.getPlayerState();
        if(sameSong && playerState == PreviewPlayerService.PlayerState.STATE_PLAY){
            btnPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
        if(!sameSong){
            pbSongStatus.setMax(0);
            pbSongStatus.setProgress(0);
            tvSongPosition.setText(getString(R.string.time_placeholder));
        }
        if( playerState == PreviewPlayerService.PlayerState.STATE_STOP){
            pbSongStatus.setMax(0);
            pbSongStatus.setProgress(0);
            tvSongPosition.setText(getString(R.string.time_placeholder));
            btnPlay.setBackgroundResource(android.R.drawable.ic_media_play);
        }
        if ( trackSelected != null) {
            tvAlbumTitle.setText(trackSelected.getAlbumTitle());
            tvSongTitle.setText(trackSelected.getName());
            tvArtistName.setText(getActivity().getIntent().getStringExtra(HomeFragment.ARTIST_NAME));
            Picasso.with(getActivity().getApplicationContext()).load(trackSelected.getImageUrl()).placeholder(R.drawable.not_found).into(ivAlbumImage);
            btnPlay.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    PreviewPlayerService.PlayerState currentPlayerState = onMediaPlayerListener.getPlayerState();
                    boolean sameSong = onMediaPlayerListener.isSameSong();
                    playerServiceIntent = getNewPlayerIntent();
                    if (currentPlayerState != PreviewPlayerService.PlayerState.STATE_PLAY) {
                        btnPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
                        if (sameSong && currentPlayerState == PreviewPlayerService.PlayerState.STATE_PAUSE){
                            playerServiceIntent.setAction(PreviewPlayerService.ACTION_UNPAUSE);
                        }else {
                            playerServiceIntent.setAction(PreviewPlayerService.ACTION_PLAY);
                        }
                    } else {
                        if (sameSong && currentPlayerState == PreviewPlayerService.PlayerState.STATE_PLAY) {
                            btnPlay.setBackgroundResource(android.R.drawable.ic_media_play);
                            playerServiceIntent.setAction(PreviewPlayerService.ACTION_PAUSE);
                        }else{
                            btnPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
                            playerServiceIntent.setAction(PreviewPlayerService.ACTION_PLAY);
                        }
                    }
                    startPlayerServiceIntent();
                }
            });
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPlayerAction(PreviewPlayerService.ACTION_NEXT);
                }
            });
            btnPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPlayerAction(PreviewPlayerService.ACTION_PREVIOUS);
                }
            });
        }
    }

    private void startPlayerAction(String action){
        playerServiceIntent = getNewPlayerIntent();
        playerServiceIntent.setAction(action);
        startPlayerServiceIntent();
        updateSongDetail();
    }

    public Intent getNewPlayerIntent(){
        return new Intent(getActivity(), PreviewPlayerService.class);
    }

    public void startPlayerServiceIntent(){
        getActivity().startService(playerServiceIntent);
    }

    public class PlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getExtras() != null) {
                if( intent.hasExtra(PreviewPlayerService.SONG_CHANGED_TAG)) {
                    boolean updateUI = intent.getBooleanExtra(PreviewPlayerService.SONG_CHANGED_TAG, false);
                    if (updateUI) updateSongDetail();
                }else if ( intent.hasExtra(PreviewPlayerService.SONG_ACTUAL_POSITION)) {
                    int seekBarPosition = intent.getIntExtra(PreviewPlayerService.SONG_ACTUAL_POSITION, 0);
                    if( onMediaPlayerListener.isSameSong()) {
                        pbSongStatus.setMax(onMediaPlayerListener.getMediaPlayer().getDuration());
                        pbSongStatus.setProgress(seekBarPosition);
                        tvSongPosition.setText(String.format("%d:%d",
                                TimeUnit.MILLISECONDS.toMinutes(seekBarPosition),
                                TimeUnit.MILLISECONDS.toSeconds(seekBarPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seekBarPosition))
                        ));
                    }
                }
            }
        }
    }

}
