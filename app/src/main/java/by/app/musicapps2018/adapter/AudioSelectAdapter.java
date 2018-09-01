package by.app.musicapps2018.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import by.app.musicapps2018.R;
import by.app.musicapps2018.manager.MediaMetaData;
import by.app.musicapps2018.view.MainActivity;
import by.app.musicapps2018.view.jcplayer.JcAudio;

public class AudioSelectAdapter extends RecyclerView.Adapter<AudioSelectAdapter.AudioAdapterViewHolder>{

    public static final String TAG = AudioSelectAdapter.class.getSimpleName();

    private List<JcAudio> jcAudioList;
    private SparseArray<Float> progressMap = new SparseArray<>();

    private static OnItemClickListener mListener ;
    private static View.OnClickListener mDownload;
    public MainActivity activity;

    // Define the mListener interface
    public interface OnItemClickListener {
        void onItemClick(int position);

        void onSongItemDeleteClicked(int position);
    }
    public interface OnClickListener {
        void onClickDownload(View v);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnClickDownload(View.OnClickListener listener) {
        this.mDownload = listener;
    }

    public AudioSelectAdapter(List<JcAudio> jcAudioList, Context context) {
        this.jcAudioList = jcAudioList;
        activity = (MainActivity) context;
        //setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return jcAudioList.get(position).getId();
    }

    @Override
    public AudioAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_select, parent, false);
        return new AudioAdapterViewHolder(view);
//        audiosViewHolder.itemView.setOnClickListener(this);
//        return audiosViewHolder;
    }

    public void setItems(List<JcAudio> list){
        jcAudioList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(AudioAdapterViewHolder holder, final int position) {

        try {
            JcAudio audio = jcAudioList.get(position);
            //audio.getSongUrl();
            holder.audioArtist.setText(audio.getArtist_n());
            holder.audioTitle.setText(audio.getTitle_n());
            holder.view.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {

                    activity.playAudioPosition(position, (ArrayList<JcAudio>) jcAudioList);
                }
            });

            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MediaMetaData mediaMetaData = new MediaMetaData();
                    mediaMetaData.setMediaId(String.valueOf(audio.getId()));
                    mediaMetaData.setMediaArtist(audio.getArtist_n());
                    mediaMetaData.setMediaTitle(audio.getTitle_n());
                    mediaMetaData.setMediaUrl(audio.getUrl());
                    mediaMetaData.setMediaDuration(String.valueOf(audio.getDuration()));
                    mediaMetaData.setMediaId(audio.getId_string());
                    if(audio.getUrl() == null) mediaMetaData.setMediaUrl("");

                    activity.downloadAudio(mediaMetaData, 1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Applying percentage to progress.
     * @param holder ViewHolder
     * @param percentage in float value. where 1 is equals as 100%
     */
    private void applyProgressPercentage(AudioAdapterViewHolder holder, float percentage) {

    }
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;


    @Override
    public int getItemCount() {
        return jcAudioList == null ? 0 : jcAudioList.size();
    }

    public void updateProgress(JcAudio jcAudio, float progress) {
        int position = jcAudioList.indexOf(jcAudio);
        Log.d(TAG, "Progress = " + progress);


        progressMap.put(position, progress);
        if(progressMap.size() > 1) {
            for(int i = 0; i < progressMap.size(); i++) {
                if(progressMap.keyAt(i) != position) {
                    Log.d(TAG, "KeyAt(" + i + ") = " + progressMap.keyAt(i));
                    notifyItemChanged(progressMap.keyAt(i));
                    progressMap.delete(progressMap.keyAt(i));
                }
            }
        }
        notifyItemChanged(position);
    }

    static class AudioAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView audioTitle;
        private TextView audioArtist;
        private LinearLayout view;
        private ImageView img;


        public AudioAdapterViewHolder(View view){
            super(view);
            this.audioArtist = (TextView) view.findViewById(R.id.item_artist);
            this.audioTitle = (TextView) view.findViewById(R.id.item_title);
            this.view = (LinearLayout) view.findViewById(R.id.view_p);
            this.img = (ImageView) view.findViewById(R.id.img_more);
        }
    }

}
