package by.app.musicapps2018.manager;

public abstract class StreamingManager {

    public abstract void onPlay(MediaMetaData infoData);

    public abstract void onPause();

    public abstract void onStop();

    public abstract void onSeekTo(long position);

    public abstract int lastSeekPosition();

    public abstract void onSkipToNext();

    public abstract void onSkipToPrevious();
}