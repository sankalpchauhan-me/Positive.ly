package me.sankalpchauhan.positively.service.repository;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final String BASE_URL ="https://listen-api.listennotes.com/api/v2/";
    private static final String BASE_QUOTES_URL = "https://type.fit/api/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(CachingPolicy.okClient())
            .build();


    private static Retrofit quotesRetrofit = new Retrofit.Builder()
            .baseUrl(BASE_QUOTES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getQuotesClient())
            .build();

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

    public static <S> S createQuotesService(Class<S> serviceClass) {
        return quotesRetrofit.create(serviceClass);
    }

    public static OkHttpClient getQuotesClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient().newBuilder()
                .addInterceptor(loggingInterceptor)
                .build();
    }
}
