package by.app.musicapps2018.presenter.impl;

import android.content.Context;
import android.webkit.CookieManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import by.app.musicapps2018.model.ApiServices;
import by.app.musicapps2018.presenter.IFragmentLoginPresenter;
import by.app.musicapps2018.view.IMainActivity;
import by.app.musicapps2018.view.fragments.IFragmentLogin;
import by.app.musicapps2018.vk.Parse;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FragmentLoginPresenter implements IFragmentLoginPresenter{

    ApiServices _api;
    IFragmentLogin _view;
    Context context;

    public FragmentLoginPresenter(IFragmentLogin view, Context _context) {
        _api = new ApiServices();
        _view = view;
        this.context = _context;
    }

    @Override
    public void getUserData() {

        String cookie = CookieManager.getInstance().getCookie("https://vk.com");
        Map<String, String> body = new HashMap();
        body.put("act", "a_get_fast_chat");
        body.put("al", "1");

        Observable<ResponseBody> dataObservable = _api.getApi().getUser(cookie, body);

        dataObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(earthquakeData ->
                {
                    try {

                        String response = ((ResponseBody) earthquakeData).string();
                        if(new Parse(context).setUserInfo(response)) {

                            _view.getMyAudio();

                        };

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


    }
}
