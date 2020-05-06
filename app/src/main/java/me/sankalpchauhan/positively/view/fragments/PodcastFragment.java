package me.sankalpchauhan.positively.view.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.service.model.Podcast;
import me.sankalpchauhan.positively.utils.utility;
import me.sankalpchauhan.positively.view.activity.MainActivity;
import me.sankalpchauhan.positively.view.activity.PodcastDetailActivity;
import me.sankalpchauhan.positively.view.adapters.PodcastAdapter;
import timber.log.Timber;

import static me.sankalpchauhan.positively.config.Constants.PODCAST_DATA;
import static me.sankalpchauhan.positively.utils.utility.isOnline;


public class PodcastFragment extends Fragment implements PodcastAdapter.PodcastAdapterOnClickHandler {
    @BindView(R.id.empty_view)
    TextView errorView;
    @BindView(R.id.empty_image)
    ImageView errorImage;
    @BindView(R.id.podcast_rv)
    RecyclerView podcastRv;
    Context context;
    private PodcastAdapter podcastAdapter;

    public PodcastFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_podcast, container, false);
        ButterKnife.bind(this, rootView);
        setUpRecyclerView();
        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void setUpRecyclerView() {
        Timber.d("I am here");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, isTabletOrLandscape(), RecyclerView.VERTICAL, false);
        podcastRv.setLayoutManager(gridLayoutManager);
        podcastRv.setHasFixedSize(true);
        podcastAdapter = new PodcastAdapter(this);
        podcastRv.setAdapter(podcastAdapter);
        if (getActivity() instanceof MainActivity) {
            podcastAdapter.setPodcastData(((MainActivity) getActivity()).getPodcastList());
        }

        Timber.d("Item Couunt: " + podcastAdapter.getItemCount());
        if (podcastAdapter.getItemCount() == 0) {
            errorImage.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.VISIBLE);
            if (!isOnline()) {
                errorView.setText(R.string.oops_network_error);
            }
        }

    }

    /**
     * Function actively checks if the device is a tablet or is in landscape if yes set the columns dynamically else return columns = 1
     *
     * @return
     */
    private int isTabletOrLandscape() {

        return utility.calculateNoOfColumns(context);
    }

    @Override
    public void onClick(Podcast podcast, ImageView imageView) {
        Intent i = new Intent(getActivity(), PodcastDetailActivity.class);
        i.putExtra(PODCAST_DATA, podcast);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), (View) imageView, imageView.getResources().getString(R.string.shared_thumbnail));
        startActivity(i, options.toBundle());
    }
}
