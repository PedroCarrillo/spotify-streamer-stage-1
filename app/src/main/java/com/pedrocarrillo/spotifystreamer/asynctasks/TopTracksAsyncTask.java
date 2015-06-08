package com.pedrocarrillo.spotifystreamer.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.pedrocarrillo.spotifystreamer.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * Created by Pedro on 07/06/15.
 */
public class TopTracksAsyncTask extends AsyncTask<String,Void,Tracks> {

    private TrackAdapterListener adapterListener;
    private Context context;

    public TopTracksAsyncTask(Fragment fragment){
        adapterListener = (TrackAdapterListener) fragment;
        context = fragment.getActivity();
    }

    @Override
    protected Tracks doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        String query = params[0];
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        Map<String, Object> parameters = new HashMap<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String countryKey = sharedPreferences.getString(context.getResources().getString(R.string.country_key), context.getResources().getString(R.string.default_country));
        parameters.put("country", countryKey);
        try{
            return spotify.getArtistTopTrack(query,parameters);
        }catch (RetrofitError e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Tracks tracks) {
        super.onPostExecute(tracks);
        if(tracks != null) {
            List<Track> trackList = tracks.tracks;
            if (trackList != null) {
                adapterListener.updateList(trackList);
            } else {
                adapterListener.clearList();
            }
        }else {
            adapterListener.errorConnection();
        }
    }

    public interface TrackAdapterListener{
        void updateList(List<Track> tracksList);
        void clearList();
        void errorConnection();
    }

}
