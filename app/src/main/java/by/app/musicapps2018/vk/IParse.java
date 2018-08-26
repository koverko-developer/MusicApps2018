package by.app.musicapps2018.vk;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

import by.app.musicapps2018.enums.TypeAudio;
import by.app.musicapps2018.manager.MediaMetaData;
import by.app.musicapps2018.view.IMainActivity;
import by.app.musicapps2018.view.jcplayer.JcAudio;

public interface IParse {

    boolean setUserInfo(String response) throws JSONException;
    ArrayList<JcAudio> getAudios(String response, int offset, TypeAudio typeAudio) throws JSONException;
    ArrayList<JcAudio> preparePlaylist(String json, TypeAudio type, int offset) throws JSONException;
    String getAudioURL(String ids);
    Map<String, String> getBody(TypeAudio typeAudio, int offset);
    void initDownloader();
    void downloadAudio(MediaMetaData mediaMetaData, int type, IMainActivity _view);

}
