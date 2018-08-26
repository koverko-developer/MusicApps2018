package by.app.musicapps2018.view.jcplayer.jcplayerexception;


public class AudioRawInvalidException extends Exception {
    public AudioRawInvalidException(String rawId) {
        super("Not a valid raw file id: " + rawId);
    }
}