package me.sankalpchauhan.positively.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import me.sankalpchauhan.positively.service.model.Podcast;
import me.sankalpchauhan.positively.service.model.Quotes;
import me.sankalpchauhan.positively.service.model.ServerResult;
import me.sankalpchauhan.positively.service.repository.PodcastRepository;
import me.sankalpchauhan.positively.service.repository.QuotesRepository;

public class MainActivityViewModel extends AndroidViewModel {
    private MutableLiveData<List<Quotes>> quoteListMutableLiveData;
    private MutableLiveData<List<String>> quotesImageUrlsMutableLiveData;
    private MutableLiveData<ServerResult> mutableLiveData;
    private PodcastRepository podcastRepository;
    private QuotesRepository quotesRepository;
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        if(mutableLiveData!=null){
            return;
        }
        podcastRepository = podcastRepository.getInstance();
        mutableLiveData = podcastRepository.getPodcasts("positivity",0,"English",1);
        quotesRepository = quotesRepository.getInstance();
    }

    public LiveData<List<Quotes>> getQuoteList(){
        quoteListMutableLiveData = quotesRepository.getQuotes();
        return quoteListMutableLiveData;
    }

    public LiveData<List<String>> getQuotesImageUrl(){
        quotesImageUrlsMutableLiveData = quotesRepository.quotesImageUrls();
        return quotesImageUrlsMutableLiveData;
    }

    public LiveData<ServerResult> getPositivityPodcasts(){
        return mutableLiveData;
    }


}
