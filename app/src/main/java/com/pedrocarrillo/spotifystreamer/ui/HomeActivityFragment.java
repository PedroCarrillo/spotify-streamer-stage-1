package com.pedrocarrillo.spotifystreamer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.pedrocarrillo.spotifystreamer.R;
import com.pedrocarrillo.spotifystreamer.adapters.ArtistListAdapter;
import com.pedrocarrillo.spotifystreamer.asynctasks.ArtistAsyncTask;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomeActivityFragment extends Fragment implements ArtistAsyncTask.AdapterListener{

    private SearchView svArtist;
    private ListView lvArtist;
    private ProgressBar progressBar;
    private ArtistListAdapter artistListAdapter;
    private ArtistAsyncTask artistAsyncTask;
    public static String QUERY_KEY = "QUERY_KEY";
    public static String ARTIST_ID = "ARTIST_ID";
    public static String ARTIST_NAME = "ARTIST_NAME";

    public HomeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        svArtist = (SearchView)rootView.findViewById(R.id.svArtist);
        lvArtist = (ListView)rootView.findViewById(R.id.lvArtists);
        progressBar = (ProgressBar)rootView.findViewById(R.id.pbLoading);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            String query = savedInstanceState.getString(QUERY_KEY);
            if ( query != null) {
                artistAsyncTask = new ArtistAsyncTask(HomeActivityFragment.this);
                artistAsyncTask.execute(query);
            }
        }
        artistListAdapter = new ArtistListAdapter(new ArrayList<Artist>());
        lvArtist.setAdapter(artistListAdapter);
        svArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    artistAsyncTask = new ArtistAsyncTask(HomeActivityFragment.this);
                    artistAsyncTask.execute(query);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    artistListAdapter.clear();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        lvArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = artistListAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TopTracksActivity.class);
                intent.putExtra(ARTIST_ID, artist.id);
                intent.putExtra(ARTIST_NAME,artist.name);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(QUERY_KEY, svArtist.getQuery().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void updateList(List<Artist> artistList) {
        alertArtistNotFound();
        artistListAdapter.updateData(artistList);
    }

    @Override
    public void clearList(){
        alertArtistNotFound();
        artistListAdapter.clear();
    }

    public void alertArtistNotFound(){
        progressBar.setVisibility(View.GONE);
        if( artistListAdapter.getCount() == 0 && svArtist.getQuery().length() > 0) {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.artist_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void noConnection(){
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
    }
}
