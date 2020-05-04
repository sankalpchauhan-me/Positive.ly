package me.sankalpchauhan.positively.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.sankalpchauhan.positively.R;
import me.sankalpchauhan.positively.config.DefaultPrefSettings;
import me.sankalpchauhan.positively.service.model.Podcast;
import me.sankalpchauhan.positively.service.model.ServerResult;
import me.sankalpchauhan.positively.viewmodel.MainActivityViewModel;
import timber.log.Timber;

import static me.sankalpchauhan.positively.utils.utility.setSnackBar;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener, BottomNavigationView.OnNavigationItemSelectedListener {
    List<Podcast> podcastList = new ArrayList<>();
    MainActivityViewModel mainActivityViewModel;
    @BindView(R.id.record_fab)
    FloatingActionButton recordFab;
    @BindView(R.id.parent_activity_main)
    CoordinatorLayout parent;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    private int appVersionCode;
    private String appVersionName;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;

    public static FirebaseUser isAuthenticated() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.setTitle(getResources().getString(R.string.positively_podcasts));
        setSupportActionBar(toolbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        initViewModel();
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            appVersionName = pInfo.versionName;
            if (android.os.Build.VERSION.SDK_INT >= 28) {
                appVersionCode = (int) pInfo.getLongVersionCode();
            } else {
                appVersionCode = pInfo.versionCode;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (firebaseAuth.getCurrentUser().isAnonymous()) {
            DefaultPrefSettings.getInstance().setUserAnonymous(true);
        } else {
            DefaultPrefSettings.getInstance().setUserAnonymous(false);
            if (!firebaseAuth.getCurrentUser().isEmailVerified()) {
                setSnackBar(parent, "Email is not verified");
            }
        }
        initDrawer();
        initGoogleSignInClient();
        recordFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RecorderActivity.class);
                startActivity(i);
            }
        });
    }

    private void initViewModel(){
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainActivityViewModel.init();
        mainActivityViewModel.getPositivityPodcasts().observe(this, new Observer<ServerResult>() {
            @Override
            public void onChanged(ServerResult serverResult) {
                //TODO: HIDE SHIMMER
                if(serverResult!=null){
                    List<Podcast> fetchedPodcastList = serverResult.getPodcasts();
                    podcastList.addAll(fetchedPodcastList);
                    Timber.e(podcastList.toString());
                    //TODO: HIDE EMPTY STATE
                } else {
                    Toast.makeText(MainActivity.this, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                    //TODO: SHOW EMPTY STATE
                }
            }
        });
    }

    private void initDrawer() {
        String email, name;
        if (isAuthenticated() != null && !DefaultPrefSettings.getInstance().isUserAnonymous()) {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()==null || FirebaseAuth.getInstance().getCurrentUser().getDisplayName().isEmpty()) {
                //Timber.d("I am here 2"+String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                String[] nameemail = email.split("@");
                name = nameemail[0];
            } else {
                name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            }
        } else {
            email = "Click to Log In/Sign Up";
            name = "User";
        }

        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
            DrawerImageLoader.init(new AbstractDrawerImageLoader() {
                @Override
                public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                    Picasso.get().load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(imageView);
                }
            });
        }

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionFirstLine(name)
                .withSelectionSecondLine(email)
                .withHeaderBackground(R.color.colorPrimary)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile profile) {
                        if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);
                            finishAffinity();
                        }
                        return false;
                    }
                })
                .build();

        Drawer drawer = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName("Sign Out"),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Build Version: " + appVersionName).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case 1:
                                signOut();
                                return false;
                        }
                        return true;
                    }
                })
                .build();
    }

    /**
     * This is not working don't know wht
     *
     * @param firebaseAuth
     */
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            DefaultPrefSettings.getInstance().setUserAnonymous(false);
            goToHomePageActivity();
        }
    }

    private void goToHomePageActivity() {
        finishAffinity();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void signOut() {
        singOutFirebase();
        signOutGoogle();
    }

    private void singOutFirebase() {
        firebaseAuth.signOut();
    }

    private void signOutGoogle() {
        googleSignInClient.signOut();
    }

    private void initGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.navigation_podcast:
                toolbar.setTitle(getResources().getString(R.string.positively_podcasts));
                return true;
            case R.id.navigation_record:
                return true;
            case R.id.navigation_quotes:
                toolbar.setTitle(getResources().getString(R.string.positively_quotes));
                return true;
        }
        return false;
    }

    /**
     * HttpResponse<JsonNode> response = Unirest.get("https://listen-api.listennotes.com/api/v2/search?q=star%20wars&sort_by_date=0&type=episode&offset=0&len_min=10&len_max=30&genre_ids=68%2C82&published_before=1580172454000&published_after=0&only_in=title%2Cdescription&language=English&safe_mode=0")
     *   .header("X-ListenAPI-Key", "<SIGN UP FOR API KEY>")
     *   .asJson();
     */

}
