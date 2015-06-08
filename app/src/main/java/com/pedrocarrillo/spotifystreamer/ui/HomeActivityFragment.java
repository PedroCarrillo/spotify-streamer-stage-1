package com.pedrocarrillo.spotifystreamer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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

    private EditText etArtist;
    private ListView lvArtist;
    private ArtistListAdapter artistListAdapter;
    private ArtistAsyncTask artistAsyncTask;
    public static String ARTIST_ID = "ARTIST_ID";
    public static String ARTIST_NAME = "ARTIST_NAME";

    public HomeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        etArtist = (EditText)rootView.findViewById(R.id.svArtist);
        lvArtist = (ListView)rootView.findViewById(R.id.lvArtists);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        artistListAdapter = new ArtistListAdapter(new ArrayList<Artist>());
        lvArtist.setAdapter(artistListAdapter);
//        etArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (!newText.isEmpty()) {
//                    artistAsyncTask = new ArtistAsyncTask(HomeActivityFragment.this);
//                    artistAsyncTask.execute(newText);
//                } else {
//                    artistListAdapter.clear();
//                }
//                return false;
//            }
//        });
        etArtist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = s.toString();
                if (!newText.isEmpty()) {
                    artistAsyncTask = new ArtistAsyncTask(HomeActivityFragment.this);
                    artistAsyncTask.execute(newText);
                } else {
                    artistListAdapter.clear();
                }
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
        if( artistListAdapter.getCount() == 0 && etArtist.getText().length() > 0) {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.artist_not_found), Toast.LENGTH_SHORT).show();
        }
    }
}
