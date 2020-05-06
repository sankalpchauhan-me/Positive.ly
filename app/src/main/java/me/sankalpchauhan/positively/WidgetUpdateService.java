package me.sankalpchauhan.positively;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.RemoteViews;

import java.util.List;
import java.util.Random;

import me.sankalpchauhan.positively.service.model.Quotes;
import me.sankalpchauhan.positively.service.repository.QuotesRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class WidgetUpdateService extends IntentService {
    private PowerManager.WakeLock wakeLock;
    QuotesRepository quotesRepository;
    public WidgetUpdateService() {
        super("Positive.ly");
        setIntentRedelivery(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("Service is created");
        quotesRepository = new QuotesRepository();
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Positively: Wakelock");
        wakeLock.acquire(600000);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("task is running");
        quotesRepository.getQuoteOfTheDay().getAllQuotes().enqueue(new Callback<List<Quotes>>() {
            @Override
            public void onResponse(Call<List<Quotes>> call, Response<List<Quotes>> response) {
                if(response.isSuccessful() && response.code()==200) {
                    int randomQuote = new Random().nextInt(response.body().size());
                    String quote = response.body().get(randomQuote).getText();
                    String author = response.body().get(randomQuote).getAuthor();
                    updateWidgetQuote(quote, author);
                }
            }
            @Override
            public void onFailure(Call<List<Quotes>> call, Throwable t) {

            }
        });
    }

    private void updateWidgetQuote(String s1, String s2){
        Timber.d("Task Response"+s1);
        Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.quote_of_the_day_widget);
        ComponentName thisWidget = new ComponentName(context, QuoteOfTheDayWidget.class);
        remoteViews.setTextViewText(R.id.quote_tv,s1);
        if(s2!=null){
            remoteViews.setTextViewText(R.id.quote_authour, "- "+s2);
        }
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }
}
