package me.sankalpchauhan.positively.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.utils.utility;
import me.sankalpchauhan.positively.view.activity.MainActivity;
import me.sankalpchauhan.positively.view.adapters.QuotesAdapter;
import timber.log.Timber;

public class QuotesFragment extends Fragment {
    @BindView(R.id.quotesRv)
    RecyclerView quotesRecycler;
    Context context;
    private QuotesAdapter quotesAdapter;

    public QuotesFragment(){

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


    private void setUpRecyclerView(){
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(utility.calculateNoOfColumns(context), RecyclerView.VERTICAL);
        quotesRecycler.setLayoutManager(staggeredGridLayoutManager);
        quotesRecycler.setHasFixedSize(true);
        quotesAdapter = new QuotesAdapter();
        quotesRecycler.setAdapter(quotesAdapter);
        if(getActivity() instanceof MainActivity){
            quotesAdapter.setQuoteData(((MainActivity) getActivity()).getQuotesList(), ((MainActivity) getActivity()).getQuotesImageUrlList());
        }

        Timber.e("Item Couunt: "+quotesAdapter.getItemCount());

    }
}
