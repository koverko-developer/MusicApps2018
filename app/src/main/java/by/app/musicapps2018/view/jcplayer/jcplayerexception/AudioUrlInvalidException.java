package by.app.musicapps2018.view.jcplayer.jcplayerexception;

public class AudioUrlInvalidException extends IllegalStateException {
    public AudioUrlInvalidException(String url) {
        super("The url does not appear valid: " + url);
    }
}