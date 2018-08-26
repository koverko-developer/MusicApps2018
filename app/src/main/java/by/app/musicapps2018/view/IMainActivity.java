package by.app.musicapps2018.view;

import java.util.ArrayList;

import by.app.musicapps2018.enums.TypeAudio;
import by.app.musicapps2018.manager.MediaMetaData;
import by.app.musicapps2018.view.jcplayer.JcAudio;

public interface IMainActivity {

    void initVW();
    void checkLogin();
    void initSpinner();
    void initWebView();
    void showProgress();
    void hideProgress();
    void showStorage();
    void hideRecycler();
    void showRecycler();
    void updateCookies();
    void setRecycler(ArrayList<JcAudio> list, TypeAudio typeAudio);
    void hideSelectRel();
    void showSelectRel();
    void initSelectRel();
    void initialiseListPlayer(ArrayList<JcAudio> audios);
    void playAudioPosition(int position, ArrayList<JcAudio> audioArrayList);
    void downloadAudio(MediaMetaData mediaMetaData, int type);
    void showMessageDownloadStatus(String name, int type);
    void checkPermission();

}
