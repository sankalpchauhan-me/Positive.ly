package me.sankalpchauhan.positively.service.repository;


import androidx.lifecycle.MutableLiveData;

import me.sankalpchauhan.positively.service.model.ServerResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * The podcast repository fetches all the requests from the netowrk
 */
public class PodcastRepository {
    public static PodcastRepository podcastRepository;
    private ListenNotesAPI listenNotesAPI;

    public PodcastRepository(){
        listenNotesAPI = RetrofitService.createService(ListenNotesAPI.class);
    }

    /**
     * Singleton Design Pattern
     * @return
     */
    public static PodcastRepository getInstance(){
        if(podcastRepository==null){
            podcastRepository = new PodcastRepository();
        }
        return podcastRepository;
    }

    public MutableLiveData<ServerResult> getPodcasts(String searchTerm, int page, String language, int safeMode){
        final MutableLiveData<ServerResult> podcastMutableLiveData = new MutableLiveData<>();
        listenNotesAPI.getEpisodes(searchTerm,page,language,safeMode).enqueue(new Callback<ServerResult>() {
            @Override
            public void onResponse(Call<ServerResult> call, Response<ServerResult> response) {
                if(response.isSuccessful() && response.code()==200){
                    podcastMutableLiveData.setValue(response.body());
                }else {
                    podcastMutableLiveData.setValue(null);
                    Timber.d("API RequestRequest failed with error" + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ServerResult> call, Throwable t) {
                podcastMutableLiveData.setValue(null);
            }
        });
        return podcastMutableLiveData;
    }
}
