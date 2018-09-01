package by.app.musicapps2018.vk;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.Html;
import android.webkit.CookieManager;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadConfiguration;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import by.app.musicapps2018.enums.TypeAudio;
import by.app.musicapps2018.manager.AudioStreamingManager;
import by.app.musicapps2018.manager.MediaMetaData;
import by.app.musicapps2018.model.ApiServices;
import by.app.musicapps2018.view.IMainActivity;
import by.app.musicapps2018.view.jcplayer.JcAudio;
import by.app.musicapps2018.view.jcplayer.JcPlayerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Parse implements IParse{

    private static String STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN0PQRSTUVWXYZO123456789+/=";
    String path = "";
    Context context;
    Prefs prefs;
    String lastPath = "";

    public Parse(){

    }

    public Parse(Context _context) {
        this.context = _context;
        prefs = new Prefs(context);
        initDownloader();
    }

    @Override
    public void initDownloader() {
        DownloadConfiguration configuration = new DownloadConfiguration();
        configuration.setMaxThreadNum(100);
        configuration.setThreadNum(30);
        DownloadManager.getInstance().init(context, configuration);
    }

    @Override
    public void downloadAudio(MediaMetaData mediaMetaData, int type, IMainActivity _view) {
        //_view.updateCookies();
        try {

            String cookie = CookieManager.getInstance().getCookie("https://vk.com");
            Map<String, String> body = new HashMap();
            body.put("act", "reload_audio");
            body.put("al", "1");
            body.put("ids", mediaMetaData.getMediaId());
            final int id = prefs.getID();
            Observable<ResponseBody> dataObservable = new ApiServices().getApi().alAudio(cookie, body);

            dataObservable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((ResponseBody earthquakeData) ->
                    {
                        try {
                            String response = ((ResponseBody) earthquakeData).string();

                            if (response.length() < 100) {

                            } else {
                                path = decode(response.substring(response.indexOf("https"), response.indexOf("\",\"")).replace("\\", ""), id);
                                mediaMetaData.setMediaUrl(path);

                                String path = "";
                                if(type == 1)  path = prefs.getPATH();
                                else path = "";

                                final DownloadRequest request = new DownloadRequest.Builder()
                                        .setName(mediaMetaData.getMediaTitle() + "-"+
                                                mediaMetaData.getMediaArtist()+ ".mp3")
                                        .setUri(mediaMetaData.getMediaUrl())
                                        .setFolder(new File(path))
                                        .build();

// download:
// the tag here, you can simply use download uri as your tag;
                                String finalPath = path;
                                DownloadManager.getInstance().download(request, mediaMetaData.getMediaId(), new CallBack() {
                                    @Override
                                    public void onStarted() {
                                        _view.showNotify();
                                        _view.setBooleanDownload(true);
                                    }

                                    @Override
                                    public void onConnecting() {

                                    }

                                    @Override
                                    public void onConnected(long total, boolean isRangeSupport) {

                                    }

                                    @Override
                                    public void onProgress(long finished, long total, int progress) {
                                        _view.showNotify();
                                        _view.setBooleanDownload(true);
                                        _view.setInfoNotify(mediaMetaData, progress, finalPath);
                                    }

                                    @Override
                                    public void onCompleted() {
                                       _view.hideNotify();
                                        _view.setBooleanDownload(false);
                                       _view.deleteFromDownloadList(mediaMetaData);
                                    }

                                    @Override
                                    public void onDownloadPaused() {
                                        _view.deleteFromDownloadList(mediaMetaData);
                                        _view.setBooleanDownload(false);
                                    }

                                    @Override
                                    public void onDownloadCanceled() {
                                        _view.deleteFromDownloadList(mediaMetaData);
                                        _view.setBooleanDownload(false);
                                    }

                                    @Override
                                    public void onFailed(DownloadException e) {
                                        String s = e.toString();
                                        _view.deleteDownloadList();
                                        _view.setBooleanDownload(false);
                                        _view.hideNotify();
                                    }
                                });
                            }

                            //return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean setUserInfo(String response) throws JSONException {

        JSONObject me = new JSONObject(Html.fromHtml(response.substring(response.indexOf("<!json>") + 7)).toString()).getJSONObject("me");
        //User user = new User(me.getString("id"), me.getString("name"), me.getString("photo"));
        String id = me.getString("id");
        prefs.setNAME(me.getString("name"));
        prefs.setPHOTO(me.getString("photo"));
        prefs.setID(id);

        return true;

    }

    @Override
    public ArrayList<JcAudio> getAudios(String response, int offset, TypeAudio typeAudio) throws JSONException {

        return preparePlaylist(response, typeAudio, offset);

    }
    @Override
    public ArrayList<JcAudio> preparePlaylist(String json, TypeAudio type, int offset) throws JSONException {


        int total = 0;
        //getSongUrl();
        JSONArray songJson = new JSONArray();
        String tmpJson = json.substring(json.indexOf("<!json>") + 7, json.indexOf("}<!>") + 1);
        JSONObject jSONObject;
        try {
            jSONObject = new JSONObject(tmpJson);
            songJson = jSONObject.getJSONArray("list");
            total = Integer.parseInt(jSONObject.getString("totalCount"));
            String dsda = "";
        } catch (Exception e) {
            jSONObject = new JSONObject(tmpJson.substring(0, tmpJson.indexOf("[[") - 7) + tmpJson.substring(tmpJson.indexOf("]]") + 3, tmpJson.length()));
            String sd = "";
            total = Integer.parseInt(jSONObject.getString("totalCount"));
            //JSONArray jSONArray = new JSONArray(fixList(tmpJson));
        }

        String ds = "";
        int size = songJson.length();
        ArrayList<JcAudio> songList = new ArrayList();
        int i = 0;
//        if(type == TypeAudio.MyAudio100 || type == TypeAudio.Search) i = offset;
//        if(type == TypeAudio.MyAudio100 || type == TypeAudio.Search) size = 50;
        for (; i < size; i++) {
            JSONArray jsonSong = songJson.getJSONArray(i);
            String[] coverUrl = jsonSong.getString(14).split(",");
            String id = jsonSong.getString(1) + "_" + jsonSong.getString(0);
            String hash = jsonSong.getString(2);
            String album = jsonSong.getString(14).split(",")[0];
            String title = jsonSong.getString(3) + "--" + jsonSong.getString(4);
            String title_norm = jsonSong.getString(3);
            String artist_norm = jsonSong.getString(4);
            String duration = jsonSong.getString(4);
            String dd = "";
            JcAudio audio = JcAudio.createFromURL(title ,id);
            audio.setAlbum_img(album);
            audio.setTitle_n(title_norm);
            audio.setArtist_n(artist_norm);
            audio.setDuration(jsonSong.getInt(5));
            audio.setId_string(id);
            //audio.setContext(context);
            audio.setTypeAudio(type);
            //audio.getSongUrl();
            songList.add(audio);

        }

        return songList;

    }

    public void playMusic(MediaPlayer mediaPlayer, MediaMetaData mediaMetaData, JcPlayerView jcPlayerView){
        try {
            String cookie = CookieManager.getInstance().getCookie("https://vk.com");
            Map<String, String> body = new HashMap();
            body.put("act", "reload_audio");
            body.put("al", "1");
            body.put("ids", mediaMetaData.getMediaId());
            final int id = prefs.getID();
            Observable<ResponseBody> dataObservable = new ApiServices().getApi().alAudio(cookie, body);

            dataObservable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((ResponseBody earthquakeData) ->
                    {
                        try {
                            String response = ((ResponseBody) earthquakeData).string();

                            if (response.length() < 100) {

                            } else {
                                path = decode(response.substring(response.indexOf("https"), response.indexOf("\",\"")).replace("\\", ""), id);
                                mediaMetaData.setMediaUrl(path);
                                mediaPlayer.setDataSource(mediaMetaData.getMediaUrl());
                                mediaPlayer.prepareAsync();
                                jcPlayerView.dismissProgressBar();
                            }

                            //return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public String getAudioURL(String ids) {

        String cookie = CookieManager.getInstance().getCookie("https://vk.com");
        Map<String, String> body = new HashMap();
        body.put("act", "reload_audio");
        body.put("al", "1");
        body.put("ids", ids);
        final int id = prefs.getID();
        Observable<ResponseBody> dataObservable = new ApiServices().getApi().alAudio(cookie, body);

        dataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((ResponseBody earthquakeData) ->
                {
                    try {
                        String response = ((ResponseBody) earthquakeData).string();

                        if (response.length() < 100) {

                        } else {
                            path = decode(response.substring(response.indexOf("https"), response.indexOf("\",\"")).replace("\\", ""), id);
                        }

                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                });
        return path;
    }

    @Override
    public Map<String, String> getBody(TypeAudio typeAudio, int offset) {

        Map<String, String> body = new HashMap<>();

        body.put("access_hash", "");
        body.put("owner_id", String.valueOf(prefs.getID()));

        body.put("offset", "0");
        body.put("act", "load_section");
        body.put("al", "1");
        if(typeAudio == TypeAudio.MyAudio) {
            body.put("playlist_id", "-1");
            body.put("type", "playlist");
        }
        if(typeAudio == TypeAudio.Special) {
            body.put("type", "recoms");
            body.put("playlist_id", "recomsPUkLGlpXADkvD0tMBBhJFicMDClBTRsDZFFLFVRACgopDEsL");
        }if(typeAudio == TypeAudio.Popular) {
            body.put("type", "recoms");
            body.put("playlist_id", "recomsPUkLGlpXADkvD0tMDRhJFicMDClBTRsDZFFLFVRACgopDEsL");
        }if(typeAudio == TypeAudio.UpdatesF) {
            body.put("type", "feed");body.put("playlist_id", "recomsPUkLGlpXADkvD0tMDRhJFicMDClBTRsDZFFLFVRACgopDEsL");
        }
        if(typeAudio == TypeAudio.News) {
            body.put("type", "recoms");
            body.put("playlist_id", "recomsPUkLGlpXADkvD0tMBABHRDYKDhNqQBIWI0lTVFZVHwcqBA5USA");
        }
        if(typeAudio == TypeAudio.MyAudio100) {
            body.put("playlist_id", "-1");
            body.put("type", "playlist");
            body.put("offset", String.valueOf(offset));
        }
        if(typeAudio == TypeAudio.Search) {
            body.put("owner_id", String.valueOf(prefs.getID()));
            body.put("type", "search");
        }

        return body;
    }

    private static String shiftArray(String[] array) {
        String result = array[0];
        System.arraycopy(array, 1, array, 0, array.length - 1);
        return result;
    }

    public static String decode(String url, int userId) {
        try {
            String[] vals = url.split("/?extra=")[1].split("#");
            url = vk_o(vals[0]);
            String[] opsArr = vk_o(vals[1]).split(String.valueOf('\t'));
            for (int i = opsArr.length - 1; i >= 0; i--) {
                String[] argsArr = opsArr[i].split(String.valueOf('\u000b'));
                String opInd = shiftArray(argsArr);
                int i2 = -1;
                url = vk_i(url, Integer.parseInt(argsArr[0]), userId);
                String s ="";
                //            switch (i2) {
                //                case uk.co.samuelwall.materialtaptargetprompt.R.styleable.PromptView_mttp_autoDismiss /*0*/:
                //                    url = vk_i(url, Integer.parseInt(argsArr[0]), userId);
                //                    break;
                //                case uk.co.samuelwall.materialtaptargetprompt.R.styleable.PromptView_mttp_autoFinish /*1*/:
                //                    url = vk_v(url);
                //                    break;
                //                case uk.co.samuelwall.materialtaptargetprompt.R.styleable.PromptView_mttp_backgroundColour /*2*/:
                //                    url = vk_r(url, Integer.parseInt(argsArr[0]));
                //                    break;
                //                case uk.co.samuelwall.materialtaptargetprompt.R.styleable.PromptView_mttp_captureTouchEventOnFocal /*3*/:
                //                    url = vk_x(url, argsArr[0]);
                //                    break;
                //                case uk.co.samuelwall.materialtaptargetprompt.R.styleable.PromptView_mttp_captureTouchEventOutsidePrompt /*4*/:
                //                    url = vk_s(url, Integer.parseInt(argsArr[0]));
                //                    break;
                //                default:
                //                    break;
                //            }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return url.substring(0, url.indexOf("?extra="));
    }

    private static String vk_i(String str, int e, int userID) {
        return vk_s(str, e ^ userID);
    }
    private static String vk_s(String str, int start) {
        StringBuilder result = null;
        try {
            result = new StringBuilder(str);
            int len = str.length();
            int e = start;
            if (len > 0) {
                int i;
                Integer[] shufflePos = new Integer[len];
                for (i = len - 1; i >= 0; i--) {
                    e = Math.abs((((i + 1) * len) ^ (e + i)) % len);
                    shufflePos[i] = Integer.valueOf(e);
                }
                for (i = 1; i < len; i++) {
                    int offset = shufflePos[(len - i) - 1].intValue();
                    String prev = result.substring(i, i + 1);
                    result.replace(i, i + 1, result.substring(offset, offset + 1));
                    result.replace(offset, offset + 1, prev);
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return result.toString();
    }

    public void play(final JcAudio jcAudio) {

        final String ids = jcAudio.getPath();
        String cookie = CookieManager.getInstance().getCookie("https://vk.com");
        Map<String, String> body = new HashMap();
        body.put("act", "reload_audio");
        body.put("al", "1");
        body.put("ids", ids);
        Prefs prefs = new Prefs(context);
        final int id = prefs.getID();



        Observable<ResponseBody> dataObservable = new ApiServices().getApi().getUser(cookie, body);

        dataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((ResponseBody earthquakeData) ->
                {
                    try {
                        String response = ((ResponseBody) earthquakeData).string();
                        if (response.length() < 100) {
                            path = lastPath;
                            jcAudio.setPath(path);
                            String dsdsd ="";
                        }else {
                            path = decode(response.substring(response.indexOf("https"), response.indexOf("\",\"")).replace("\\", ""), id);
                            lastPath = path;
                            jcAudio.setPath(path);
                            String dsdsd ="";
                        }

                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                });


    }

    private static String vk_o(String str) {
        StringBuilder b = null;
        try {
            int len = str.length();
            int i = 0;
            b = new StringBuilder();
            int index2 = 0;
            for (int s = 0; s < len; s++) {
                int symIndex = STR.indexOf(str.substring(s, s + 1));
                if (symIndex >= 0) {
                    if (index2 % 4 != 0) {
                        i = (i << 6) + symIndex;
                    } else {
                        i = symIndex;
                    }
                    if (index2 % 4 != 0) {
                        index2++;
                        b.append((char) ((i >> ((index2 * -2) & 6)) & 255));
                    } else {
                        index2++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b.toString();
    }

}
