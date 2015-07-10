package com.pedrocarrillo.spotifystreamer.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pedro on 08/06/15.
 */
public class Artist implements Parcelable {

    private String name;
    private String imageUrl;
    private String id;

    @Override
    public int describeContents() {
        return 0;
    }

    public Artist(String id, String name, String imageUrl){
        this.name = name;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    public Artist(Parcel in){
        this.name = in.readString();
        this.id = in.readString();
        this.imageUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(imageUrl);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        public Artist[] newArray(int size) {
            return new Artist[size];
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
}
