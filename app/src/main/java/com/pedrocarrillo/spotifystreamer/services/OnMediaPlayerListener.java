package com.pedrocarrillo.spotifystreamer.services;

import android.media.MediaPlayer;

import com.pedrocarrillo.spotifystreamer.entities.Track;

/**
 * Created by Pedro on 30/06/15.
 */

public interface OnMediaPlayerListener{
    Track getTrackSelected();
    MediaPlayer getMediaPlayer();
    boolean isSameSong();
    PreviewPlayerService.PlayerState getPlayerState();
}