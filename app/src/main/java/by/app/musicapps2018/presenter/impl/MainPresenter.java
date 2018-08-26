package by.app.musicapps2018.presenter.impl;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadConfiguration;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.app.musicapps2018.enums.TypeAudio;
import by.app.musicapps2018.manager.MediaMetaData;
import by.app.musicapps2018.model.ApiServices;
import by.app.musicapps2018.presenter.IMainPresenter;
import by.app.musicapps2018.view.IMainActivity;
import by.app.musicapps2018.view.jcplayer.JcAudio;
import by.app.musicapps2018.vk.Parse;
import by.app.musicapps2018.vk.Prefs;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenter implements IMainPresenter{

    private static final String TAG = MainPresenter.class.getName();
    Context context;
    IMainActivity _view;
    Prefs prefs;
    Parse parse;
    String user_id;

    public MainPresenter(Context context, IMainActivity _view) {
        this.context = context;
        this._view = _view;
        prefs = new Prefs(context);
        parse = new Parse(context);
        user_id = String.valueOf(prefs.getID());
    }

    @Override
    public void isLogin() {
        Log.e(TAG, "isLOGIN :"+String.valueOf(prefs.isLogin()));
        if(prefs.isLogin()) _view.initSpinner();
        else _view.initWebView();
    }

    @Override
    public void getMyAudio() {

        _view.showProgress();
        String cookie = CookieManager.getInstance().getCookie("https://vk.com");

        Map<String, String> body = parse.getBody(TypeAudio.MyAudio,0);

        Observable<ResponseBody> dataObservable = new ApiServices().getApi().alAudio(cookie, body);
        dataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(earthquakeData ->
                {
                    try {

                        String response = ((ResponseBody) earthquakeData).string();
                        ArrayList<JcAudio> list =  parse.getAudios(response, 0, TypeAudio.MyAudio);

                        if(list != null) {
                            for (JcAudio a:list
                                    ) {
                                a.setUser_id(user_id);
                            }
                            getMyAudioAfter100(100, list);
                            //_view.setRecycler(list, TypeAudio.MyAudio);

                        }
                        //_view.hideProgress();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });



    }

    @Override
    public void getMyAudioAfter100(int offset, ArrayList<JcAudio> list) {

        _view.showProgress();
        String cookie = CookieManager.getInstance().getCookie("https://vk.com");

        Map<String, String> body = parse.getBody(TypeAudio.MyAudio100,offset);

        Observable<ResponseBody> dataObservable = new ApiServices().getApi().alAudio(cookie, body);
        dataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(earthquakeData ->
                {
                    try {

                        String response = ((ResponseBody) earthquakeData).string();
                        ArrayList<JcAudio> lists =  parse.getAudios(response, offset, TypeAudio.MyAudio100);

                        if(lists != null) {
                            for (JcAudio a:lists
                                    ) {
                                a.setUser_id(user_id);
                            }

                            list.addAll(lists);
                            _view.setRecycler(list, TypeAudio.MyAudio100);

                        }
                        _view.hideProgress();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

    }

    @Override
    public void getSpecial() {

        _view.showProgress();
        String cookie = CookieManager.getInstance().getCookie("https://vk.com");

        Map<String, String> body = parse.getBody(TypeAudio.Special,0);

        Observable<ResponseBody> dataObservable = new ApiServices().getApi().alAudio(cookie, body);
        dataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(earthquakeData ->
                {
                    try {

                        String response = ((ResponseBody) earthquakeData).string();
                        ArrayList<JcAudio> list =  parse.getAudios(response, 0, TypeAudio.Special);

                        if(list != null) {
                            for (JcAudio a:list
                                    ) {
                                a.setUser_id(user_id);
                            }
                            _view.setRecycler(list, TypeAudio.Special);

                        }
                        _view.hideProgress();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

    }

    @Override
    public void getNews() {
        _view.showProgress();
        String cookie = CookieManager.getInstance().getCookie("https://vk.com");

        Map<String, String> body = parse.getBody(TypeAudio.News,0);

        Observable<ResponseBody> dataObservable = new ApiServices().getApi().alAudio(cookie, body);
        dataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(earthquakeData ->
                {
                    try {

                        String response = ((ResponseBody) earthquakeData).string();
                        ArrayList<JcAudio> list =  parse.getAudios(response, 0, TypeAudio.News);

                        if(list != null) {
                            for (JcAudio a:list
                                    ) {
                                a.setUser_id(user_id);
                            }
                            _view.setRecycler(list, TypeAudio.News);

                        }
                        _view.hideProgress();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void getPopular() {
        _view.showProgress();
        String cookie = CookieManager.getInstance().getCookie("https://vk.com");

        Map<String, String> body = parse.getBody(TypeAudio.Popular,0);

        Observable<ResponseBody> dataObservable = new ApiServices().getApi().alAudio(cookie, body);
        dataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(earthquakeData ->
                {
                    try {

                        String response = ((ResponseBody) earthquakeData).string();
                        ArrayList<JcAudio> list =  parse.getAudios(response, 0, TypeAudio.Popular);

                        if(list != null) {
                            for (JcAudio a:list
                                    ) {
                                a.setUser_id(user_id);
                            }
                            _view.setRecycler(list, TypeAudio.Popular);

                        }
                        _view.hideProgress();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void getUpdateFriends() {
        _view.showProgress();
        String cookie = CookieManager.getInstance().getCookie("https://vk.com");

        Map<String, String> body = parse.getBody(TypeAudio.UpdatesF,0);

        Observable<ResponseBody> dataObservable = new ApiServices().getApi().alAudio(cookie, body);
        dataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(earthquakeData ->
                {
                    try {

                        String response = ((ResponseBody) earthquakeData).string();
                        ArrayList<JcAudio> list =  parse.getAudios(response, 0, TypeAudio.UpdatesF);

                        if(list != null) {
                            for (JcAudio a:list
                                    ) {
                                a.setUser_id(user_id);
                            }
                            _view.setRecycler(list, TypeAudio.UpdatesF);

                        }
                        _view.hideProgress();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void getSearch(String q, int offset) {

        _view.showProgress();
        String cookie = CookieManager.getInstance().getCookie("https://vk.com");

        Map<String, String> body = parse.getBody(TypeAudio.Search,0);
        body.put("search_q", q);

        Observable<ResponseBody> dataObservable = new ApiServices().getApi().alAudio(cookie, body);
        dataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(earthquakeData ->
                {
                    try {

                        String response = ((ResponseBody) earthquakeData).string();
                        ArrayList<JcAudio> list =  parse.getAudios(response, offset, TypeAudio.Search);

                        if(list != null) {

                            for (JcAudio a:list
                                 ) {
                                a.setUser_id(user_id);
                            }

                            _view.setRecycler(list, TypeAudio.Search);

                        }
                        _view.hideProgress();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

    }

    @Override
    public void setPath(String path) {
        prefs.setPATH(path);
    }



    @Override
    public void downloadAudio(MediaMetaData mediaMetaData, int type) {

        String path = "";
        if(type == 1){
           if(prefs.getPATH().equals("0")) {
               _view.showStorage();
               return;
           }else path = prefs.getPATH();

        }else path = "";

        new Parse(context).downloadAudio(mediaMetaData, type, _view);
    }


}
