package com.pedrocarrillo.spotifystreamer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pedrocarrillo.spotifystreamer.R;
import com.pedrocarrillo.spotifystreamer.entities.Track;
import com.pedrocarrillo.spotifystreamer.services.OnMediaPlayerListener;
import com.pedrocarrillo.spotifystreamer.services.PreviewPlayerService;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackDetailFragment extends Fragment {

    private TextView tvAlbumTitle, tvSongTitle, tvArtistName;
    private ImageView ivAlbumImage;
    private Button btnPlay, btnPrevious, btnNext;
    private ProgressBar pbSongStatus;
    private int positionTrackSelected;
    private OnMediaPlayerListener onMediaPlayerListener;

    public TrackDetailFragment() {
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
        pbSongStatus = (ProgressBar)rootView.findViewById(R.id.pbLoading);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onMediaPlayerListener = (OnMediaPlayerListener)getActivity();
        positionTrackSelected = getActivity().getIntent().getExtras().getInt(TopTracksFragment.TRACK_KEY_POSITION);
        Intent playerServiceIntent = new Intent(getActivity(),PreviewPlayerService.class);
        playerServiceIntent.setAction(PreviewPlayerService.ACTION_PREPARE_TRACK);
        playerServiceIntent.putExtra(PreviewPlayerService.TRACK_SELECTED_POSITION,positionTrackSelected);
        getActivity().startService(playerServiceIntent);
    }



    public void updateSongDetail(){
        final Track trackSelected = onMediaPlayerListener.getTrackSelected();
        if ( trackSelected != null) {
            tvAlbumTitle.setText(trackSelected.getAlbumTitle());
            tvSongTitle.setText(trackSelected.getName());
            tvArtistName.setText(getActivity().getIntent().getStringExtra(HomeFragment.ARTIST_NAME));
            Picasso.with(getActivity().getApplicationContext()).load(trackSelected.getImageUrl()).placeholder(R.drawable.not_found).into(ivAlbumImage);
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent playerServiceIntent = new Intent(getActivity(), PreviewPlayerService.class);
                    if (!onMediaPlayerListener.getMediaPlayer().isPlaying()) {
                        playerServiceIntent.setAction(PreviewPlayerService.ACTION_PLAY);
                    } else {
                        playerServiceIntent.setAction(PreviewPlayerService.ACTION_PAUSE);
                    }
                    getActivity().startService(playerServiceIntent);
                }
            });
        }
    }

}
