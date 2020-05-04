package me.sankalpchauhan.positively.service.repository;

import me.sankalpchauhan.positively.BuildConfig;
import me.sankalpchauhan.positively.service.model.ServerResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ListenNotesAPI {

    @Headers("X-ListenAPI-Key: "+ BuildConfig.LISTEN_NOTES_API_KEY)
    @GET("search")
    Call<ServerResult> getEpisodes(@Query("q") String searchTerm, @Query("offset") int page, @Query("language") String language, @Query("safe_mode") int safeMode);
}
