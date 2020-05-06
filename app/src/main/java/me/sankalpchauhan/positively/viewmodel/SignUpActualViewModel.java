package me.sankalpchauhan.positively.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthCredential;

import me.sankalpchauhan.positively.service.model.User;
import me.sankalpchauhan.positively.service.repository.LoginRepository;

public class SignUpActualViewModel extends AndroidViewModel {
    private LoginRepository authRepository;
    public LiveData<User> authenticatedUserLiveData;
    public LiveData<Boolean> isCredentialLinked;
    public LiveData<User> createdUserLiveData;

    public SignUpActualViewModel(@NonNull Application application) {
        super(application);
        authRepository = new LoginRepository();
    }

    public void signUpWithEmail(Context context, String email, String password, String name) {
        authenticatedUserLiveData = authRepository.firebaseCreateWithEmail(context, email, password, name);
    }

    public void sendPasswordReset(Context context, String email) {
        authRepository.sendPasswordReset(context,email);
    }

    public void linkCredential(AuthCredential credential){
        isCredentialLinked = authRepository.linkCredential(credential);
    }
    public void createUser(User authenticatedUser) {
        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser);
    }

    public void signInWithEmail(Context context, String email, String password){
        authenticatedUserLiveData = authRepository.firebaseSignInWithEmail(context, email, password);
    }
}
