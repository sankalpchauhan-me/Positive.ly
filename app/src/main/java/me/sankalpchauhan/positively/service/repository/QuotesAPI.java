package me.sankalpchauhan.positively.service.repository;

import java.util.List;

import me.sankalpchauhan.positively.service.model.Quotes;
import retrofit2.Call;
import retrofit2.http.GET;


public interface QuotesAPI {
    @GET("quotes")
    Call<List<Quotes>> getAllQuotes();
}
