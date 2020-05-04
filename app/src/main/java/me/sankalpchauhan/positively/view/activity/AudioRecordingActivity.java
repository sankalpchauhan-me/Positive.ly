package me.sankalpchauhan.positively.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.BuildConfig;
import me.sankalpchauhan.positively.R;
import timber.log.Timber;

import static me.sankalpchauhan.positively.config.Contants.MY_PERMISSIONS_RECORD;
import static me.sankalpchauhan.positively.config.Contants.RECORDING_PATH;

public class AudioRecordingActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    @BindView(R.id.chronometerTimer)
    Chronometer chronometer;
    @BindView(R.id.imageView2)
    ImageView playPause;
    @BindView(R.id.parent_recorder_activity)
    ConstraintLayout constraintLayout;
    private boolean isRecording = false;
    private int previousProgress = 0;
    private String fileName = null;
    private MediaRecorder mRecorder;
    private boolean isPlaying = false;
    private boolean havePermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recording);
        ButterKnife.bind(this);

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginPermission();
                if (havePermission) {
                    recordingPreparation();
                    startRecording();
                }
            }
        });
    }

    public void recordingStopAfter() {
        playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_accent_24dp));
        //playLL.setVisibility(View.VISIBLE);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginPermission();
                if (havePermission) {
                    //if(!isPlaying) {
                    recordingPreparation();
                    startRecording();
                    //}
                }
            }
        });
    }

    public void recordingPreparation() {
        Timber.d("I am here 1");
        playPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
        //playLL.setVisibility(View.GONE);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d("I am here 2");
                //if(isPlaying) {
                recordingStopAfter();
                stopRecording();
                //}
            }
        });
    }

    public void beginPermission() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            Log.i("1", "Permission is not granted");
            havePermission = false;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) && (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                Log.i("REQUEST", "Requesting permission....");
                ActivityCompat.requestPermissions(AudioRecordingActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_RECORD);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_RECORD);
            }
        } else {
            havePermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("1", "Permission is granted");
                    havePermission = true;
                } else {
                    Log.i("1", "Permission is again not granted");
                    havePermission = false;
                    Snackbar mySnackbar = Snackbar.make(findViewById(android.R.id.content), "Please ennable the permissions", Snackbar.LENGTH_SHORT);
                    mySnackbar.setAction("ENABLE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                        }
                    });
                    mySnackbar.show();
                }
                return;
            }
        }
    }

//    private void stopPlaying() {
//        try {
//            mPlayer.release();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        mPlayer = null;
//        chronometer.stop();
//
//    }

    private void startRecording() {
        Timber.d("I am here 3");
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        File root = android.os.Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + RECORDING_PATH);
        Timber.e(file.getAbsoluteFile().toString());
        if (!file.exists()) {
            Timber.d("I am here 4");
            boolean mkdirs = file.mkdirs();
            if (mkdirs) {
                Timber.d("Success");
            } else {
                Timber.d("failiure");
            }
        }
        Timber.d("I am here 5");
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH mm a");
        String formattedDate = df.format(currentTime);
        fileName = root.getAbsolutePath() + RECORDING_PATH + '/' + formattedDate + ".mp3";
        Timber.d(String.valueOf(fileName));
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            Timber.d("I am here 6");
            mRecorder.prepare();
            mRecorder.start();
            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
            Timber.d("I am here 7");
        }
        previousProgress = 0;
//        seekBar.setProgress(0);
        //stopPlaying();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    private void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isRecording = false;
        }
        mRecorder = null;
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        Toast.makeText(this, "Voice Log Saved", Toast.LENGTH_SHORT).show();
    }

//    private void startPlaying() {
//        mPlayer = new MediaPlayer();
//        try {
//            mPlayer.setDataSource(fileName);
//            mPlayer.prepare();
//            mPlayer.start();
//        } catch (IOException e) {
//            Timber.e("failed");
//        }
//        seekBar.setProgress(previousProgress);
//        mPlayer.seekTo(previousProgress);
//        seekBar.setMax(mPlayer.getDuration());
//        seekUpdation();
//        chronometer.start();
//        mPlayer.setOnCompletionListener(this);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (mPlayer != null && fromUser) {
//                    mPlayer.seekTo(progress);
//                    chronometer.setBase(SystemClock.elapsedRealtime() - mPlayer.getCurrentPosition());
//                    previousProgress = progress;
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//    }

//    private void seekUpdation() {
//        if (mPlayer != null) {
//            int mCurrentPosition = mPlayer.getCurrentPosition();
//            seekBar.setProgress(mCurrentPosition);
//            previousProgress = mCurrentPosition;
//        }
//        //mHandler.postDelayed(runnable, 100);
//    }


    @Override
    public void onBackPressed() {
        if (isRecording) {
            new AlertDialog.Builder(AudioRecordingActivity.this)
                    .setTitle("Voice Log not Saved")
                    .setMessage("You have an ongoing voice log, do you want to discard it")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AudioRecordingActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        isPlaying = false;
        chronometer.stop();
    }


}

