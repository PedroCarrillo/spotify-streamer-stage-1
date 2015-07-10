package com.pedrocarrillo.spotifystreamer.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pcarrillo on 09/07/2015.
 */
public class Track implements Parcelable {

    private String id, name, albumName, imageUrl, previewUrl;

    public Track(String id,String name, String albumName, String imageUrl, String previewUrl){
        this.id = id;
        this.name = name;
        this.albumName = albumName;
        this.imageUrl = imageUrl;
        this.previewUrl = previewUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbumTitle() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    protected Track(Parcel in) {
        id = in.readString();
        name = in.readString();
        albumName = in.readString();
        imageUrl = in.readString();
        previewUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(albumName);
        dest.writeString(imageUrl);
        dest.writeString(previewUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}
