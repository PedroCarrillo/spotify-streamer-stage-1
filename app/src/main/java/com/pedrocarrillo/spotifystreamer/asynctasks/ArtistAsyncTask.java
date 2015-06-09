package com.pedrocarrillo.spotifystreamer.asynctasks;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * Created by Pedro on 07/06/15.
 */
public class ArtistAsyncTask extends AsyncTask<String,Void,ArtistsPager> {

    private AdapterListener adapterListener;

    public ArtistAsyncTask(Fragment fragment){
        adapterListener = (AdapterListener) fragment;
    }

    @Override
    protected ArtistsPager doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        String query = params[0];
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        try {
            return spotify.searchArtists(query);
        }catch (RetrofitError e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArtistsPager artistsPager) {
        super.onPostExecute(artistsPager);
        if (artistsPager != null) {
            List<Artist> artistList = artistsPager.artists.items;
            List<com.pedrocarrillo.spotifystreamer.entities.Artist> entityArtistList = new ArrayList<>();
            for ( Artist spotifyArtist : artistList){
                String imageUrl = "http://www.google.com";
                if(spotifyArtist.images.size() > 0) {
                    imageUrl = spotifyArtist.images.get(0).url;
                }
                entityArtistList.add(new com.pedrocarrillo.spotifystreamer.entities.Artist(spotifyArtist.id,spotifyArtist.name, imageUrl));
            }
            if (artistList != null) {
                adapterListener.updateList(entityArtistList);
            } else {
                adapterListener.clearList();
            }
        }else{
            adapterListener.noConnection();
        }
    }

    public interface AdapterListener{
        void updateList(List<com.pedrocarrillo.spotifystreamer.entities.Artist> artistList);
        void clearList();
        void noConnection();
    }

}
