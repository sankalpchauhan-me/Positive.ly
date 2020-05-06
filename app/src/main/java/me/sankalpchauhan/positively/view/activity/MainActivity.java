package me.sankalpchauhan.positively.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import me.sankalpchauhan.positively.service.model.Quotes;
import me.sankalpchauhan.positively.service.model.ServerResult;
import me.sankalpchauhan.positively.view.fragments.PodcastFragment;
import me.sankalpchauhan.positively.view.fragments.QuotesFragment;
import me.sankalpchauhan.positively.viewmodel.MainActivityViewModel;
import timber.log.Timber;

import static me.sankalpchauhan.positively.config.Constants.SERVER_DATA;
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
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout shimmerFrameLayout;
    private int appVersionCode;
    private String appVersionName;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    private List<Quotes> quotesList = new ArrayList<>();
    private List<String> quotesImageUrlList = new ArrayList<>();
    private static boolean isPodcast = true;

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
        shimmerFrameLayout.startShimmer();
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
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this, (View) recordFab, getResources().getString(R.string.record_fab_transition));
                startActivity(i, options.toBundle());
            }
        });

        if(savedInstanceState!=null){
            isPodcast = savedInstanceState.getBoolean("SAVE_BUNDLE_MAIN");
            Timber.e("Value of podcast: "+ isPodcast);
        }
    }

    private void initViewModel() {
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mainActivityViewModel.init();
        mainActivityViewModel.getPositivityPodcasts().observe(this, new Observer<ServerResult>() {
            @Override
            public void onChanged(ServerResult serverResult) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                //TODO: HIDE SHIMMER
                if (serverResult != null) {
                    List<Podcast> fetchedPodcastList = serverResult.getPodcasts();
                    podcastList.addAll(fetchedPodcastList);
                    PodcastFragment podcastFragment = new PodcastFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SERVER_DATA, serverResult);
                    loadFragment(podcastFragment);
                    for (Podcast podcast : podcastList) {
                        Timber.e("Podcast List: " + podcast.getTitleOriginal());
                    }
                    //TODO: HIDE EMPTY STATE
                } else {
                    Toast.makeText(MainActivity.this, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                    //TODO: SHOW EMPTY STATE
                }
            }
        });
        mainActivityViewModel.getQuoteList().observe(this, new Observer<List<Quotes>>() {
            @Override
            public void onChanged(List<Quotes> quotes) {
                if (quotes != null) {
                    quotesList.addAll(quotes);
                    if(!isPodcast){
                        loadFragment(new QuotesFragment());
                        toolbar.setTitle(getResources().getString(R.string.positively_quotes));
                    }
                } else {
                    Timber.e("Unable to Fetch quotes");
                }
            }
        });
        mainActivityViewModel.getQuotesImageUrl().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                if (strings != null) {
                    quotesImageUrlList.addAll(strings);
                } else {
                    Timber.e("Unable to Fetch Image Urls");
                }
            }
        });
    }

    private void initDrawer() {
        String email, name;
        if (isAuthenticated() != null && !DefaultPrefSettings.getInstance().isUserAnonymous()) {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null || FirebaseAuth.getInstance().getCurrentUser().getDisplayName().isEmpty()) {
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
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.navigation_podcast:
                toolbar.setTitle(getResources().getString(R.string.positively_podcasts));
                isPodcast=true;
                fragment = new PodcastFragment();
                break;
            case R.id.navigation_record:
                break;
            case R.id.navigation_quotes:
                toolbar.setTitle(getResources().getString(R.string.positively_quotes));
                isPodcast=false;
                fragment = new QuotesFragment();
                break;
        }
        return loadFragment(fragment);
    }

    public boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public List<Podcast> getPodcastList() {
        return podcastList;
    }

    public List<Quotes> getQuotesList() {
        return quotesList;
    }

    public List<String> getQuotesImageUrlList() {
        return quotesImageUrlList;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("SAVE_BUNDLE_MAIN", isPodcast);
    }

    @Override
    public void onBackPressed() {
        //finishAndRemoveTask();
        super.onBackPressed();
    }
}
