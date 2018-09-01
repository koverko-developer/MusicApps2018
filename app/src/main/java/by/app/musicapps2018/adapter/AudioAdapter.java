package by.app.musicapps2018.adapter;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.List;

import by.app.musicapps2018.R;
import by.app.musicapps2018.manager.MediaMetaData;
import by.app.musicapps2018.view.jcplayer.JcPlayerView;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioAdapterViewHolder>{

    Context context;
    private List<MediaMetaData> musicList;
    JcPlayerView jcPlayerView;

    public AudioAdapter(Context context, List<MediaMetaData> musicList,
                        JcPlayerView jcPlayerView) {
        this.context = context;
        this.musicList = musicList;
        this.jcPlayerView = jcPlayerView;
    }

    @NonNull
    @Override
    public AudioAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_adapter, parent, false);
        return new AudioAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioAdapterViewHolder holder, int position) {

        MediaMetaData mediaMetaData = musicList.get(position);

        holder.audioArtist.setText(mediaMetaData.getMediaArtist());
        holder.audioTitle.setText(mediaMetaData.getMediaTitle());

        setImageAnim(holder.img_p, mediaMetaData.getPlayState());

        holder.img_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jcPlayerView.download(mediaMetaData, 1);
            }
        });

        holder.img_p.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                jcPlayerView.playSong(mediaMetaData);
            }
        });

        holder.view_p.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                jcPlayerView.playSong(mediaMetaData);
            }
        });
    }


    public void notifyPlayState(MediaMetaData metaData) {
        if (this.musicList != null && metaData != null) {
            int index = this.musicList.indexOf(metaData);
            //TODO SOMETIME INDEX RETURN -1 THOUGH THE OBJECT PRESENT IN THIS LIST
            if (index == -1) {
                for (int i = 0; i < this.musicList.size(); i++) {
                    if (this.musicList.get(i).getMediaId().equalsIgnoreCase(metaData.getMediaId())) {
                        index = i;
                        break;
                    }
                }
            }
            if (index > 0 && index < this.musicList.size()) {
                this.musicList.set(index, metaData);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    private void setImageAnim(ImageView imageView,  int state) {
        switch (state) {
            case PlaybackStateCompat.STATE_NONE:
                imageView.setVisibility(View.GONE);
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                imageView.setVisibility(View.GONE);
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.image_7));
                YoYo.with(Techniques.Pulse)
                        .repeat(1000000)
                        .duration(1000)
                        .playOn(imageView);
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.ic_pause_black));
                break;
        }
    }

    static class AudioAdapterViewHolder extends RecyclerView.ViewHolder{
        private TextView audioTitle;
        private TextView audioArtist;
        private LinearLayout view;
        private LinearLayout view_p;
        ImageView img_p, img_more;


        public AudioAdapterViewHolder(View view){
            super(view);
            this.audioArtist = (TextView) view.findViewById(R.id.item_artist);
            this.audioTitle = (TextView) view.findViewById(R.id.item_title);
            this.view = (LinearLayout) view.findViewById(R.id.veiw);
            this.view_p = (LinearLayout) view.findViewById(R.id.view_play);
            this.img_p = (ImageView) view.findViewById(R.id.img_play);
            this.img_more = (ImageView) view.findViewById(R.id.img_more);
        }
    }

}
