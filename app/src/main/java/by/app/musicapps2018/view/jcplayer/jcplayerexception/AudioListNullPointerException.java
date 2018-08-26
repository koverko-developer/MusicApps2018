package by.app.musicapps2018.view.jcplayer.jcplayerexception;

public class AudioListNullPointerException extends NullPointerException {
    public AudioListNullPointerException() {
        super("The playlist is empty or null");
    }
}