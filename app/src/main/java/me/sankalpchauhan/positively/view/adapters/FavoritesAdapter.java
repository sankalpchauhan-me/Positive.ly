package me.sankalpchauhan.positively.view.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.service.model.Quotes;
import timber.log.Timber;

import static me.sankalpchauhan.positively.PositivelyApp.getAnalyticsInstance;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteHolder> {
    final FavoritesAdapterClickListener mCallback;
    List<Quotes> quotesList = new ArrayList<>();

    public FavoritesAdapter(FavoritesAdapterClickListener mCallback) {
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.quote_item, parent, false);
        return new FavoriteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteHolder holder, int position) {
        holder.placeholder.startShimmer();
        Quotes quotes = quotesList.get(position);
        String imageUrl = quotesList.get(position).getImageUrl();

        Picasso.get().load(imageUrl).error(holder.itemView.getResources().getDrawable(R.drawable.ic_broken_image_black_24dp)).into(holder.quoteBackground, new Callback() {
            @Override
            public void onSuccess() {
                holder.placeholder.stopShimmer();
                holder.placeholder.setVisibility(View.GONE);
                holder.mainCard.setVisibility(View.VISIBLE);
                holder.quoteText.setText(quotes.getText());
            }

            @Override
            public void onError(Exception e) {
                holder.placeholder.stopShimmer();
                holder.placeholder.setVisibility(View.GONE);
                holder.mainCard.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
        });
    }

    public void setQuoteData(List<Quotes> quoteData) {
        quotesList = quoteData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (quotesList == null) {
            return 0;
        }
        return quotesList.size();
    }

    public interface FavoritesAdapterClickListener {
        void onClick(Quotes quote, View imageView);
    }

    public class FavoriteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.main_cardview)
        CardView mainCard;
        @BindView(R.id.quote_actual)
        TextView quoteText;
        @BindView(R.id.quote_background)
        ImageView quoteBackground;
        @BindView(R.id.shimmer_view_container)
        ShimmerFrameLayout placeholder;

        public FavoriteHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mCallback.onClick(quotesList.get(getAdapterPosition()), quoteBackground);
        }
    }
}
