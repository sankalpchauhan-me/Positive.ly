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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.config.Constants;
import me.sankalpchauhan.positively.service.model.Quotes;
import me.sankalpchauhan.positively.utils.utility;
import me.sankalpchauhan.positively.view.activity.MainActivity;
import me.sankalpchauhan.positively.view.activity.QuoteDetailActivity;
import me.sankalpchauhan.positively.view.adapters.QuotesAdapter;
import timber.log.Timber;

import static me.sankalpchauhan.positively.utils.utility.isOnline;

public class QuotesFragment extends Fragment implements QuotesAdapter.QuotesAdapterClickHandler {
    @BindView(R.id.empty_view)
    TextView errorView;
    @BindView(R.id.empty_image)
    ImageView errorImage;
    @BindView(R.id.quotesRv)
    RecyclerView quotesRecycler;
    Context context;
    List<Quotes> quotesList = new ArrayList<>();
    List<String> imageUrl = new ArrayList<>();
    private QuotesAdapter quotesAdapter;

    public QuotesFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_quotes, container, false);
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
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(utility.calculateNoOfColumns(context), RecyclerView.VERTICAL);
        quotesRecycler.setLayoutManager(staggeredGridLayoutManager);
        quotesRecycler.setHasFixedSize(true);
        quotesAdapter = new QuotesAdapter(this);
        quotesRecycler.setAdapter(quotesAdapter);
        if (getActivity() instanceof MainActivity) {
            imageUrl.addAll(((MainActivity) getActivity()).getQuotesImageUrlList());
            quotesList.addAll(((MainActivity) getActivity()).getQuotesList());
            quotesAdapter.setQuoteData(((MainActivity) getActivity()).getQuotesList(), ((MainActivity) getActivity()).getQuotesImageUrlList());
        }

        Timber.d("Item Couunt: " + quotesAdapter.getItemCount());
        if (quotesAdapter.getItemCount() == 0) {
            errorImage.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.VISIBLE);
            if (!isOnline()) {
                errorView.setText(R.string.oops_network_error);
            }
        }

    }

    @Override
    public void onClick(int quotePosition, int imagePosition, View imageView) {
        Intent i = new Intent(getActivity(), QuoteDetailActivity.class);
        i.putExtra(Constants.QUOTES_DATA, quotesList.get(quotePosition));
        i.putExtra(Constants.QUOTES_IMAGE_DATA, imageUrl.get(imagePosition));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), (View) imageView, imageView.getResources().getString(R.string.shared_thumbnail));
        startActivity(i, options.toBundle());
    }
}
