package com.pedrocarrillo.spotifystreamer.asynctasks;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by Pedro on 07/06/15.
 */
public class ArtistAsyncTask extends AsyncTask<String,Void,List<Artist>> {

    private AdapterListener adapterListener;
    private List<Artist> artistList;

    public ArtistAsyncTask(Fragment fragment){
        adapterListener = (AdapterListener) fragment;
    }

    @Override
    protected List<Artist> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        String query = params[0];
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        ArtistsPager results = spotify.searchArtists(query);
        artistList = results.artists.items;
        return artistList;
    }

    @Override
    protected void onPostExecute(List<Artist> artistList) {
        super.onPostExecute(artistList);
        if ( artistList != null) {
            adapterListener.updateList(artistList);
        }else{
            adapterListener.clearList();
        }
    }

    public interface AdapterListener{
        void updateList(List<Artist> artistList);
        void clearList();
    }

}
