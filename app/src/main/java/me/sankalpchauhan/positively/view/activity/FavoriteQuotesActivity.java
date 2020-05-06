package me.sankalpchauhan.positively.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.config.Constants;
import me.sankalpchauhan.positively.service.model.Quotes;
import me.sankalpchauhan.positively.utils.utility;
import me.sankalpchauhan.positively.view.adapters.FavoritesAdapter;
import me.sankalpchauhan.positively.viewmodel.FavoriteQuotesActivityViewModel;
import timber.log.Timber;

import static me.sankalpchauhan.positively.utils.utility.isOnline;

public class FavoriteQuotesActivity extends AppCompatActivity implements FavoritesAdapter.FavoritesAdapterClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.empty_view)
    TextView errorView;
    @BindView(R.id.empty_image)
    ImageView errorImage;
    @BindView(R.id.quotesRv)
    RecyclerView quotesRecycler;
    List<Quotes> quotesList = new ArrayList<>();
    FavoriteQuotesActivityViewModel favoriteQuotesActivityViewModel;
    private FavoritesAdapter quotesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_quotes);
        ButterKnife.bind(this);
        toolbar.setTitle(getResources().getString(R.string.user_fav_quotes));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initViewModel();
        getQuotesListFromRoom();
        setUpRecyclerView();
    }

    private void initViewModel() {
        favoriteQuotesActivityViewModel = new ViewModelProvider(this).get(FavoriteQuotesActivityViewModel.class);
    }

    public void getQuotesListFromRoom() {
        favoriteQuotesActivityViewModel.getFavQuotes().observe(this, new Observer<List<Quotes>>() {
            @Override
            public void onChanged(List<Quotes> quotes) {
                quotesList.clear();
                Timber.e(quotes.toString());
                if (quotes != null) {
                    quotesList.addAll(quotes);
                    quotesAdapter.setQuoteData(quotesList);
                } else {
                    errorImage.setVisibility(View.VISIBLE);
                    errorView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setUpRecyclerView() {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(utility.calculateNoOfColumns(this), RecyclerView.VERTICAL);
        quotesRecycler.setLayoutManager(staggeredGridLayoutManager);
        quotesRecycler.setHasFixedSize(true);
        quotesAdapter = new FavoritesAdapter(this);
        quotesRecycler.setAdapter(quotesAdapter);
        quotesAdapter.setQuoteData(quotesList);
        Timber.d("Item Couunt: " + quotesAdapter.getItemCount());

    }

    @Override
    protected void onResume() {
        super.onResume();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
    }

    @Override
    public void onClick(Quotes quote, View imageView) {
        Intent i = new Intent(this, QuoteDetailActivity.class);
        i.putExtra(Constants.QUOTES_DATA, quote);
        i.putExtra(Constants.QUOTES_IMAGE_DATA, quote.getImageUrl());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, (View) imageView, imageView.getResources().getString(R.string.shared_thumbnail));
        startActivity(i, options.toBundle());
    }
}
