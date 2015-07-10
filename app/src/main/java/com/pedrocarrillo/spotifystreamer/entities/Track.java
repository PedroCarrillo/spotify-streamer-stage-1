package com.pedrocarrillo.spotifystreamer.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pedro on 09/06/15.
 */
public class Track implements Parcelable {

    private String name;
    private String albumTitle;
    private String imageUrl;
    private String id;
    private String previewUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    public Track(String id, String name, String albumTitle,String imageUrl,String previewUrl){
        this.name = name;
        this.imageUrl = imageUrl;
        this.id = id;
        this.albumTitle = albumTitle;
        this.previewUrl = previewUrl;
    }

    public Track(Parcel in){
        this.name = in.readString();
        this.imageUrl = in.readString();
        this.id = in.readString();
        this.albumTitle = in.readString();
        this.previewUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(id);
        dest.writeString(albumTitle);
        dest.writeString(previewUrl);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}
