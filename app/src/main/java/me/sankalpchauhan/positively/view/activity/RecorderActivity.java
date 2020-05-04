package me.sankalpchauhan.positively.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.service.model.VoiceLog;
import me.sankalpchauhan.positively.view.adapters.RecorderAdapter;
import timber.log.Timber;

import static me.sankalpchauhan.positively.config.Contants.RECORDING_PATH;

public class RecorderActivity extends AppCompatActivity {
    List<VoiceLog> voiceLogList = new ArrayList<>();
    @BindView(R.id.add_recording)
    FloatingActionButton addFAB;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recorder_rv)
    RecyclerView recorderRv;
    @BindView(R.id.empty_image)
    ImageView emptyImage;
    @BindView(R.id.empty_view)
    TextView emptyText;
    private RecorderAdapter recorderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        ButterKnife.bind(this);
        toolbar.setTitle("Positive.ly Voice Logs");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecorderActivity.this, AudioRecordingActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recorderAdapter != null) {
            voiceLogList.clear();
            getRecordings();
        } else {
            getRecordings();
        }
        setUpRecyclerView();
    }

    private void getRecordings() {
        File root = android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + RECORDING_PATH;
        Timber.d(path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                String recordingUri = root.getAbsolutePath() + RECORDING_PATH + '/' + fileName;
                VoiceLog recording = new VoiceLog(recordingUri, fileName, false);
                voiceLogList.add(recording);
            }
            emptyImage.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);

        }

    }

    private void setUpRecyclerView() {
        recorderRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recorderRv.setHasFixedSize(true);
        recorderAdapter = new RecorderAdapter();
        recorderRv.setAdapter(recorderAdapter);
        recorderAdapter.setVoiceLogData(voiceLogList);
        if (recorderAdapter.getItemCount() == 0) {
            emptyImage.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyImage.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
