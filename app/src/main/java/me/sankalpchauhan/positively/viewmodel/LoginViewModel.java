package me.sankalpchauhan.positively.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;

import me.sankalpchauhan.positively.service.model.User;
import me.sankalpchauhan.positively.service.repository.LoginRepository;

public class LoginViewModel extends AndroidViewModel {
    public LiveData<User> createdUserLiveData;
    public LiveData<User> anonymousUserLiveData;
    private LoginRepository authRepository;

    public LoginViewModel(Application application) {
        super(application);
        authRepository = new LoginRepository();
    }

    public void createUser(User authenticatedUser) {
        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser);
    }

    public void signInAnonymous() {
        anonymousUserLiveData = authRepository.anonymousSignIn();
    }

}
