package me.sankalpchauhan.positively.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.WidgetUpdateService;
import me.sankalpchauhan.positively.config.DefaultPrefSettings;
import me.sankalpchauhan.positively.service.model.User;
import me.sankalpchauhan.positively.viewmodel.LoginViewModel;
import me.sankalpchauhan.positively.viewmodel.SplashViewModel;
import timber.log.Timber;

import static me.sankalpchauhan.positively.config.Constants.USER;

public class SplashScreen extends AppCompatActivity {
    private SplashViewModel splashViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initSplashViewModel();
        checkIfUserIsAuthenticated();
        startService(new Intent(this, WidgetUpdateService.class));
    }

    private void initSplashViewModel() {
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
    }

    private void checkIfUserIsAuthenticated() {
        splashViewModel.checkIfUserIsAuthenticated();
        splashViewModel.isUserAuthenticatedLiveData.observe(this, user -> {
            Timber.d("Test " + user.isAuthenticated);
            if (!user.isAuthenticated) {
                goToHomePageActivity();
                finish();
            } else {
                getUserFromDatabase(user.uid);
            }
        });
    }

    private void goToHomePageActivity() {
        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
        startActivity(intent);
    }

    private void getUserFromDatabase(String uid) {
        splashViewModel.setUid(uid);
        splashViewModel.userLiveData.observe(this, user -> {
            if (user != null) {
                goToMainActivity(user);
                finish();
            } else {
                if (DefaultPrefSettings.getInstance().isUserAnonymous()) {
                    goToMainActivity(null);
                    finish();
                }
            }
        });
    }

    private void goToMainActivity(User user) {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        if (user != null) {
            intent.putExtra(USER, user);
        }
        startActivity(intent);
    }
}
