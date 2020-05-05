package me.sankalpchauhan.positively.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.config.DefaultPrefSettings;
import me.sankalpchauhan.positively.service.model.User;
import me.sankalpchauhan.positively.viewmodel.LoginViewModel;

import static me.sankalpchauhan.positively.config.Constants.CREATE_ACCOUNT;
import static me.sankalpchauhan.positively.config.Constants.NAV_TYPE;
import static me.sankalpchauhan.positively.config.Constants.SIGN_IN;
import static me.sankalpchauhan.positively.config.Constants.USER;
import static me.sankalpchauhan.positively.utils.utility.isEmailValid;

public class SignUpActualActivity extends AppCompatActivity {
    @BindView(R.id.up_button)
    ImageButton upImageButton;
    @BindView(R.id.user_email)
    EditText userEmail;
    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.user_password)
    EditText userPsssword;
    @BindView(R.id.user_conf_password)
    EditText userConfPassword;
    @BindView(R.id.forgot_password)
    Button forgotPassword;
    @BindView(R.id.create_acount_login_btn)
    Button createAccountOrLogin;
    @BindView(R.id.title_text_sign_in_up)
    TextView title;
    private LoginViewModel authViewModel;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent catchIntent = getIntent();
        if (catchIntent.hasExtra(NAV_TYPE)) {
            type = catchIntent.getStringExtra(NAV_TYPE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_actual);
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        initViews();
        initAuthViewModel();
        createAccountOrLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCreation()){
                    if(validateSignUpForm()){
                        signUpWithEmail(SignUpActualActivity.this, userEmail.getText().toString(), userPsssword.getText().toString(), userName.getText().toString());
                    }
                } else {
                    if(validateSignInForm()){
                        emailSignIn();
                    }
                }
            }
        });
        upImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userForgotPassword();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.bottom_down);
    }

    public void initViews() {
        if (type.equals(CREATE_ACCOUNT)) {
            forgotPassword.setVisibility(View.GONE);
        } else if (type.equals(SIGN_IN)) {
            createAccountOrLogin.setText(getResources().getString(R.string.sign_up));
            userName.setVisibility(View.GONE);
            userConfPassword.setVisibility(View.GONE);
            title.setText(getResources().getString(R.string.account_login));
            createAccountOrLogin.setText(getResources().getString(R.string.sign_in));
        }
    }

    public void initAuthViewModel() {
        authViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private boolean validateSignInForm() {
        boolean valid = true;

        String email = userEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            userEmail.setError("Required.");
            valid = false;
        } else {
            userEmail.setError(null);
        }

        String password = userPsssword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            userPsssword.setError("Required.");
            valid = false;
        } else {
            userPsssword.setError(null);
        }
        if (password.length() < 6) {
            userPsssword.setError("Password should be at least 6 characters");
        }
        if (!isEmailValid(email)) {
            userEmail.setError("Email Not Valid");
            valid = false;
        }

        return valid;
    }

    private boolean validateSignUpForm() {
        boolean valid = true;

        String useremail = userEmail.getText().toString();
        if (TextUtils.isEmpty(useremail.trim())) {
            userEmail.setError("Required");
            valid = false;
        } else {
            userEmail.setError(null);
        }

        if (!userPsssword.getText().toString().equals(userConfPassword.getText().toString())) {
            userConfPassword.setError("Passwords Do Not Match");
            valid = false;
        }

        String username = userName.getText().toString();
        if (TextUtils.isEmpty(username.trim())) {
            userName.setError("Required.");
            valid = false;
        } else {
            userName.setError(null);
        }

        String userpass = userPsssword.getText().toString();
        if (TextUtils.isEmpty(userpass.trim())) {
            userPsssword.setError("Required.");
            valid = false;
        } else {
            userPsssword.setError(null);
        }

        if (userpass.length() < 6) {
            userPsssword.setError("Password should be at least 6 characters");
        }


        if (!isEmailValid(useremail)) {
            userEmail.setError("Email Not Valid");
            valid = false;
        }


        return valid;
    }

    public boolean isCreation(){
        if(type.equals(SIGN_IN)){
            return false;
        }
        return true;
    }

    private void signUpWithEmail(Context context, String email, String password, String name) {
        authViewModel.signUpWithEmail(context, email, password, name);
        createAccountOrLogin.setVisibility(View.INVISIBLE);
        authViewModel.authenticatedUserLiveData.observe(this, authenticatedUser -> {
            if(authenticatedUser!=null) {
                if(DefaultPrefSettings.getInstance().isUserAnonymous()){
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                    authViewModel.linkCredential(credential);
                    authenticatedUser.isNew = true;
                }
                if (authenticatedUser.isNew) {
                    createNewUser(authenticatedUser);
                } else {
                    goToMainActivity(authenticatedUser);
                }
            } else {
                setCreateAccountOrLoginVisible();
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

    private void goToMainActivity(User user) {
        finishAffinity();
        Intent intent = new Intent(SignUpActualActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
        finish();
    }

    private void toastMessage(String name) {
        Toast.makeText(this, "Hi " + name + "!\n" + "Your account was successfully created. A verification mail was sent.", Toast.LENGTH_LONG).show();
    }

    private void emailSignIn(){
        if(validateSignInForm()){
            authViewModel.signInWithEmail(this, userEmail.getText().toString(), userPsssword.getText().toString());
            createAccountOrLogin.setVisibility(View.INVISIBLE);
            authViewModel.authenticatedUserLiveData.observe(this, authenticatedUser -> {
                if(authenticatedUser!=null) {
                    if (authenticatedUser.isNew) {
                        createNewUser(authenticatedUser);
                    } else {
                        goToMainActivity(authenticatedUser);
                    }
                } else {
                    setCreateAccountOrLoginVisible();
                }
            });
        }
    }

    public void setCreateAccountOrLoginVisible(){
        createAccountOrLogin.setVisibility(View.VISIBLE);
    }

    public void userForgotPassword(){
        if(!userEmail.getText().toString().isEmpty()) {
            authViewModel.sendPasswordReset(this, userEmail.getText().toString());
        } else {
            userEmail.setError("Required");
        }
    }


}
