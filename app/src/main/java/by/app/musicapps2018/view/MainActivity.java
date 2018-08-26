package by.app.musicapps2018.view;

import android.Manifest;
import android.animation.Animator;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

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


    Fragment fragment;
    Toolbar toolbar;
    FloatingActionButton fab;
    IMainPresenter _presenter;
    ProgressBar progress;
    Endless endless;

    ArrayList<JcAudio> listForSelect = new ArrayList<>();
    JcPlayerView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(_presenter == null) _presenter = new MainPresenter(this, this);

        initVW();
        initSelectRel();
        updateCookies();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initVW() {
        runOnUiThread(new Runnable() {
            public void run() {
                container = (NestedScrollView) findViewById(R.id.container) ;
                progress = (ProgressBar) findViewById(R.id.progress);

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
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                //_presenter.getMyAudio();
                //_presenter.getMyAudioAfter100(listForSelect.size());
                //_presenter.getSpecial();
                //_presenter.getNews();
                //_presenter.getPopular();
                //_presenter.getUpdateFriends();
                //_presenter.getSearch("Егор Крид", 0);
            }
        });

    }

    @Override
    public void initWebView() {
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

    @Override
    public void onBackPressed() {
        if(select_rel.getVisibility() == View.VISIBLE) hideSelectRel();
        else {
            super.onBackPressed();
            finish();
        }
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
                                player.getListOfSongs().size() == 0) initialiseListPlayer(list);
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
    public void initialiseListPlayer(ArrayList<JcAudio> audios) {

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

        if(player != null) player.setListAudio(listMusic);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void playAudioPosition(int position, ArrayList<JcAudio> audioArrayList) {
        initialiseListPlayer(audioArrayList);
        if(player != null) {
            MediaMetaData metaData = player.getMedia(position);
           if(metaData != null) {
               player.playSong(metaData);
           }
        }
        hideSelectRel();

    }

    @Override
    public void downloadAudio(MediaMetaData mediaMetaData, int type) {
        _presenter.downloadAudio(mediaMetaData, type);
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
