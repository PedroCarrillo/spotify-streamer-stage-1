package com.pedrocarrillo.spotifystreamer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pedrocarrillo.spotifystreamer.R;
import com.pedrocarrillo.spotifystreamer.adapters.TrackListAdapter;
import com.pedrocarrillo.spotifystreamer.asynctasks.TopTracksAsyncTask;
import com.pedrocarrillo.spotifystreamer.entities.Track;
import com.pedrocarrillo.spotifystreamer.services.PreviewPlayerService;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksFragment extends Fragment implements TopTracksAsyncTask.TrackAdapterListener{

    private ListView lvTopTracks;
    private TrackListAdapter trackListAdapter;
    private ProgressBar progressBar;
    public static String ARTIST_NAME_KEY = "ARTIST_NAME";
    public static String TRACK_KEY_POSITION = "TRACK_KEY_POSITION";

    public static String TRACK_LIST_KEY = "TRACK_LIST_KEY";

    TrackDetailFragment trackDetailFragment;
    boolean mIsLargeLayout;

    public TopTracksFragment() {
    }

    public static TopTracksFragment newInstance(String artistId, String artistName){
        TopTracksFragment fragment = new TopTracksFragment();
        Bundle args = new Bundle();
        args.putString(HomeFragment.ARTIST_ID, artistId);
        args.putString(HomeFragment.ARTIST_NAME, artistName);
        fragment.setArguments(args);
        return fragment;
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
        final String artistId, artistName;
        ArrayList<Track> tracksArrayList;
        if( savedInstanceState == null){
            tracksArrayList = new ArrayList<Track>();
            if(getArguments() != null) {
                artistId = getArguments().getString(HomeFragment.ARTIST_ID);
                artistName = getArguments().getString(HomeFragment.ARTIST_NAME);
                progressBar.setVisibility(View.VISIBLE);
                TopTracksAsyncTask topTracksAsyncTask = new TopTracksAsyncTask(this);
                topTracksAsyncTask.execute(artistId);
            }
        }else {
            artistName = savedInstanceState.getString(ARTIST_NAME_KEY);
            tracksArrayList = savedInstanceState.getParcelableArrayList(TRACK_LIST_KEY);
        }
        trackListAdapter = new TrackListAdapter(tracksArrayList);

        lvTopTracks.setAdapter(trackListAdapter);
        lvTopTracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(),TrackDetailActivity.class);
//                intent.putExtra(HomeFragment.ARTIST_NAME,artistName);
//                intent.putExtra(HomeFragment.LIST_ARTIST_KEY,trackListAdapter.getTrackList());
//                intent.putExtra(TRACK_KEY_POSITION,position);
//                startActivity(intent);
                showTrackDetail(position);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(TRACK_LIST_KEY, trackListAdapter.getTrackList());
        outState.putString(ARTIST_NAME_KEY, getActivity().getIntent().getStringExtra(HomeFragment.ARTIST_NAME));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void updateList(List<Track> tracksList) {
        progressBar.setVisibility(View.GONE);
        trackListAdapter.updateData(tracksList);
        Intent playerServiceIntent = new Intent(getActivity(),PreviewPlayerService.class);
        playerServiceIntent.setAction(PreviewPlayerService.ACTION_INIT);
        playerServiceIntent.putExtra(PreviewPlayerService.TRACK_LIST, new ArrayList<>(tracksList));
        getActivity().startService(playerServiceIntent);
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

    public void showTrackDetail(int positionTrackSelected){
        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
        trackDetailFragment = TrackDetailFragment.newInstance(positionTrackSelected);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
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

    }
}
