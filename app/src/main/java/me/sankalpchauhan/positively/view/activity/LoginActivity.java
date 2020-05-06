package me.sankalpchauhan.positively.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.config.DefaultPrefSettings;
import me.sankalpchauhan.positively.service.model.User;
import me.sankalpchauhan.positively.viewmodel.LoginViewModel;
import timber.log.Timber;

import static com.google.firebase.auth.FirebaseAuth.getInstance;
import static me.sankalpchauhan.positively.config.Constants.USER;
import static me.sankalpchauhan.positively.utils.utility.isEmailValid;
import static me.sankalpchauhan.positively.utils.utility.isOnline;
import static me.sankalpchauhan.positively.utils.utility.setSnackBarNoAction;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.skip_do_anonymous_sign_in)
    Button skipButton;
    @BindView(R.id.editText)
    EditText email;
    @BindView(R.id.button_continue)
    Button continueButton;
    @BindView(R.id.continue_with_other)
    Button continueWithOther;
    //private boolean isUserAnonymous = false;
    @BindView(R.id.login_parent)
    ConstraintLayout parent;
    private LoginViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        initAuthViewModel();
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authViewModel.signInAnonymous();
                authViewModel.anonymousUserLiveData.observe(LoginActivity.this, user -> {
                    if (user != null) {
                        Timber.d("Test " + user.isAnonymous);
                        goToMainActivity(user);
                    } else {
                        if (isOnline()) {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.Some_Error_Occured), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyForm()) {
                    Timber.d("Initiate Passwordless Login");
                    sendSignInLink(email.getText().toString());
                }
            }
        });
        continueWithOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                //i.putExtra(USER_IS_ANONYMOUS, "userIsAnonymous");
                startActivity(i);
                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
            }
        });
    }

    public void initAuthViewModel() {
        authViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void goToMainActivity(User user) {
        finishAffinity();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
        finish();
    }

    private boolean verifyForm() {
        boolean valid = true;
        if (!isEmailValid(email.getText().toString())) {
            email.setError(getResources().getString(R.string.Invalid_email));
            valid = false;
        }

        if (email.getText().toString().isEmpty()) {
            email.setError(getResources().getString(R.string.Required));
            valid = false;
        }

        return valid;

    }

    /**
     * All methods below are used for firebase passwordless sign in
     * Since they are quite integrated to UI & have to use dynamic links, for now
     * they will reside here instead of auth repository
     */

    public void initDynamicLink() {
        //    tinyUtilities.showProgressDialog(this, "Verifying Mail");
        //Toast.makeText(this, "Verifying Mail", Toast.LENGTH_LONG).show();
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            continueButton.setVisibility(View.INVISIBLE);
                            deepLink = pendingDynamicLinkData.getLink();
                            if (DefaultPrefSettings.getInstance().getUpdateEmail() != null) {
                                emailUpdate(DefaultPrefSettings.getInstance().getUpdateEmail());
                            } else {
                                signIn();
                            }
                        } else {
//                            tinyUtilities.hideProgressDialog();
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("SIGNINACTIVITY", "getDynamicLink:onFailure", e);
                        //tinyUtilities.hideProgressDialog();
                    }
                });
    }


    /**
     * Attribution Firebase Auth Docs
     * https://firebase.google.com/docs/auth/android/email-link-auth
     */
    public void signIn() {
        FirebaseAuth auth = getInstance();
        Intent intent = getIntent();
        String emailLink = intent.getData().toString();

        // Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            String email = DefaultPrefSettings.getInstance().getUserEmail();
            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Timber.d("Successfully signed in with email link!");
                                AuthResult result = task.getResult();
                                FirebaseUser authenticatedUser = result.getUser();
                                User user = new User(authenticatedUser.getUid(), authenticatedUser.getDisplayName(), authenticatedUser.getEmail());
                                authViewModel.createUser(user);
                                authViewModel.createdUserLiveData.observe(LoginActivity.this, new Observer<User>() {
                                    @Override
                                    public void onChanged(User user) {
                                        continueButton.setVisibility(View.VISIBLE);
                                        if (user != null) {
                                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.signed_in), Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                            finish();
                                            startActivity(i);
                                        }
                                    }
                                });
                            } else {
                                Timber.e("Error signing in with email link" + task.getException());
                                Toast.makeText(LoginActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnline()) {
            setSnackBarNoAction(parent, getResources().getString(R.string.no_internet));
        }
        initDynamicLink();
    }

    public void emailUpdate(final String newEmail) {
        if (getInstance().getCurrentUser() != null) {
            Intent intent = getIntent();
            String emailLink = intent.getData().toString();
            AuthCredential credential =
                    EmailAuthProvider.getCredentialWithLink(getInstance().getCurrentUser().getEmail(), emailLink);
            getInstance().getCurrentUser().reauthenticateAndRetrieveData(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                getInstance().getCurrentUser().updateEmail(newEmail);
                                //Intent i = new Intent(LoginActivity.this, SettingsActivity.class);
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.email_updated_to) + newEmail, Toast.LENGTH_LONG).show();
                                DefaultPrefSettings.getInstance().setUserEmail(newEmail);
                                DefaultPrefSettings.getInstance().removeUpdateEmail();
                                //i.putExtra("transfer", "EmailChanged");
                                //startActivity(i);
                                //finishAndRemoveTask();
                            } else {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_reauthenticating) + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            DefaultPrefSettings.getInstance().removeUpdateEmail();
            signIn();
        }

    }

    public void updateViewOnSignIn() {
        Toast.makeText(LoginActivity.this, getResources().getString(R.string.verification_email_sent), Toast.LENGTH_LONG).show();
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(getResources().getString(R.string.check_email))
                .setMessage(getResources().getString(R.string.link_sent_message))
                .setNegativeButton(android.R.string.yes, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        //tinyUtilities.hideProgressDialog();
//        Intent i = new Intent(rootView.getContext(), MainActivity.class);
//        rootView.getContext().startActivity(i);

    }

    /**
     * Attribution Firebase Auth Docs
     * https://firebase.google.com/docs/auth/android/email-link-auth
     */
    public void sendSignInLink(String email) {
        continueButton.setVisibility(View.INVISIBLE);
        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://positively.page.link")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "me.sankalpchauhan.positively",
                                true, /* installIfNotAvailable */
                                "21"    /* minimumVersion */)
                        .build();
        DefaultPrefSettings.getInstance().setUserEmail(email);
        getInstance().sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        continueButton.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            Timber.d("signInWithEmail:Link send success");
                            updateViewOnSignIn();
                        } else {
                            Timber.e("signInWithEmail:Link send failure", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
