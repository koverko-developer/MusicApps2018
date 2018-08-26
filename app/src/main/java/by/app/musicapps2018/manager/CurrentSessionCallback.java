package by.app.musicapps2018.manager;

public interface CurrentSessionCallback {
    void updatePlaybackState(int state);

    void playSongComplete();

    void currentSeekBarPosition(int progress);

    void playCurrent(int indexP, MediaMetaData currentAudio);

    void playNext(int indexP, MediaMetaData currentAudio);

    void playPrevious(int indexP, MediaMetaData currentAudio);
}