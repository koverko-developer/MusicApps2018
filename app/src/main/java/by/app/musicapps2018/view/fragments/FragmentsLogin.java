package by.app.musicapps2018.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

import by.app.musicapps2018.R;
import by.app.musicapps2018.model.ApiServices;
import by.app.musicapps2018.presenter.IFragmentLoginPresenter;
import by.app.musicapps2018.presenter.impl.FragmentLoginPresenter;
import by.app.musicapps2018.view.MainActivity;

@SuppressLint("ValidFragment")
public class FragmentsLogin extends Fragment implements IFragmentLogin{

    Context context;
    IFragmentLoginPresenter _presenter;
    ProgressBar progressBar;

    private static final String TAG = FragmentsLogin.class.getName();
    View v;

    public FragmentsLogin(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_login, container, false);
        if (_presenter == null) _presenter = new FragmentLoginPresenter(this, context);
        initWV();

        return v;
    }


    @Override
    public void initWV() {
        Log.d(TAG, "init web view");
        progressBar = (ProgressBar) v.findViewById(R.id.progress);

        final WebView webView = (WebView) v.findViewById(R.id.webView);
        webView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showProgress();
            }

            public void onPageFinished(WebView view, String url) {

                hireProgress();
                String cookie = CookieManager.getInstance().getCookie(url);
                if (cookie == null || !cookie.contains("xsid")) {
                    String cookies = CookieManager.getInstance().getCookie(url);
                    Log.d(TAG, "All the cookies in a string:" + cookies);
                    webView.setVisibility(View.VISIBLE);

                } else {
                    CookieSyncManager.getInstance().sync();
                    webView.setVisibility(View.GONE);
                    String cookies = CookieManager.getInstance().getCookie(url);
                    Log.d(TAG, "All the cookies in a string:" + cookies);
                    ConnectivityManager connMgr = (ConnectivityManager)
                            v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        _presenter.getUserData();

                    } else {
                        showNoConnectionMessage();
                    }



                }


            }
        });
        webView.loadUrl("https://vk.com");

    }

    @Override
    public void showNoConnectionMessage() {
        Log.d(TAG, "no connection");
    }

    @Override
    public void getMyAudio() {
        Log.d(TAG, "getMyAudio");
        MainActivity activity = (MainActivity) context;
        activity.checkLogin();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hireProgress() {
        progressBar.setVisibility(View.GONE);
    }
}
