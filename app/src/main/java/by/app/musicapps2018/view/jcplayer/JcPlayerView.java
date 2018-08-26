package by.app.musicapps2018.view.jcplayer;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.List;

import by.app.musicapps2018.R;
import by.app.musicapps2018.adapter.AudioAdapter;
import by.app.musicapps2018.manager.AudioStreamingManager;
import by.app.musicapps2018.manager.CurrentSessionCallback;
import by.app.musicapps2018.manager.Logger;
import by.app.musicapps2018.manager.MediaMetaData;
import by.app.musicapps2018.view.MainActivity;

public class JcPlayerView extends LinearLayout implements
        View.OnClickListener, SeekBar.OnSeekBarChangeListener, CurrentSessionCallback {

    Context context;
    private AudioStreamingManager streamingManager;
    private MediaMetaData currentSong;
    private List<MediaMetaData> listOfSongs = new ArrayList<MediaMetaData>();
    Handler h;

    //----------------------------


    private static final String TAG = JcPlayerView.class.getSimpleName();

    private static OnClickListener mListener;

    private static final int PULSE_ANIMATION_DURATION = 400;
    private static final int TITLE_ANIMATION_DURATION = 600;

    private TextView txtCurrentMusic;
    private TextView txtCurrentMusicTitle;
    private ImageButton btnPrev;
    private ImageButton btnPlay;
    private ProgressBar progressBarPlayer;
    ProgressBar progressBarMini;
    private TextView txtDuration;
    private ImageButton btnNext;
    private SeekBar seekBar;
    private TextView txtCurrentDuration;
    private boolean isInitialized;
    ImageView btnPlayMini, btnNextMini;
    TextView txtCurrentMusicMini;
    public RelativeLayout relMini, relPlayer;
    private boolean isMini = true;
    MainActivity activity;
    ImageView btm_list;
    ImageView btn_folder;
    ImageView img;
    RecyclerView recycler;
    RelativeLayout rel_recucler, rel_hide, rel_hide_mini;
//    AvatarView avatarView;
//    IImageLoader imageLoader1;
    //SeekBar holoCircleSeekBar;
//
//    private AdView mAdView;
    AudioAdapter adapter;

    public JcPlayerView(Context context) {
        super(context);
        init();
    }

    public JcPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public JcPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    private void init() {
        try {
            inflate(getContext(), R.layout.view_jcplayer, this);

            activity = (MainActivity) getContext();
            context = getContext();
            h = new Handler();
            try {
                if (streamingManager != null) {
                    streamingManager.subscribesCallBack(this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            configAudioStreamer();


            this.progressBarPlayer = (ProgressBar) findViewById(R.id.progress_bar_player);
            this.progressBarMini = (ProgressBar) findViewById(R.id.progressBar3);
            this.btnNext = (ImageButton) findViewById(R.id.btn_next);
            this.btnPrev = (ImageButton) findViewById(R.id.btn_prev);
            this.btnPlay = (ImageButton) findViewById(R.id.btn_play);
            this.btnPlayMini = (ImageView) findViewById(R.id.btn_play_mini);
            this.btnNextMini = (ImageView) findViewById(R.id.btn_next_mini);
            this.txtDuration = (TextView) findViewById(R.id.txt_total_duration);
            this.txtCurrentDuration = (TextView) findViewById(R.id.txt_current_duration);
            this.txtCurrentMusic = (TextView) findViewById(R.id.txt_current_music);
            this.txtCurrentMusicTitle = (TextView) findViewById(R.id.txt_current_music_title);
            this.txtCurrentMusicMini = (TextView) findViewById(R.id.text_current_music_mini);
            this.seekBar = (SeekBar) findViewById(R.id.seek_bar);
            //this.holoCircleSeekBar = (HoloCircleSeekBar) findViewById(R.id.picker);
            this.btnPlay.setTag(R.drawable.ic_play_white);
            this.relMini = (RelativeLayout) findViewById(R.id.relPlayerMini);
            this.relPlayer = (RelativeLayout) findViewById(R.id.relPlaing) ;
            this.btm_list = (ImageView) findViewById(R.id.btn_list);
            this.btn_folder = (ImageView) findViewById(R.id.btn_folder);
            this.img = (ImageView) findViewById(R.id.img_bg);
            this.recycler = (RecyclerView) findViewById(R.id.recycler);
            this.rel_recucler = (RelativeLayout) findViewById(R.id.rel_recycler);
            this.rel_hide = (RelativeLayout) findViewById(R.id.rel_hide_select_jc);
            this.rel_hide_mini = (RelativeLayout) findViewById(R.id.rel_hide_select_mini);

            Glide.with(this.img.getContext()).load(R.drawable.image).into(this.img);

            btnNext.setOnClickListener(this);
            btnPrev.setOnClickListener(this);
            btnPlay.setOnClickListener(this);
            btnPlayMini.setOnClickListener(this);
            btnNextMini.setOnClickListener(this);
            seekBar.setOnSeekBarChangeListener(this);
            relMini.setOnClickListener(this);
            btm_list.setOnClickListener(this);
            btn_folder.setOnClickListener(this);
            rel_hide.setOnClickListener(this);
            rel_hide_mini.setOnClickListener(this);

            isInitialized = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Runnable showProgress = new Runnable() {
        @Override
        public void run() {

            setSeekBar(streamingManager.audioPlayback.getCurrentStreamPosition());
            setPGTime(streamingManager.audioPlayback.getCurrentStreamPosition());
            h.postDelayed(showProgress, 1000);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (isInitialized) {
            if (view.getId() == R.id.btn_play) {

                if(this.currentSong == null) {
                    if(listOfSongs != null) this.currentSong = listOfSongs.get(0);
                    else return;
                }

                if (streamingManager.isPlaying()) {
                    btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_24dp));

                    streamingManager.onPause();
                }else {
                    btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));
//                    YoYo.with(Techniques.Landing)
//                            .duration(PULSE_ANIMATION_DURATION)
//                            .playOn(btnPlay);
                    playSong(currentSong);
                }

            }

            if (view.getId() == R.id.btn_play_mini) {
                YoYo.with(Techniques.Landing)
                        .duration(PULSE_ANIMATION_DURATION)
                        .playOn(btnPlayMini);
            }
        }
        if (view.getId() == R.id.btn_next) {
            YoYo.with(Techniques.Landing)
                    .duration(PULSE_ANIMATION_DURATION)
                    .playOn(btnNext);
            streamingManager.onSkipToNext();

        }
        if (view.getId() == R.id.btn_next_mini) {
            YoYo.with(Techniques.Landing)
                    .duration(PULSE_ANIMATION_DURATION)
                    .playOn(btnNext);

        }

        if (view.getId() == R.id.btn_prev) {
            YoYo.with(Techniques.Landing)
                    .duration(PULSE_ANIMATION_DURATION)
                    .playOn(btnPrev);
            streamingManager.onSkipToPrevious();

        }
        if(view.getId()== R.id.relPlayerMini){


        }
        if(view.getId() == R.id.btn_list){
            YoYo.with(Techniques.Landing)
                    .duration(PULSE_ANIMATION_DURATION)
                    .playOn(btm_list);
            showMini();
        }
        if(view.getId() == R.id.btn_folder){
            YoYo.with(Techniques.Landing)
                    .duration(PULSE_ANIMATION_DURATION)
                    .playOn(btn_folder);
            activity.showStorage();
        }

        if(view.getId() == R.id.rel_hide_select_mini){
            if(listOfSongs != null && listOfSongs.size() != 0 ) showRecycler();
        }

        if(view.getId() == R.id.rel_hide_select_jc){
            hideRecycler();
        }
    }

    public void showMini() {

        isMini = true;
        relMini.setVisibility(VISIBLE);
        relPlayer.setVisibility(GONE);
        activity.showRecycler();
        YoYo.with(Techniques.Landing)
                .duration(400)
                .playOn(relMini);

    }

    public void hideMini() {

        isMini = false;
        relMini.setVisibility(GONE);
        relPlayer.setVisibility(VISIBLE);
        activity.hideRecycler();
        YoYo.with(Techniques.Landing)
                .duration(1200)
                .playOn(relPlayer);

    }

    /**
     * Create a notification player with same playlist with a custom icon.
     *
     * @param iconResource icon path.
     */
    public void createNotification(int iconResource) {

    }

    /**
     * Create a notification player with same playlist with a default icon
     */
    public void createNotification() {

    }


    private void sortPlaylist(List<JcAudio> playlist) {
        for (int i = 0; i < playlist.size(); i++) {
            JcAudio jcAudio = playlist.get(i);
            jcAudio.setId(i);
            jcAudio.setPosition(i);
        }
    }

    /**
     * Check if playlist already sorted or not.
     * We need to check because there is a possibility that the user reload previous playlist
     * from persistence storage like sharedPreference or SQLite.
     *
     * @param playlist list of JcAudio
     * @return true if sorted, false if not.
     */
    private boolean isAlreadySorted(List<JcAudio> playlist) {
        // If there is position in the first audio, then playlist is already sorted.
        if (playlist != null) {
            if (playlist.get(0).getPosition() != -1) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void generateTitleAudio(List<JcAudio> playlist, String title) {
        for (int i = 0; i < playlist.size(); i++) {
            if (title.equals(getContext().getString(R.string.track_number))) {
                playlist.get(i).setTitle(getContext().getString(R.string.track_number) + " " + String.valueOf(i + 1));
            } else {
                playlist.get(i).setTitle(title);
            }
        }
    }

    public void showProgressBar() {
        progressBarPlayer.setVisibility(ProgressBar.VISIBLE);
        progressBarMini.setVisibility(ProgressBar.VISIBLE);
        btnPlay.setVisibility(Button.GONE);
        btnPlayMini.setVisibility(View.GONE);
        btnNext.setClickable(false);
        btnPrev.setClickable(false);
    }

    public void dismissProgressBar() {
        progressBarPlayer.setVisibility(ProgressBar.GONE);
        progressBarMini.setVisibility(ProgressBar.GONE);
        btnPlay.setVisibility(Button.VISIBLE);
        btnPlayMini.setVisibility(View.VISIBLE);
        btnNext.setClickable(true);
        btnPrev.setClickable(true);
    }

    private void resetPlayerInfo() {
        seekBar.setProgress(0);
        //holoCircleSeekBar.setValue(0);
        txtCurrentMusic.setText("");
        txtCurrentMusicTitle.setText("");
        txtCurrentMusicMini.setText("");
        txtCurrentDuration.setText(getContext().getString(R.string.play_initial_time));
        txtDuration.setText(getContext().getString(R.string.play_initial_time));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        if(fromUser){
            streamingManager.onSeekTo(i);
            streamingManager.scheduleSeekBarUpdate();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        showProgressBar();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        dismissProgressBar();
    }


    @Override
    public void updatePlaybackState(int state) {
        Logger.e("updatePlaybackState: ", "" + state);
        for (MediaMetaData m: listOfSongs
             ) {
            m.setPlayState(PlaybackStateCompat.STATE_STOPPED);
        }
        adapter.notifyDataSetChanged();
        switch (state) {
            case PlaybackStateCompat.STATE_PLAYING:
                //pgPlayPauseLayout.setVisibility(View.INVISIBLE);
                btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));
                //btn_play.Play();
                YoYo.with(Techniques.Landing)
                        .duration(PULSE_ANIMATION_DURATION)
                        .playOn(btnPlay);
                if (currentSong != null) {
                    currentSong.setPlayState(PlaybackStateCompat.STATE_PLAYING);
                    h.post(showProgress);

                    notifyAdapter(currentSong);
                }
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_24dp));
                //pgPlayPauseLayout.setVisibility(View.INVISIBLE);
                //btn_play.Pause();
                YoYo.with(Techniques.Landing)
                        .duration(PULSE_ANIMATION_DURATION)
                        .playOn(btnPlay);
                if (currentSong != null) {
                    currentSong.setPlayState(PlaybackStateCompat.STATE_PAUSED);
                    notifyAdapter(currentSong);
                }
                break;
            case PlaybackStateCompat.STATE_NONE:
                currentSong.setPlayState(PlaybackStateCompat.STATE_NONE);
                notifyAdapter(currentSong);
                break;
            case PlaybackStateCompat.STATE_STOPPED:
               // pgPlayPauseLayout.setVisibility(View.INVISIBLE);
                btnPlay.setBackground(getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));
                //btn_play.Pause();
                //audioPg.setValue(0);
                seekBar.setProgress(0);
                if (currentSong != null) {
                    currentSong.setPlayState(PlaybackStateCompat.STATE_NONE);
                    //notifyAdapter(currentSong);
                }
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                //pgPlayPauseLayout.setVisibility(View.VISIBLE);
                if (currentSong != null) {
                    currentSong.setPlayState(PlaybackStateCompat.STATE_NONE);
                    //notifyAdapter(currentSong);
                }
                break;
        }
    }

    private void notifyAdapter(MediaMetaData currentSong) {

        if(adapter != null){
            adapter.notifyPlayState(currentSong);
        }

    }

    @Override
    public void playSongComplete() {

        String timeString = "00.00";
        txtDuration.setText(timeString);
        txtCurrentDuration.setText(timeString);
        //time_progress_bottom.setText(timeString);
        //time_progress_slide.setText(timeString);
        //lineProgress.setLineProgress(0);
        seekBar.setProgress(0);

    }

    @Override
    public void currentSeekBarPosition(int progress) {
        seekBar.setProgress(progress);
        setPGTime(progress);
    }

    public void setSeekBar(int pos){
        seekBar.setProgress(pos);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void playCurrent(int indexP, MediaMetaData currentAudio) {
        showMediaInfo(currentAudio);
        //notifyAdapter(currentAudio);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void playNext(int indexP, MediaMetaData currentAudio) {
        showMediaInfo(currentAudio);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void playPrevious(int indexP, MediaMetaData currentAudio) {
        showMediaInfo(currentAudio);
    }

    public void onstop(){
        if (streamingManager != null) {
            streamingManager.unSubscribeCallBack();
        }
    }
    public void ondestroy(){
        if (streamingManager != null) {
            streamingManager.unSubscribeCallBack();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showMediaInfo(MediaMetaData media) {
        try {
            currentSong = media;
            txtCurrentMusic.setText(media.getMediaArtist());
            txtCurrentMusicTitle.setText(media.getMediaTitle());
            seekBar.setProgress(0);
            //seekBar.setMin(0);
            seekBar.setMax(Integer.valueOf(media.getMediaDuration()) * 1000);
            setPGTime(0);
            setMaxTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //loadSongDetails(media);
    }

    private void setMaxTime() {
        try {
            String timeString = DateUtils.formatElapsedTime(Long.parseLong(currentSong.getMediaDuration()));
            txtDuration.setText(timeString);
            //time_total_slide.setText(timeString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void setPGTime(int progress) {
        try {
            String timeString = "00.00";
            int linePG = 0;
            currentSong = streamingManager.getCurrentAudio();
            if (currentSong != null && progress != Long.parseLong(currentSong.getMediaDuration())) {
                timeString = DateUtils.formatElapsedTime(progress / 1000);
                Long audioDuration = Long.parseLong(currentSong.getMediaDuration());
                linePG = (int) (((progress / 1000) * 100) / audioDuration);
            }
            txtCurrentDuration.setText(timeString);
            //time_progress_slide.setText(timeString);
            //lineProgress.setLineProgress(linePG);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void playSong(MediaMetaData media) {
        if (streamingManager != null) {
            streamingManager.onPlay(media);
        }
    }

    private void configAudioStreamer() {
        streamingManager = AudioStreamingManager.getInstance(context);
        streamingManager.setPlayer(this);
        //Set PlayMultiple 'true' if want to playing sequentially one by one songs
        // and provide the list of songs else set it 'false'
        streamingManager.setPlayMultiple(true);
        streamingManager.setMediaList(listOfSongs);
        //If you want to show the Player Notification then set ShowPlayerNotification as true
        //and provide the pending intent so that after click on notification it will redirect to an activity
        streamingManager.setShowPlayerNotification(true);
        streamingManager.setPendingIntentAct(getNotificationPendingIntent());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkAlreadyPlaying() {
        if (streamingManager.isPlaying()) {
            currentSong = streamingManager.getCurrentAudio();
            if (currentSong != null) {
                currentSong.setPlayState(streamingManager.mLastPlaybackState);
                showMediaInfo(currentSong);
                //notifyAdapter(currentSong);
            }
        }
    }

    private PendingIntent getNotificationPendingIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction("openplayer");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return mPendingIntent;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setListAudio(List<MediaMetaData> listMusic){
        listOfSongs = listMusic;
        configAudioStreamer();
        checkAlreadyPlaying();
        setRecycler(listOfSongs);
    }

    public List<MediaMetaData> getListOfSongs() {
        return listOfSongs;
    }

    public MediaMetaData getMedia(int position){
        if(listOfSongs != null && listOfSongs.size() != 0) return listOfSongs.get(position);
        else return null;
    }

    public void setRecycler(List<MediaMetaData> list) {
        try {
            activity.runOnUiThread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void run() {
                    if(list != null && list.size() != 0) {
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                        recycler.setLayoutManager(layoutManager);
                        adapter = new AudioAdapter(context, list, JcPlayerView.this);
                        recycler.setAdapter(adapter);

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showRecycler(){

        try {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if(rel_recucler.getVisibility() == View.GONE){
                        YoYo.with(Techniques.SlideInRight)
                                .duration(600)
                                .withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animator) {
                                        rel_recucler.setVisibility(View.VISIBLE);
                                        rel_hide_mini.setVisibility(GONE);
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
                                .playOn(rel_recucler);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void hideRecycler(){

        activity.runOnUiThread(new Runnable() {
            public void run() {
                YoYo.with(Techniques.SlideOutRight)
                        .duration(600)
                        .withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {
                                rel_recucler.setVisibility(View.GONE);
                                rel_hide_mini.setVisibility(VISIBLE);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        })
                        .playOn(rel_recucler);

            }
        });

    }

    public void download(MediaMetaData mediaMetaData, int type){
        activity.downloadAudio(mediaMetaData, type);
    }

}