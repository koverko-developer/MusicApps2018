package by.app.musicapps2018.presenter;

import java.util.ArrayList;

import by.app.musicapps2018.manager.MediaMetaData;
import by.app.musicapps2018.view.jcplayer.JcAudio;

public interface IMainPresenter {

    void isLogin();
    void getMyAudio();
    void getMyAudioAfter100(int offset, ArrayList<JcAudio> list);
    void getSpecial();
    void getNews();
    void getPopular();
    void getUpdateFriends();
    void getSearch(String q, int offset);
    void setPath(String path);
    void downloadAudio(MediaMetaData mediaMetaData, int type);

}
