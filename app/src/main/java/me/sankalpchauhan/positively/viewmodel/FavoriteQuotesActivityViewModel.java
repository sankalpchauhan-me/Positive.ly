package me.sankalpchauhan.positively.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import me.sankalpchauhan.positively.database.AppDatabase;
import me.sankalpchauhan.positively.service.model.Quotes;

public class FavoriteQuotesActivityViewModel extends AndroidViewModel {
    private LiveData<List<Quotes>> favQuotes;

    public FavoriteQuotesActivityViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        favQuotes = database.quotesDao().getAllFavQuotes();
    }

    public LiveData<List<Quotes>> getFavQuotes() {
        return favQuotes;
    }
}
