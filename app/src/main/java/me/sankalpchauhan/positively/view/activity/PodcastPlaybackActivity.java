package me.sankalpchauhan.positively.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

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
    SimpleExoPlayer mExoPlayer;
    private String userAgent;
    @BindView(R.id.video_view)
    PlayerView mExoPlayerView;
    private Cache downloadCache;
    private File downloadDirectory;
    private DatabaseProvider databaseProvider;
    private long playBackPosition = 0L;
    private boolean playWhenReady = true;
    private Bitmap thumbnail;
    private int backdropColor;
    private PlayerNotificationManager playerNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent catchIntent = getIntent();
        if(catchIntent.hasExtra(Constants.PODCAST_DATA)){
            podcast = (Podcast) catchIntent.getSerializableExtra(Constants.PODCAST_DATA);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_playback);
        ButterKnife.bind(this);
        Picasso.get().load(podcast.getThumbnail()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                thumbnail = bitmap;
                Palette.from(bitmap)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@Nullable Palette palette) {
                                Palette.Swatch backdropSwtch = palette.getDarkVibrantSwatch();
                                if(backdropSwtch==null){
                                    Timber.e("Test");
                                    return;
                                }
                                backdropColor = backdropSwtch.getRgb();

                            }
                        });
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        userAgent = getUserAgent(this, "Positive.ly");
//        playerNotificationManager = new PlayerNotificationManager(
//                this,
//                new DescriptionAdapter(),
//                CHANNEL_ID,
//                NOTIFICATION_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.VISIBLE);
        mExoPlayerView.setVisibility(View.GONE);
        if (!podcast.getAudio().isEmpty()) {
            initializePlayer();
        } else {
            //progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        releaseExoplayer();
        super.onPause();
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
            mExoPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.seekTo(playBackPosition);
            mExoPlayer.setPlayWhenReady(true);
            mExoPlayer.addListener(this);
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
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

//    public static DefaultDataSourceFactory createDataSourceFactory(Context context,
//                                                                   String userAgent, TransferListener listener) {
//        // Default parameters, except allowCrossProtocolRedirects is true
//        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
//                userAgent,
//                listener,
//                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
//                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
//                true /* allowCrossProtocolRedirects */
//        );
//
//        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
//                context,
//                listener,
//                httpDataSourceFactory
//        );
//
//        return dataSourceFactory;
//    }
//    /** Returns a {@link DataSource.Factory}. */
//    public DataSource.Factory buildDataSourceFactory() {
//        DefaultDataSourceFactory upstreamFactory =
//                new DefaultDataSourceFactory(this, buildHttpDataSourceFactory());
//        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache());
//    }
//
//    /** Returns a {@link HttpDataSource.Factory}. */
//    public HttpDataSource.Factory buildHttpDataSourceFactory() {
//        return new DefaultHttpDataSourceFactory(userAgent, null /* listener */,
//                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
//                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
//                true /* allowCrossProtocolRedirects */);
//    }
//
//    protected static CacheDataSourceFactory buildReadOnlyCacheDataSource(
//            DataSource.Factory upstreamFactory, Cache cache) {
//        return new CacheDataSourceFactory(
//                cache,
//                upstreamFactory,
//                new FileDataSourceFactory(),
//                /* cacheWriteDataSinkFactory= */ null,
//                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
//                /* eventListener= */ null);
//    }
//
//    protected synchronized Cache getDownloadCache() {
//        if (downloadCache == null) {
//            File downloadContentDirectory = new File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY);
//            downloadCache =
//                    new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor(), getDatabaseProvider());
//        }
//        return downloadCache;
//    }
//
//    private File getDownloadDirectory() {
//        if (downloadDirectory == null) {
//            downloadDirectory = getExternalFilesDir(null);
//            if (downloadDirectory == null) {
//                downloadDirectory = getFilesDir();
//            }
//        }
//        return downloadDirectory;
//    }
//
//    private DatabaseProvider getDatabaseProvider() {
//        if (databaseProvider == null) {
//            databaseProvider = new ExoDatabaseProvider(this);
//        }
//        return databaseProvider;
//    }

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

