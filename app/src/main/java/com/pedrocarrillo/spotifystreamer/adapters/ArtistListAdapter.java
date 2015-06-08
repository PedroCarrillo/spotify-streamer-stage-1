package com.pedrocarrillo.spotifystreamer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pedrocarrillo.spotifystreamer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Pedro on 07/06/15.
 */
public class ArtistListAdapter extends BaseAdapter {

    List<Artist> artistList;

    public ArtistListAdapter(List<Artist> artistList){
        this.artistList = artistList;
    }

    @Override
    public int getCount() {
        return artistList== null ? 0 : artistList.size();
    }

    @Override
    public Artist getItem(int position) {
        return artistList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;// artistList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.list_search_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvArtistName = (TextView) convertView.findViewById(R.id.tvArtistName);
            viewHolder.ivArtistImage = (ImageView) convertView.findViewById(R.id.ivArtistImage);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Artist artist = artistList.get(position);
        viewHolder.tvArtistName.setText(artist.name);
        String imageUrl = "http://www.google.com";
        if(artist.images.size() > 0) {
            imageUrl = artist.images.get(0).url;
        }
        Picasso.with(parent.getContext()).load(imageUrl).placeholder(R.drawable.not_found ).into(viewHolder.ivArtistImage);
        return convertView;
    }

    public void updateData(List<Artist> artistList){
        this.artistList = artistList;
        notifyDataSetChanged();
    }

    public void clear(){
        artistList.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder{
        ImageView ivArtistImage;
        TextView tvArtistName;
    }

}
