package com.pedrocarrillo.spotifystreamer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pedrocarrillo.spotifystreamer.R;
import com.pedrocarrillo.spotifystreamer.entities.Track;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Pedro on 07/06/15.
 */
public class TrackListAdapter extends BaseAdapter {

    List<Track> trackList;

    public TrackListAdapter(List<Track> trackList){
        this.trackList = trackList;
    }

    @Override
    public int getCount() {
        return trackList== null ? 0 : trackList.size();
    }

    @Override
    public Track getItem(int position) {
        return trackList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.list_top_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvAlbumTitle = (TextView) convertView.findViewById(R.id.tvAlbumName);
            viewHolder.tvSongTitle = (TextView) convertView.findViewById(R.id.tvSongTitle);
            viewHolder.ivSongImage = (ImageView) convertView.findViewById(R.id.ivSongImage);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Track track = trackList.get(position);
        viewHolder.tvSongTitle.setText(track.getName());
        viewHolder.tvAlbumTitle.setText(track.getAlbumTitle());
        Picasso.with(parent.getContext()).load(track.getImageUrl()).placeholder(R.drawable.not_found).into(viewHolder.ivSongImage);
        return convertView;
    }

    public ArrayList<Track> getTrackList(){
        return new ArrayList<>(trackList);
    }

    public void updateData(List<Track> trackList){
        this.trackList = trackList;
        notifyDataSetChanged();
    }

    public void clear(){
        trackList.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder{
        ImageView ivSongImage;
        TextView tvSongTitle, tvAlbumTitle;
    }

}