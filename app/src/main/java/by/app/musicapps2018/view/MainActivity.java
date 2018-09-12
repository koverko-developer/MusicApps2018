package by.app.musicapps2018.view;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadConfiguration;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;
import com.codekidlabs.storagechooser.StorageChooser;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.endless.Endless;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import by.app.musicapps2018.R;
import by.app.musicapps2018.adapter.AudioSelectAdapter;
import by.app.musicapps2018.enums.TypeAudio;
import by.app.musicapps2018.manager.AudioStreamingManager;
import by.app.musicapps2018.manager.MediaMetaData;
import by.app.musicapps2018.model.ApiServices;
import by.app.musicapps2018.presenter.IMainPresenter;
import by.app.musicapps2018.presenter.impl.MainPresenter;
import by.app.musicapps2018.view.fragments.FragmentsLogin;
import by.app.musicapps2018.view.jcplayer.JcAudio;
import by.app.musicapps2018.view.jcplayer.JcPlayerView;
import by.app.musicapps2018.view.jcplayer.JcStatus;

public class MainActivity extends AppCompatActivity implements IMainActivity{
    // RELATIVE SELECT

    RelativeLayout select_rel, select_rel_img;
    RecyclerView select_recycler;
    AudioSelectAdapter adapter;
    NestedScrollView container;
    //-------------------------


    // notify

    RelativeLayout rel_n;
    ImageView img_expand_n;
    TextView tv_progress_n, tv_name_n;
    ProgressBar progress_n;
    TextView tv_path;

    //------------------

    InterstitialAd interstitial;
    Fragment fragment;
    Toolbar toolbar;
    FloatingActionButton fab;
    IMainPresenter _presenter;
    ProgressBar progress;
    Endless endless;
    AppBarLayout appBarLayout;

    ArrayList<JcAudio> listForSelect = new ArrayList<>();
    List<MediaMetaData> listForDownload = new ArrayList<>();

    JcPlayerView player;


    public boolean download = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(_presenter == null) _presenter = new MainPresenter(this, this);

