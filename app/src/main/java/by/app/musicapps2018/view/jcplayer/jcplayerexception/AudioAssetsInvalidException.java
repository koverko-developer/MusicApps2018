package by.app.musicapps2018.view.jcplayer.jcplayerexception;

public class AudioAssetsInvalidException extends Exception {
    public AudioAssetsInvalidException(String path) {
        super("The file name is not a valid Assets file: " + path);
    }
}
