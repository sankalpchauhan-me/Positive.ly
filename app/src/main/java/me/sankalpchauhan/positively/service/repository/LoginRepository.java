package me.sankalpchauhan.positively.service.repository;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import me.sankalpchauhan.positively.config.DefaultPrefSettings;
import me.sankalpchauhan.positively.service.model.User;
import timber.log.Timber;

import static me.sankalpchauhan.positively.config.Constants.USERS_COLLECTION;

/**
 * Contains all the methods related to FirebaseAuth
 */
public class LoginRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(USERS_COLLECTION);
    private User user = new User();

    public MutableLiveData<User> firebaseSignInWithGoogle(AuthCredential googleAuthCredential) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();
                    String photoUrl = String.valueOf(firebaseUser.getPhotoUrl());
                    User user;
                    if (photoUrl == null) {
                        user = new User(uid, name, email);
                    } else {
                        user = new User(uid, name, email, photoUrl);
                    }
                    user.isNew = isNewUser;
                    authenticatedUserMutableLiveData.setValue(user);
                }
            } else {
                authenticatedUserMutableLiveData.setValue(null);
                Timber.e(authTask.getException().getMessage());
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<User> firebaseCreateWithEmail(Context context, String email, String password, String userName) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> authTask) {
                //((SignUpActivity) context).setSignUpVisibility();
                if (authTask.isSuccessful()) {
                    boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName).build();
                        firebaseUser.updateProfile(profileUpdates);
                        String email = firebaseUser.getEmail();
                        String photoUrl = String.valueOf(firebaseUser.getPhotoUrl());
                        User user;
                        if (photoUrl == null) {
                            user = new User(uid, userName, email);
                        } else {
                            user = new User(uid, userName, email, photoUrl);
                        }
                        user.isNew = isNewUser;
                        firebaseUser.sendEmailVerification();
                        authenticatedUserMutableLiveData.setValue(user);
                    }
                } else {
                    authenticatedUserMutableLiveData.setValue(null);
                    Timber.e(authTask.getException().getMessage());
                    Toast.makeText(context, authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<User> firebaseSignInWithEmail(Context context, String email, String password) {
        MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> authTask) {
                        //((LoginActivity) context).setSignInVisible();
                        if (authTask.isSuccessful()) {
                            boolean isNewUser = authTask.getResult().getAdditionalUserInfo().isNewUser();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();
                                String name = firebaseUser.getDisplayName();
                                String email = firebaseUser.getEmail();
                                String photoUrl = String.valueOf(firebaseUser.getPhotoUrl());
                                User user;
                                if (photoUrl == null) {
                                    user = new User(uid, name, email);
                                } else {
                                    user = new User(uid, name, email, photoUrl);
                                }
                                user.isNew = isNewUser;
                                authenticatedUserMutableLiveData.setValue(user);
                            }
                        } else {
                            Timber.e(authTask.getException().getMessage());
                            authenticatedUserMutableLiveData.setValue(null);
                            Toast.makeText(context, authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        return authenticatedUserMutableLiveData;
    }

    public void sendPasswordReset(Context context, String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Password Reset Mail Sent", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public MutableLiveData<User> createUserInFirestoreIfNotExists(User authenticatedUser) {
        MutableLiveData<User> newUserMutableLiveData = new MutableLiveData<>();
        DocumentReference uidRef = usersRef.document(authenticatedUser.uid);
        uidRef.get().addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                DocumentSnapshot document = uidTask.getResult();
                if (!document.exists()) {
                    uidRef.set(authenticatedUser).addOnCompleteListener(userCreationTask -> {
                        if (userCreationTask.isSuccessful()) {
                            authenticatedUser.isCreated = true;
                            newUserMutableLiveData.setValue(authenticatedUser);
                        } else {
                            Timber.e(userCreationTask.getException().getMessage());
                        }
                    });
                } else if(document.get("name")==null || document.get("photoUrl")==null){
                    uidRef.update("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    uidRef.update("photoUrl", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                    newUserMutableLiveData.setValue(authenticatedUser);
                }
                else {
                    newUserMutableLiveData.setValue(authenticatedUser);
                }
            } else {
                Timber.e(uidTask.getException().getMessage());
            }
        });
        return newUserMutableLiveData;
    }

    public MutableLiveData<User> checkIfUserIsAuthenticatedInFirebase() {
        MutableLiveData<User> isUserAuthenticateInFirebaseMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (DefaultPrefSettings.getInstance().isUserAnonymous()) {
            user.isAuthenticated = true;
            user.isAnonymous = true;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
        }
        if (firebaseUser == null) {
            user.isAuthenticated = false;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
        } else {
            user.uid = firebaseUser.getUid();
            user.isAuthenticated = true;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
        }
        return isUserAuthenticateInFirebaseMutableLiveData;
    }

    public MutableLiveData<User> addUserToLiveData(String uid) {
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        usersRef.document(uid).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot document = userTask.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    userMutableLiveData.setValue(user);
                } else {
                    //TODO: check if this is correct
                    userMutableLiveData.setValue(null);
                }
            } else {
                userMutableLiveData.setValue(null);
                Timber.e(userTask.getException().getMessage());
            }
        });
        return userMutableLiveData;
    }

    public MutableLiveData<User> anonymousSignIn() {
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Timber.d("Anonymous Sign In Success");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    User authenticatedUser = new User();
                    authenticatedUser.isAnonymous = true;
                    userMutableLiveData.setValue(authenticatedUser);
                } else {
                    userMutableLiveData.setValue(null);
                    Timber.e(task.getException().getMessage());
                }
            }
        });
        return userMutableLiveData;
    }

    /**
     * We try linking anonymous user if it fails we register anonymous user as fresh user
     * @param credential
     * @return
     */
    public MutableLiveData<Boolean> linkCredential(AuthCredential credential) {
        MutableLiveData<Boolean> anonymousUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Timber.d("linkWithCredential:success");
                    //FirebaseUser user = task.getResult().getUser();
                    anonymousUserMutableLiveData.setValue(true);
                } else {
                    Timber.e("linkWithCredential:failiure "+task.getException().getMessage());
                    anonymousUserMutableLiveData.setValue(false);
                }
            }
        });
        return anonymousUserMutableLiveData;
    }

}
