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
    private LoginRepository authRepository;
    public LiveData<User> authenticatedUserLiveData;
    public LiveData<User> createdUserLiveData;
    public LiveData<User> isUserAuthenticatedLiveData;
    public LiveData<User> userLiveData;
    public LiveData<User> anonymousUserLiveData;
    public LiveData<Boolean> isCredentialLinked;

    public LoginViewModel(Application application) {
        super(application);
        authRepository = new LoginRepository();
    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authenticatedUserLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

    public void signInWithEmail(Context context, String email, String password){
        authenticatedUserLiveData = authRepository.firebaseSignInWithEmail(context, email, password);
    }

    public void createUser(User authenticatedUser) {
        createdUserLiveData = authRepository.createUserInFirestoreIfNotExists(authenticatedUser);
    }

    public void sendPasswordReset(Context context, String email) {
        authRepository.sendPasswordReset(context,email);
    }

    public void signUpWithEmail(Context context, String email, String password, String name) {
        authenticatedUserLiveData = authRepository.firebaseCreateWithEmail(context, email, password, name);
    }

    public void checkIfUserIsAuthenticated() {
        isUserAuthenticatedLiveData = authRepository.checkIfUserIsAuthenticatedInFirebase();
    }

    public void setUid(String uid) {
        userLiveData = authRepository.addUserToLiveData(uid);
    }

    /**
     * Move To LoginViewModel at seperate
     */
    public void signInAnonymous(){
        anonymousUserLiveData = authRepository.anonymousSignIn();
    }

    /**
     * Seperate for each ViewModel whichever requires it
     * @param credential
     */
    public void linkCredential(AuthCredential credential){
        isCredentialLinked = authRepository.linkCredential(credential);
    }
}
