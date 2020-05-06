package me.sankalpchauhan.positively.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.BuildConfig;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.config.DefaultPrefSettings;
import me.sankalpchauhan.positively.service.model.User;
import me.sankalpchauhan.positively.viewmodel.LoginViewModel;
import me.sankalpchauhan.positively.viewmodel.SignUpViewModel;
import timber.log.Timber;

import static me.sankalpchauhan.positively.config.Constants.CREATE_ACCOUNT;
import static me.sankalpchauhan.positively.config.Constants.NAV_TYPE;
import static me.sankalpchauhan.positively.config.Constants.RC_SIGN_IN;
import static me.sankalpchauhan.positively.config.Constants.SIGN_IN;
import static me.sankalpchauhan.positively.config.Constants.USER;

public class SignUpActivity extends AppCompatActivity {
//    private boolean isUserAnonymous=false;
    private GoogleSignInClient googleSignInClient;
    private SignUpViewModel authViewModel;
    @BindView(R.id.up_button)
    ImageButton upImageButton;
    @BindView(R.id.create_new_account)
    Button newAccountButton;
    @BindView(R.id.sign_in_with_email)
    Button signInButton;
    @BindView(R.id.sign_in_with_google)
    Button signInGoogle;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        initAuthViewModel();
        initGoogleSignInClient();
        newAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, SignUpActualActivity.class);
                i.putExtra(NAV_TYPE, CREATE_ACCOUNT);
                startActivity(i);
                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, SignUpActualActivity.class);
                i.putExtra(NAV_TYPE, SIGN_IN);
                startActivity(i);
                overridePendingTransition(R.anim.bottom_up, R.anim.nothing);
            }
        });
        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        upImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
    }

    private void initAuthViewModel() {
        authViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    getGoogleAuthCredential(googleSignInAccount);
                }
            } catch (ApiException e) {
                Timber.e(e.getMessage());
                linearLayout.setVisibility(View.VISIBLE);
            }
        } else {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenId = googleSignInAccount.getIdToken();
        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenId, null);
        signInWithGoogleAuthCredential(googleAuthCredential);
    }

    private void signInWithGoogleAuthCredential(AuthCredential googleAuthCredential) {
        authViewModel.signInWithGoogle(googleAuthCredential);
        authViewModel.authenticatedUserLiveData.observe(this, authenticatedUser -> {
            if(authenticatedUser!=null) {
                if(DefaultPrefSettings.getInstance().isUserAnonymous()){
                    authViewModel.linkCredential(googleAuthCredential);
                    authenticatedUser.isNew = true;
                }
                if (authenticatedUser.isNew) {
                    createNewUser(authenticatedUser);
                } else {
                    goToMainActivity(authenticatedUser);
                }
            } else {
                linearLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createNewUser(User authenticatedUser) {
        authViewModel.createUser(authenticatedUser);
        authViewModel.createdUserLiveData.observe(this, user -> {
            if (user.isCreated) {
                toastMessage(user.name);
            }
            goToMainActivity(user);
        });
    }

    private void toastMessage(String name) {
        Toast.makeText(this, getResources().getString(R.string.hi) + name + "!\n" + getResources().getString(R.string.acount_success_creation), Toast.LENGTH_LONG).show();
    }

    private void goToMainActivity(User user) {
        finishAffinity();
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
        finish();
    }

    private void signIn() {
        linearLayout.setVisibility(View.INVISIBLE);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.WEBCLIENT_ID)
                .requestEmail()
                .build();

        googleSignInClient =  GoogleSignIn.getClient(this, googleSignInOptions);
    }
}
