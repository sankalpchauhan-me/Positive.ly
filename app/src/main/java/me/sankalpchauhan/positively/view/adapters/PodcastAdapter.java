package me.sankalpchauhan.positively.view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.service.model.Podcast;
import timber.log.Timber;

public class PodcastAdapter extends RecyclerView.Adapter<PodcastAdapter.PodcastHolder> {
    private final PodcastAdapterOnClickHandler mCallback;
    List<Podcast> podcastList = new ArrayList<>();

    public PodcastAdapter(PodcastAdapterOnClickHandler mCallback) {
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public PodcastHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.podcast_item, parent, false);
        return new PodcastHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PodcastHolder holder, int position) {
        Podcast podcast = podcastList.get(position);
        Picasso.get().load(podcast.getThumbnail()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                setViewsVisible(holder);
                holder.podcastThumbnail.setImageBitmap(bitmap);
                Palette.from(bitmap)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@Nullable Palette palette) {
                                Palette.Swatch backdropSwtch = palette.getDarkVibrantSwatch();
                                if (backdropSwtch == null) {
                                    Timber.d("Podcast Adapter: Test");
                                    setViewsVisible(holder);
                                    return;
                                }
                                int transparentRGBInt = getColorWithAplha(backdropSwtch.getRgb(), 0.5f);
                                holder.textBackDrop.setBackgroundColor(transparentRGBInt);

                            }
                        });
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                setViewsVisible(holder);
                holder.podcastThumbnail.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.ic_broken_image_black_24dp));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // It is being handled by shimmer
                holder.placeholder.startShimmer();
            }
        });
        if (podcast.getTitleOriginal() != null) {
            holder.podcastTitle.setText(podcast.getTitleOriginal());
        }

    }

    public void setViewsVisible(PodcastHolder holder) {
        holder.placeholder.stopShimmer();
        holder.placeholder.setVisibility(View.GONE);
        holder.mainCardview.setVisibility(View.VISIBLE);
    }

    /**
     * Attribution: getColorWithApha()
     * https://stackoverflow.com/questions/36078861/palette-library-how-to-add-transparency-to-palette-swatch-color
     *
     * @param color opaque RGB integer color for ex: -11517920
     * @param ratio ratio of transparency for ex: 0.5f
     * @return transparent RGB integer color
     */
    private int getColorWithAplha(int color, float ratio) {
        int transColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        transColor = Color.argb(alpha, r, g, b);
        return transColor;
    }

    @Override
    public int getItemCount() {
        if (podcastList == null) {
            return 0;
        }
        return podcastList.size();
    }

    public void setPodcastData(List<Podcast> podcastData) {
        podcastList = podcastData;
        notifyDataSetChanged();
    }

    public interface PodcastAdapterOnClickHandler {
        void onClick(Podcast podcast, ImageView imageView);
    }

    public class PodcastHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.main_cardview)
        CardView mainCardview;
        @BindView(R.id.podcast_thumbnail)
        ImageView podcastThumbnail;
        @BindView(R.id.podcast_title)
        TextView podcastTitle;
        @BindView(R.id.textBackDrop)
        ImageView textBackDrop;
        @BindView(R.id.shimmer_view_container)
        ShimmerFrameLayout placeholder;

        public PodcastHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCallback.onClick(podcastList.get(adapterPosition), podcastThumbnail);
        }
    }


}
