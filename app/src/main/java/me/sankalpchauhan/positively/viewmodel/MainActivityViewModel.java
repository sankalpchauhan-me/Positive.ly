package me.sankalpchauhan.positively.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import me.sankalpchauhan.positively.service.model.Podcast;
import me.sankalpchauhan.positively.service.model.ServerResult;
import me.sankalpchauhan.positively.service.repository.PodcastRepository;

public class MainActivityViewModel extends AndroidViewModel {
    private MutableLiveData<ServerResult> mutableLiveData;
    private PodcastRepository podcastRepository;
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        if(mutableLiveData!=null){
            return;
        }
        podcastRepository = podcastRepository.getInstance();
        mutableLiveData = podcastRepository.getPodcasts("positivity",0,"English",1);
    }

    public LiveData<ServerResult> getPositivityPodcasts(){
        return mutableLiveData;
    }
}
