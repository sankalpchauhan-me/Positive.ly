package me.sankalpchauhan.positively.service.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import me.sankalpchauhan.positively.config.Constants;
import me.sankalpchauhan.positively.service.model.Quotes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class QuotesRepository {
    public static QuotesRepository quotesRepository;
    QuotesAPI quotesAPI;

    public QuotesRepository(){
        quotesAPI = RetrofitService.createQuotesService(QuotesAPI.class);
    }

    /**
     * Singleton design pattern
     * @return
     */
    public static QuotesRepository getInstance(){
        if(quotesRepository==null){
            quotesRepository = new QuotesRepository();
        }
        return quotesRepository;
    }

    public MutableLiveData<List<Quotes>> getQuotes(){
        final MutableLiveData<List<Quotes>> quotesMutableLiveData = new MutableLiveData<>();
        quotesAPI.getAllQuotes().enqueue(new Callback<List<Quotes>>() {
            @Override
            public void onResponse(Call<List<Quotes>> call, Response<List<Quotes>> response) {
                if(response.isSuccessful() && response.code()==200) {
                    quotesMutableLiveData.setValue(response.body());
                } else {
                    Timber.e("Quotes Server Responded with "+response.code());
                    quotesMutableLiveData.setValue(null);
                }
            }
            @Override
            public void onFailure(Call<List<Quotes>> call, Throwable t) {
                quotesMutableLiveData.setValue(null);
                t.printStackTrace();

            }
        });
        return quotesMutableLiveData;
    }

    public MutableLiveData<List<String>> quotesImageUrls(){
        final MutableLiveData<List<String>> quotesImageUrl = new MutableLiveData<>();
        FirebaseFirestore.getInstance().collection(Constants.QUOTES_IMAGES_COLLECTION_REFERENCE).document(Constants.QUOTES_IMAGES_DOCUMENT_REFERENCE).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    quotesImageUrl.setValue((List<String>) document.get(Constants.IMAGE_ARRAY));
                } else {
                    quotesImageUrl.setValue(null);
                    Timber.e(task.getException());
                }
            }
        });

        return quotesImageUrl;
    }
}