        initVW();
        initSelectRel();
        updateCookies();
        _presenter.checkNewVersion();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        runOnUiThread(new Runnable() {
            public void run() {
                getMenuInflater().inflate(R.menu.menu_main, menu);
                MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
                final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        _presenter.getSearch(query, 0);

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        return true;
                    }
                });}});


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initVW() {
        runOnUiThread(new Runnable() {
            public void run() {
                container = (NestedScrollView) findViewById(R.id.container) ;
                progress = (ProgressBar) findViewById(R.id.progress);
                appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
                progress_n = (ProgressBar) findViewById(R.id.progress_n);
                img_expand_n = (ImageView) findViewById(R.id.img_expand_n);
                tv_name_n = (TextView) findViewById(R.id.tv_name_n);
                tv_progress_n = (TextView) findViewById(R.id.tv_progress_n);
                rel_n = (RelativeLayout) findViewById(R.id.rel_n);
                tv_path = (TextView) findViewById(R.id.tv_path);
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listForSelect.get(5);
                    }
                });
                player = (JcPlayerView) findViewById(R.id.jcplayer);

                img_expand_n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(rel_n.getVisibility() == View.VISIBLE) hideNotify();
                    }
                });


            }});

    }

    @Override
    public void checkLogin() {
        _presenter.isLogin();
    }

    @Override
    public void initSpinner() {

        runOnUiThread(new Runnable() {
            public void run() {
                appBarLayout.setVisibility(View.VISIBLE);
                player.setVisibility(View.VISIBLE);
                if(container != null) container.setVisibility(View.GONE);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                // Setup spinner
                Spinner spinner = (Spinner) findViewById(R.id.spinner);
                spinner.setAdapter(new MyAdapter(
                        toolbar.getContext(),
                        new String[]{
                                "Мои аудиозаписи",
                                "Специально для вас",
                                "Новинки",
                                "Популярное",
                                "Обновления друзей",
                                "Выбор папки",
                                "Выход",
                        }));

                spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // When the given dropdown item is selected, show its contents in the
                        // container view.
                        //fragment = new FragmentsLogin(MainActivity.this);
                        if(position == 0) _presenter.getMyAudio();
                        if(position == 1) _presenter.getSpecial();
                        if(position == 2) _presenter.getNews();
                        if(position == 3) _presenter.getPopular();
                        if(position == 4) _presenter.getUpdateFriends();
                        if(position == 5) showStorage();
                        if(position == 6) _presenter.exit();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });


            }
        });

    }

    @Override
    public void initWebView() {
        container.setVisibility(View.VISIBLE);
        fragment = new FragmentsLogin(MainActivity.this);
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void showProgress() {
        runOnUiThread(new Runnable() {
            public void run() {
                progress.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideProgress() {
        runOnUiThread(new Runnable() {
            public void run() {
                progress.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void showStorage() {
        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(MainActivity.this)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();
        chooser.show();
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                _presenter.setPath(path);
            }
        });
    }

    @Override
    public void hideRecycler() {

    }

    @Override
    public void showRecycler() {

    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(select_rel.getVisibility() == View.VISIBLE) {
                hideSelectRel();
                return true;
            }
            if(!_presenter.checkReview()){
                hide();
                showAds();

            }else {
                showNewReviewDialog();
                return true;
            }
        } return super.onKeyDown(keyCode, event);
    }



    @Override
    public void updateCookies() {

        showProgress();
        runOnUiThread(new Runnable() {
            public void run() {

                WebView webView = (WebView) findViewById(R.id.webView);
                webView.loadUrl("https://vk.com");
                webView.setWebViewClient(new WebViewClient() {
                    public void onPageFinished(WebView view, String url) {
                        CookieSyncManager.getInstance().sync();
                        hideProgress();
                        checkLogin();
                    }
                });

            }});

    }

    @Override
    public void setRecycler(ArrayList<JcAudio> list, TypeAudio typeAudio) {
        try {
            runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void run() {
                    if(list != null && list.size() != 0) {


                        listForSelect = list;
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                        select_recycler.setLayoutManager(layoutManager);
                        adapter = new AudioSelectAdapter(listForSelect, select_recycler.getContext());
                        select_recycler.setAdapter(adapter);
                        showSelectRel();
                        if(player != null && player.getListOfSongs() != null &&
                                player.getListOfSongs().size() == 0) initialiseListPlayer(list, false);
                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void hideSelectRel() {
        runOnUiThread(new Runnable() {
            public void run() {
                YoYo.with(Techniques.SlideOutDown)
                        .duration(600)
                        .withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                select_rel.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        })
                        .playOn(select_rel);

            }
        });

    }

    @Override
    public void showSelectRel() {
        player.hideRecycler();
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    if(select_rel.getVisibility() == View.GONE){
                        YoYo.with(Techniques.SlideInUp)
                                .duration(600)
                                .withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {
                                        select_rel.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animator) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animator) {

                                    }
                                })
                                .playOn(select_rel);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkPermission();
        ads();
    }

    @Override
    public void initSelectRel() {
        select_rel = (RelativeLayout) findViewById(R.id.rel_select_audio);
        select_rel_img = (RelativeLayout) findViewById(R.id.rel_hide_select);
        select_recycler = (RecyclerView) findViewById(R.id.recycler_select);

        select_rel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSelectRel();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void initialiseListPlayer(ArrayList<JcAudio> audios, boolean play) {

        List<MediaMetaData> listMusic = new ArrayList<>();

        for (JcAudio jc: audios
             ) {
            MediaMetaData mediaMetaData = new MediaMetaData();
            mediaMetaData.setMediaId(String.valueOf(jc.getId()));
            mediaMetaData.setMediaArtist(jc.getArtist_n());
            mediaMetaData.setMediaDuration("100");
            mediaMetaData.setMediaTitle(jc.getTitle_n());
            mediaMetaData.setMediaUrl(jc.getUrl());
            mediaMetaData.setMediaDuration(String.valueOf(jc.getDuration()));
            mediaMetaData.setMediaId(jc.getId_string());
            if(jc.getUrl() == null) mediaMetaData.setMediaUrl("");
            listMusic.add(mediaMetaData);
        }

        try {
            if(player != null) {
                if(play)player.setListAudio(listMusic);
                else {
                    player.setListAudio(listMusic);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void playAudioPosition(int position, ArrayList<JcAudio> audioArrayList) {
        initialiseListPlayer(audioArrayList, true);
        if(player != null) {
            MediaMetaData metaData = player.getMedia(position);
           if(metaData != null) {
               player.playSong(metaData);
           }
        }
        hideSelectRel();
        _presenter.checkCount();
    }

    @Override
    public void downloadAudio(MediaMetaData mediaMetaData, int type) {

        if(listForDownload.contains(mediaMetaData)) return;;
        _presenter.checkCount();

        if(!download){
            _presenter.downloadAudio(mediaMetaData, type);
        }
        else {
            addToDownloadList(mediaMetaData);
            Toast.makeText(MainActivity.this,
                    mediaMetaData.getMediaArtist()+" - "+
                            mediaMetaData.getMediaTitle()+" добавлена в загрузки", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMessageDownloadStatus(String name, int type) {
        String text = "";
        if(type == 1) text = "Начало загрузки " + name;
        else if(type == 2) text = "Загрузка "+ name+" завершена";
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 199);
        }
    }

    @Override
    public void showNotify() {
        if(rel_n.getVisibility() == View.GONE){
            runOnUiThread(new Runnable() {
                public void run() {
                    rel_n.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.SlideInDown)
                            .duration(1000)
                            .withListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {

                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            })
                            .playOn(rel_n);

                }
            });

            runOnUiThread(new Runnable() {
                public void run() {

                    YoYo.with(Techniques.SlideOutUp)
                            .duration(1000)
                            .withListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    appBarLayout.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            })
                            .playOn(appBarLayout);

                }
            });
        }


    }

    @Override
    public void hideNotify() {

        runOnUiThread(new Runnable() {
            public void run() {

                YoYo.with(Techniques.SlideOutUp)
                        .duration(1000)
                        .withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                rel_n.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        })
                        .playOn(rel_n);

            }
        });

        runOnUiThread(new Runnable() {
            public void run() {
                appBarLayout.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInDown)
                        .duration(1000)
                        .withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        })
                        .playOn(appBarLayout);

            }
        });

    }

    @Override
    public void setInfoNotify(MediaMetaData metaData, int progress, String path) {

        runOnUiThread(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {

                tv_path.setText("Сохраняем в "+path);
                tv_name_n.setText(metaData.getMediaTitle() + " - "+ metaData.getMediaArtist());
                progress_n.setProgress(progress);
                tv_progress_n.setText(String.valueOf(progress)+" %");

                if(progress > 0 && progress <= 20) {
                    tv_name_n.setTextColor(getResources().getColor(R.color.color_0));
                    tv_progress_n.setTextColor(getResources().getColor(R.color.color_0));
                }else if(progress > 20 && progress <= 40) {
                    tv_name_n.setTextColor(getResources().getColor(R.color.color_20));
                    tv_progress_n.setTextColor(getResources().getColor(R.color.color_20));
                }else if(progress > 40 && progress <= 60) {
                    tv_name_n.setTextColor(getResources().getColor(R.color.color_40));
                    tv_progress_n.setTextColor(getResources().getColor(R.color.color_40));
                }else if(progress > 60 && progress <= 80) {
                    tv_name_n.setTextColor(getResources().getColor(R.color.color_60));
                    tv_progress_n.setTextColor(getResources().getColor(R.color.color_60));
                }else if(progress > 80 && progress <= 100) {
                    tv_name_n.setTextColor(getResources().getColor(R.color.color_80));
                    tv_progress_n.setTextColor(getResources().getColor(R.color.color_80));
                }
            }
        });

    }

    @Override
    public boolean checkDownloadList() {

        if(listForDownload.size() == 0) return false;
        else return true;

    }

    @Override
    public void addToDownloadList(MediaMetaData metaData) {
        listForDownload.add(metaData);
    }

    @Override
    public void deleteFromDownloadList(MediaMetaData metaData) {
        listForDownload.remove(metaData);
        nextDownloadAudio();
    }

    @Override
    public void nextDownloadAudio() {
        if(checkDownloadList()) downloadAudio(listForDownload.get(0), 1);
    }

    @Override
    public void deleteDownloadList() {
        listForDownload.clear();
    }

    @Override
    public void setBooleanDownload(boolean b) {
        download = b;
    }

    @Override
    public void ads() {
        Random random = new Random();
        if(random.nextInt(4) == 1){
            MobileAds.initialize(this, getResources().getString(R.string.id_ad2));
            interstitial = new InterstitialAd(this);
            interstitial.setAdUnitId(getResources().getString(R.string.int2));
            AdRequest adRequesti = new AdRequest.Builder().build();
            interstitial.loadAd(adRequesti);
        }else {
            MobileAds.initialize(this, getResources().getString(R.string.id_ad1));
            interstitial = new InterstitialAd(this);
            interstitial.setAdUnitId(getResources().getString(R.string.int1));
            AdRequest adRequesti = new AdRequest.Builder().build();
            interstitial.loadAd(adRequesti);
        }
    }

    @Override
    public void checkReview() {
        _presenter.checkReview();
    }

    @Override
    public void showAds() {
        if (interstitial.isLoaded()) {
            interstitial.show();
            ads();

        }

    }


    @Override
    public void startNewVersion(String pakage) {

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pakage)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + pakage)));
        }

    }

    @Override
    public void showNewReviewDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Пять звезд.");
        alertDialog.setMessage("Понравилось приложние Поставь пять звезд. Поддержи разработчиков.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
        alertDialog.show();

    }

    @Override
    public void resetAllView() {

        if(player != null) player.setVisibility(View.GONE);
        select_rel.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        //container.setVisibility(View.GONE);

    }

    @Override
    public void hide() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        try {
            if(player != null) player.onstop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
           if(player != null) player.ondestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }
    }


}
