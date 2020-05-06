package me.sankalpchauhan.positively.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import me.sankalpchauhan.positively.service.model.User;
import me.sankalpchauhan.positively.service.repository.LoginRepository;

public class SplashViewModel extends AndroidViewModel {
    public LiveData<User> isUserAuthenticatedLiveData;
    public LiveData<User> userLiveData;
    private LoginRepository authRepository;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        authRepository = new LoginRepository();
    }

    public void checkIfUserIsAuthenticated() {
        isUserAuthenticatedLiveData = authRepository.checkIfUserIsAuthenticatedInFirebase();
    }

    public void setUid(String uid) {
        userLiveData = authRepository.addUserToLiveData(uid);
    }

}
