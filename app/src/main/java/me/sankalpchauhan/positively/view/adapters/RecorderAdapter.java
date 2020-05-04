package me.sankalpchauhan.positively.view.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.service.model.VoiceLog;
import timber.log.Timber;

public class RecorderAdapter extends RecyclerView.Adapter<RecorderAdapter.RecordHolder> {
    private int last_index = -1;
    private boolean isPlaying = false;
    private List<VoiceLog> voiceLogList = new ArrayList<>();
    private MediaPlayer mPlayer;

    @NonNull
    @Override
    public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.recorder_item, parent, false);
        return new RecordHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordHolder holder, int position) {
        VoiceLog voiceLog = voiceLogList.get(position);
        holder.recodingTitle.setText(voiceLog.getFileName());

        if (voiceLog.isPlaying()) {
            holder.playPause.setImageResource(R.drawable.ic_pause_black_24dp);
            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
            holder.seekBar.setVisibility(View.VISIBLE);
            holder.seekUpdation(holder);
        } else {
            holder.playPause.setImageResource(R.drawable.ic_play_arrow_accent_24dp);
            TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView);
            holder.seekBar.setVisibility(View.GONE);
        }

        holder.manageSeekBar(holder);
    }

    @Override
    public int getItemCount() {
        if (voiceLogList == null) {
            return 0;
        }
        return voiceLogList.size();
    }

    public void setVoiceLogData(List<VoiceLog> voiceLogData) {
        voiceLogList = voiceLogData;
        notifyDataSetChanged();
    }


    public class RecordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.play_pause)
        ImageView playPause;
        @BindView(R.id.recording_title)
        TextView recodingTitle;
        @BindView(R.id.seekbar)
        SeekBar seekBar;
        private Handler mHandler = new Handler();
        private int previousProgress = 0;
        private RecordHolder holder;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                seekUpdation(holder);
            }
        };
        private String voiceLogUri;

        public RecordHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            playPause.setOnClickListener(this);
            recodingTitle.setOnClickListener(this);
        }

        private void startPlaying(final VoiceLog audio, final int position) {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(voiceLogUri);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Timber.e("prepare() failed");
            }
            //showing the pause button
            seekBar.setMax(mPlayer.getDuration());
            isPlaying = true;

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    audio.setPlaying(false);
                    notifyItemChanged(position);
                }
            });
        }

        private void markAllPaused() {
            for (int i = 0; i < voiceLogList.size(); i++) {
                voiceLogList.get(i).setPlaying(false);
                voiceLogList.set(i, voiceLogList.get(i));
            }
            notifyDataSetChanged();
        }

        private void stopPlaying() {
            try {
                mPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPlayer = null;
            isPlaying = false;
        }

        public void manageSeekBar(RecordHolder holder) {
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mPlayer != null && fromUser) {
                        mPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        private void seekUpdation(RecordHolder holder) {
            this.holder = holder;
            if (mPlayer != null) {
                int mCurrentPosition = mPlayer.getCurrentPosition();
                holder.seekBar.setMax(mPlayer.getDuration());
                holder.seekBar.setProgress(mCurrentPosition);
                previousProgress = mCurrentPosition;
            }
            mHandler.postDelayed(runnable, 100);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            VoiceLog voiceLog = voiceLogList.get(position);

            voiceLogUri = voiceLog.getUri();
            if (isPlaying) {
                stopPlaying();
                if (position == last_index) {
                    voiceLog.setPlaying(false);
                    stopPlaying();
                    notifyItemChanged(position);
                } else {
                    markAllPaused();
                    voiceLog.setPlaying(true);
                    notifyItemChanged(position);
                    startPlaying(voiceLog, position);
                    last_index = position;
                }

            } else {
                if (voiceLog.isPlaying()) {
                    voiceLog.setPlaying(false);
                    stopPlaying();
                    Timber.d("isPlayin: True");
                } else {
                    startPlaying(voiceLog, position);
                    voiceLog.setPlaying(true);
                    seekBar.setMax(mPlayer.getDuration());
                    Timber.d("isPlayin: False");
                }
                notifyItemChanged(position);
                last_index = position;
            }
        }
    }
}
