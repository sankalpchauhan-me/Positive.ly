package me.sankalpchauhan.positively.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.service.model.Quotes;
import timber.log.Timber;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteHolder> {
    private List<Quotes> quotesList;
    private List<String> imageList;

    @NonNull
    @Override
    public QuoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.quote_item, parent, false);
        return new QuoteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteHolder holder, int position) {
        holder.placeholder.startShimmer();
        int randomImage = new Random().nextInt(imageList.size());
        int randomQuote = new Random().nextInt(imageList.size());
        Quotes quotes = quotesList.get(randomQuote);
        String imageUrl = imageList.get(randomImage);

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
                Timber.e("Image with error"+imageList.get(randomImage)+" position in database: "+randomImage);
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(quotesList==null){
            return 0;
        }
        return quotesList.size();
    }

    public void setQuoteData(List<Quotes> quoteData, List<String> imageData) {
        quotesList = quoteData;
        imageList =imageData;
        notifyDataSetChanged();
    }

    public class QuoteHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.main_cardview)
        CardView mainCard;
        @BindView(R.id.quote_actual)
        TextView quoteText;
        @BindView(R.id.quote_background)
        ImageView quoteBackground;
        @BindView(R.id.shimmer_view_container)
        ShimmerFrameLayout placeholder;
        public QuoteHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
