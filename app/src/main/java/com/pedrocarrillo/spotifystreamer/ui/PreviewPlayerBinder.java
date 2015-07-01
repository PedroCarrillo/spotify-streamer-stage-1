package com.pedrocarrillo.spotifystreamer.ui;

import android.os.Binder;

import com.pedrocarrillo.spotifystreamer.services.OnMediaPlayerInteraction;

/**
 * Created by Pedro on 30/06/15.
 */
public class PreviewPlayerBinder extends Binder implements OnMediaPlayerInteraction {

    @Override
    public void setCurrentPositionTrack(int position) {
        
    }
}
