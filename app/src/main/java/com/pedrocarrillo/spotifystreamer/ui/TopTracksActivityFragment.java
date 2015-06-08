package com.pedrocarrillo.spotifystreamer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pedrocarrillo.spotifystreamer.R;
import com.pedrocarrillo.spotifystreamer.adapters.TrackListAdapter;
import com.pedrocarrillo.spotifystreamer.asynctasks.TopTracksAsyncTask;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment implements TopTracksAsyncTask.TrackAdapterListener{

    private ListView lvTopTracks;
    private TrackListAdapter trackListAdapter;
    private ProgressBar progressBar;
    public static String ARTIST_NAME_KEY = "ARTIST_NAME";
    public static String ARTIST_ID_KEY = "ARTIST_ID";

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        lvTopTracks = (ListView)rootView.findViewById(R.id.lvTopTracks);
        progressBar = (ProgressBar)rootView.findViewById(R.id.pbLoading);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        trackListAdapter = new TrackListAdapter(new ArrayList<Track>());
        lvTopTracks.setAdapter(trackListAdapter);
        String artistId, artistName;
        if( savedInstanceState == null){
            artistId = getActivity().getIntent().getStringExtra(HomeActivityFragment.ARTIST_ID);
            artistName = getActivity().getIntent().getStringExtra(HomeActivityFragment.ARTIST_NAME);
        }else {
            artistId = savedInstanceState.getString(ARTIST_ID_KEY);
            artistName = savedInstanceState.getString(ARTIST_NAME_KEY);
        }
        ((TopTracksActivity)getActivity()).getSupportActionBar().setSubtitle(artistName);
        progressBar.setVisibility(View.VISIBLE);
        TopTracksAsyncTask topTracksAsyncTask = new TopTracksAsyncTask(this);
        topTracksAsyncTask.execute(artistId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARTIST_ID_KEY, getActivity().getIntent().getStringExtra(HomeActivityFragment.ARTIST_ID));
        outState.putString(ARTIST_NAME_KEY, getActivity().getIntent().getStringExtra(HomeActivityFragment.ARTIST_NAME));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void updateList(List<Track> tracksList) {
        progressBar.setVisibility(View.GONE);
        trackListAdapter.updateData(tracksList);
    }

    @Override
    public void clearList() {
        progressBar.setVisibility(View.GONE);
        trackListAdapter.clear();
    }

    @Override
    public void errorConnection(){
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
    }
}
