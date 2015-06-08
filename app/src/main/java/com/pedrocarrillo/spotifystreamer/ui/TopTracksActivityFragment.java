package com.pedrocarrillo.spotifystreamer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        lvTopTracks = (ListView)rootView.findViewById(R.id.lvTopTracks);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        trackListAdapter = new TrackListAdapter(new ArrayList<Track>());
        lvTopTracks.setAdapter(trackListAdapter);
        String artistId = getActivity().getIntent().getStringExtra(HomeActivityFragment.ARTIST_ID);
        String artistName = getActivity().getIntent().getStringExtra(HomeActivityFragment.ARTIST_NAME);
        ((TopTracksActivity)getActivity()).getSupportActionBar().setSubtitle(artistName);
        TopTracksAsyncTask topTracksAsyncTask = new TopTracksAsyncTask(this);
        topTracksAsyncTask.execute(artistId);
    }

    @Override
    public void updateList(List<Track> tracksList) {
        trackListAdapter.updateData(tracksList);
    }

    @Override
    public void clearList() {
        trackListAdapter.clear();
    }
}
