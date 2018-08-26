package by.app.musicapps2018.view.jcplayer;

import android.content.Context;
import android.support.annotation.RawRes;
import android.webkit.CookieManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import by.app.musicapps2018.enums.Origin;
import by.app.musicapps2018.enums.TypeAudio;
import by.app.musicapps2018.model.ApiServices;
import by.app.musicapps2018.vk.Parse;
import by.app.musicapps2018.vk.Prefs;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class JcAudio implements Serializable {

    Context context;
    TypeAudio typeAudio;
    private long id;
    private String user_id;
    private String title;
    private int position;
    private String path;
    private Origin origin;
    String album_img;
    int duration;

    String title_n;
    String artist_n;

    Pattern pattern = Pattern.compile("[^a-z A-Z]");


    String url;

    public TypeAudio getTypeAudio() {
        return typeAudio;
    }

    public void setTypeAudio(TypeAudio typeAudio) {
        this.typeAudio = typeAudio;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlbum_img() {
        return album_img;
    }

    public void setAlbum_img(String album_img) {
        this.album_img = album_img;
    }

    public JcAudio(String title, String path, Origin origin) {
        // It looks bad
        //int randomNumber = path.length() + title.length();

        // We init id  -1 and position with -1. And let JcPlayerView define it.
        // We need to do this because there is a possibility that the user reload previous playlist
        // from persistence storage like sharedPreference or SQLite.
        this.id = -1;
        this.position = -1;
        this.title = title;
        this.path = path;
        this.origin = origin;

    }

    public void getUrlNorm(){

    }

    public JcAudio(String title, String path, long id, int position, Origin origin) {
        this.id = id;
        this.position = position;
        this.title = title;
        this.path = path;
        this.origin = origin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public static JcAudio createFromRaw(@RawRes int rawId) {
        return new JcAudio(String.valueOf(rawId), String.valueOf(rawId), Origin.RAW);
    }

    public static JcAudio createFromRaw(String title, @RawRes int rawId) {
        return new JcAudio(title, String.valueOf(rawId), Origin.RAW);
    }

    public static JcAudio createFromAssets(String assetName) {
        return new JcAudio(assetName, assetName, Origin.ASSETS);
    }

    public static JcAudio createFromAssets(String title, String assetName) {
        return new JcAudio(title, assetName, Origin.ASSETS);
    }

    public static JcAudio createFromURL(String url) {
        return new JcAudio(url, url, Origin.URL);
    }

    public static JcAudio createFromURL(String title, String url) {
        return new JcAudio(title, url, Origin.URL);
    }

    public static JcAudio createFromFilePath(String filePath) {
        return new JcAudio(filePath, filePath, Origin.FILE_PATH);
    }

    public static JcAudio createFromFilePath(String title, String filePath) {
        return new JcAudio(title, filePath, Origin.FILE_PATH);
    }

    public void getSongUrl() {
        //Prefs prefs = new Prefs(context);
        String cookie = CookieManager.getInstance().getCookie("https://vk.com");
        Map<String, String> body = new HashMap();
        body.put("act", "reload_audio");
        body.put("al", "1");
        body.put("ids", this.path);
        //final int id = prefs.getID();
        Observable<ResponseBody> dataObservable = new ApiServices().getApi().alAudio(cookie, body);

        dataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((ResponseBody earthquakeData) ->
                {
                    try {
                        String response = ((ResponseBody) earthquakeData).string();

                        if (response.length() < 100) {

                        } else {
                            path = new Parse().decode(response.substring(response.indexOf("https"), response.indexOf("\",\"")).replace("\\", ""), Integer.parseInt(user_id));
                            setUrl(path);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

    }

    public String getTitle_n() {
        return title_n;
    }

    public void setTitle_n(String title_n) {
        this.title_n = title_n;
    }

    public String getArtist_n() {
        return artist_n;
    }

    public void setArtist_n(String artist_n) {
        this.artist_n = artist_n;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    String id_string;

    public String getId_string() {
        return id_string;
    }

    public void setId_string(String id_string) {
        this.id_string = id_string;
    }
}