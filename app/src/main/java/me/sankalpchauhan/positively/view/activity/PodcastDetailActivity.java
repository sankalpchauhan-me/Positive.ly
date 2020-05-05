package me.sankalpchauhan.positively.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.config.Constants;
import me.sankalpchauhan.positively.service.model.Podcast;

public class PodcastDetailActivity extends AppCompatActivity {
    private boolean isTextViewClicked =false;
    private Podcast podcast;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.podcast_image)
    ImageView podcastImage;
    @BindView(R.id.podcast_toolbar)
    Toolbar podcastDetailToolbar;
    @BindView(R.id.podcast_thumbnail)
    ImageView podcastThumbnail;
    @BindView(R.id.podcast_description)
    TextView podcastDescription;
    @BindView(R.id.podcast_play)
    FloatingActionButton playFAB;
    @BindView(R.id.podcast_length)
    TextView podcastLength;
    @BindView(R.id.podcast_publishers)
    TextView podcastPublishers;
    @BindView(R.id.podcast_publish_date)
    TextView podcastPublishDate;
    @BindView(R.id.podcast_click_to_expand)
    ImageButton clickToExpand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent catchIntent = getIntent();
        if(catchIntent.hasExtra(Constants.PODCAST_DATA)){
            podcast = (Podcast) catchIntent.getSerializableExtra(Constants.PODCAST_DATA);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_detail);
        ButterKnife.bind(this);
        setSupportActionBar(podcastDetailToolbar);
        ActionBar bar = getSupportActionBar();
        if(bar!=null){
            bar.setDisplayHomeAsUpEnabled(true);
            if(podcast.getTitleOriginal()!=null){
                bar.setTitle(Html.fromHtml("<font color='#ffffff'><B>" + podcast.getTitleOriginal() + "</B></font>"));
            }
        }

        if(podcast.getDescriptionOriginal()!=null) {
            podcastDescription.setText(podcast.getDescriptionOriginal());
        }
        Picasso.get().load(podcast.getThumbnail()).error(getResources().getDrawable(R.drawable.ic_broken_image_black_24dp)).into(podcastThumbnail);
        Picasso.get().load(podcast.getImage()).into(podcastImage);
        if(podcast.getPublisherOriginal()!=null){
            podcastPublishers.setText(podcast.getPublisherOriginal());
        }

        podcastDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTextViewClicked){
                    podcastDescription.setMaxLines(8);
                    isTextViewClicked = false;
                    clickToExpand.setVisibility(View.VISIBLE);
                } else {
                    podcastDescription.setMaxLines(Integer.MAX_VALUE);
                    isTextViewClicked = true;
                    clickToExpand.setVisibility(View.GONE);
                }
            }
        });
        clickToExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                podcastDescription.performClick();
            }
        });

        podcastPublishDate.setText(getDate(podcast.getPubDateMs(), "dd MMMM yyyy"));
        if(podcast.getAudioLengthSec()!=null){
            podcastLength.setText(getHours().toString());
        }

        playFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PodcastDetailActivity.this, PodcastPlaybackActivity.class);
                i.putExtra(Constants.PODCAST_DATA, podcast);
                startActivity(i);
            }
        });
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public StringBuilder getHours(){
        StringBuilder sb = new StringBuilder();
        double minutes = (double)podcast.getAudioLengthSec()/60;
        int hours  =(int) minutes/60;
        int actualMinutes = (int)minutes-(hours*60);
        if(hours!=0) {
            return sb.append(hours).append("h ").append(actualMinutes).append(" minutes");
        } else {
            return sb.append(actualMinutes).append(" minutes");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
