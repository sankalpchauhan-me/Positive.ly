package me.sankalpchauhan.positively.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import me.sankalpchauhan.positively.service.model.Quotes;

@Dao
public interface QuotesDao {
    @Query("SELECT * from quotes")
    LiveData<List<Quotes>> getAllFavQuotes();

    @Query("SELECT * from quotes WHERE quoteText = :currentQuote AND imagePath = :currentImagePath")
    LiveData<Quotes> getCurrentQuoteById(String currentQuote, String currentImagePath);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertQuoteToRoom(Quotes quotes);

    @Query("DELETE FROM quotes WHERE quoteText = :currentQuote AND imagePath = :currentImagePath")
    void deleteQuoteById(String currentQuote, String currentImagePath);
}
