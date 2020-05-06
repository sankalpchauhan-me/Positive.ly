package me.sankalpchauhan.positively.config;

public class Constants {
    public static final String USERS_COLLECTION = "users";
    public static final String NAV_TYPE = "navigation_purpose";
    public static final String CREATE_ACCOUNT = "createAccount";
    public static final String SIGN_IN = "signIn";
    public static final String USER = "user";
    public static final int RC_SIGN_IN = 123;
    public static final String USER_IS_ANONYMOUS = "anonymousUserLinkWithCredential";
    public static final int MY_PERMISSIONS_RECORD = 1101;
    public static final String RECORDING_PATH = "/Positively/Affirmations";
    public static final String SERVER_DATA = "server_data";
    public static final String PODCAST_DATA = "podcast_data";
    public static final String PLAY_BACK_POSITION = "playBackPosition";
    public static final String PLAY_WHEN_READY = "playWhenReady";
    public static final String QUOTES_IMAGES_COLLECTION_REFERENCE = "quotes_images";
    public static final String QUOTES_IMAGES_DOCUMENT_REFERENCE = "quotesImages";
    public static final String IMAGE_ARRAY = "imageArray";
    public static final String QUOTES_DATA = "quotesData";
    public static final String QUOTES_IMAGE_DATA = "imageData";
    //max number of retries before fail
    public static final int maxLimit = 3;
    // wait for 5 second before retrying network request
    public static final int waitThreshold = 5000;
}
