package me.sankalpchauhan.positively.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.config.Constants;
import me.sankalpchauhan.positively.service.model.Podcast;
import timber.log.Timber;

import static me.sankalpchauhan.positively.config.Constants.PLAY_BACK_POSITION;
import static me.sankalpchauhan.positively.config.Constants.PLAY_WHEN_READY;
import static me.sankalpchauhan.positively.utils.utility.getUserAgent;

public class PodcastPlaybackActivity extends AppCompatActivity implements Player.EventListener {
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
    private Podcast podcast;
    ExoPlayer mExoPlayer;
    private String userAgent;
    @BindView(R.id.video_view)
    PlayerView mExoPlayerView;
    @BindView(R.id.controls)
    PlayerControlView controlView;
    @BindView(R.id.podcast_parent)
    ConstraintLayout constraintLayout;
    @BindView(R.id.podcast_title)
    TextView podcastTitle;
    @BindView(R.id.up_navigation)
    FloatingActionButton upNavigation;
    private Cache downloadCache;
    private File downloadDirectory;
    private DatabaseProvider databaseProvider;
    private long playBackPosition = 0L;
    private boolean playWhenReady = true;
    private Bitmap thumbnail;
    private int backdropColor;
    private PlayerNotificationManager playerNotificationManager;
    private int notificationId = 1234;
    private PlayerNotificationManager.MediaDescriptionAdapter mediaDescriptionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent catchIntent = getIntent();
        if(catchIntent.hasExtra(Constants.PODCAST_DATA)){
            podcast = (Podcast) catchIntent.getSerializableExtra(Constants.PODCAST_DATA);
            mediaDescriptionAdapter = new PlayerNotificationManager.MediaDescriptionAdapter() {
                @Override
                public String getCurrentSubText(Player player) {
                    return podcast.getPublisherOriginal();
                }

                @Override
                public String getCurrentContentTitle(Player player) {
                    return podcast.getTitleOriginal();
                }

                @Override
                public PendingIntent createCurrentContentIntent(Player player) {
                    return null;
                }

                @Override
                public String getCurrentContentText(Player player) {
                    return "Positive.ly Podcast";
                }

                @Override
                public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                    return null;
                }
            };
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_playback);
        ButterKnife.bind(this);
        userAgent = getUserAgent(this, "Positive.ly");
        if(podcast.getTitleOriginal()!=null) {
            podcastTitle.setText(podcast.getTitleOriginal());
        }
        upNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(podcast.getThumbnail()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                thumbnail = bitmap;
                Palette.from(bitmap)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@Nullable Palette palette) {
                                Palette.Swatch backdropSwtch1 = palette.getLightVibrantSwatch();
                                Palette.Swatch backdropSwtch2 = palette.getDarkMutedSwatch();
                                if(backdropSwtch1==null || backdropSwtch2==null){
                                    Timber.e("Test");
                                    GradientDrawable gd1 = new GradientDrawable(
                                            GradientDrawable.Orientation.TOP_BOTTOM,
                                            new int[] {Color.rgb(116,209,77),Color.rgb(56,120,29)});
                                    gd1.setCornerRadius(0f);
                                    preInitializePlayer();
                                    constraintLayout.setBackground(gd1);
                                    return;
                                }
                                GradientDrawable gd2 = new GradientDrawable(
                                        GradientDrawable.Orientation.TOP_BOTTOM,
                                        new int[] {backdropSwtch1.getRgb(),backdropSwtch2.getRgb()});
                                gd2.setCornerRadius(0f);
                                constraintLayout.setBackground(gd2);
                                upNavigation.setBackgroundColor(backdropSwtch1.getRgb());

                            }
                        });
                preInitializePlayer();

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                GradientDrawable gd1 = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[] {Color.rgb(116,209,77),Color.rgb(56,120,29)});
                gd1.setCornerRadius(0f);
                preInitializePlayer();
                constraintLayout.setBackground(gd1);
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Will be destroyed if user navigates out of the activity
     * or closes the app
     */
    @Override
    protected void onDestroy() {
        releaseExoplayer();
        super.onDestroy();
    }

    public void preInitializePlayer(){
        mExoPlayerView.setVisibility(View.GONE);
        if (!podcast.getAudio().isEmpty()) {
            initializePlayer();
        } else {
            //progressBar.setVisibility(View.GONE);
        }
    }


    private void initializePlayer() {
        mExoPlayerView.setVisibility(View.VISIBLE);
        if (mExoPlayer == null) {
            mExoPlayerView.setVisibility(View.VISIBLE);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
            prepareExoPlayer();
            mExoPlayerView.setShowBuffering(true);
            mExoPlayerView.setUseArtwork(true);
            mExoPlayerView.setDefaultArtwork(thumbnail);
            mExoPlayerView.setShutterBackgroundColor(backdropColor);
            controlView.setPlayer(mExoPlayer);
            controlView.setBackgroundColor(backdropColor);
            mExoPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.seekTo(playBackPosition);
            mExoPlayer.setPlayWhenReady(true);
            mExoPlayer.addListener(this);
            playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(this, "My_channel_id", R.string.channel_name, notificationId, mediaDescriptionAdapter, new PlayerNotificationManager.NotificationListener() {
                @Override
                public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                }

                @Override
                public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                }
            });
            playerNotificationManager.setPlayer(mExoPlayer);

        }
    }

    private void prepareExoPlayer() {
        Uri uri = Uri.parse(podcast.getAudio());
        String userAgent = Util.getUserAgent(this, "Positive.ly");
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                null /* listener */,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true /* allowCrossProtocolRedirects */
        );

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this,
                null /* listener */,
                httpDataSourceFactory
        );
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        mExoPlayer.prepare(mediaSource);
    }

    private void releaseExoplayer() {
        if (mExoPlayer != null) {
            playBackPosition = mExoPlayer.getCurrentPosition();
            playWhenReady = mExoPlayer.getPlayWhenReady();
            mExoPlayer.stop();
            playerNotificationManager.setPlayer(null);
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Timber.d("Loading Listener: " + isLoading);

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//        progressBar.setVisibility(View.INVISIBLE);
//        if (playbackState == Player.STATE_BUFFERING) {
//            progressBar.setVisibility(View.VISIBLE);
//        } else if (playbackState == Player.STATE_READY) {
//            progressBar.setVisibility(View.INVISIBLE);
//        }

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
//        mExoPlayerView.setVisibility(View.GONE);
//        progressBar.setVisibility(View.VISIBLE);
//        mStepThumbnail.setVisibility(View.VISIBLE);
//        if (!mStep.getThumbnailURL().isEmpty()) {
//            Picasso.get().load(mStep.getThumbnailURL()).into(mStepThumbnail, new Callback() {
//                @Override
//                public void onSuccess() {
//                    progressBar.setVisibility(View.INVISIBLE);
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    progressBar.setVisibility(View.INVISIBLE);
//                    Timber.d("Both video & thumbnail failed");
//                    mStepThumbnail.setVisibility(View.GONE);
//                    e.printStackTrace();
//                }
//            });
//        } else {
//            progressBar.setVisibility(View.INVISIBLE);
//            Timber.d("Both video & thumbnail failed");
//            mStepThumbnail.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PLAY_WHEN_READY, playWhenReady);
        outState.putLong(PLAY_BACK_POSITION, playBackPosition);
    }

}

