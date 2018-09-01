package by.app.musicapps2018.presenter;

import java.util.ArrayList;

import by.app.musicapps2018.manager.MediaMetaData;
import by.app.musicapps2018.model.Like;
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
    void groupVK();
    void setCount(int count);
    boolean checkCount();
    void checkNewVersion();
    void exit();
    boolean checkReview();
    void checklike();
    void getHash(Like like);
    void setLike(String body, Like like);

}
