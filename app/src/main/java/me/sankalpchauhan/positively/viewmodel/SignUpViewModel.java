package me.sankalpchauhan.positively.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthCredential;

import me.sankalpchauhan.positively.service.model.User;
import me.sankalpchauhan.positively.service.repository.LoginRepository;

public class SignUpViewModel extends AndroidViewModel {
    public LiveData<User> createdUserLiveData;
    public LiveData<User> authenticatedUserLiveData;
    public LiveData<Boolean> isCredentialLinked;
    private LoginRepository authRepository;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        authRepository = new LoginRepository();
    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authenticatedUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

    public void createUser(User authenticatedUser) {
        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser);
    }

    public void linkCredential(AuthCredential credential) {
        isCredentialLinked = authRepository.linkCredential(credential);
    }


}
