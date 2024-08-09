package com.example.util;


import com.example.videostreamingapp.BuildConfig;

import java.io.Serializable;

public class Constant implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private static String SERVER_URL = BuildConfig.SERVER_URL;

    public static final String IMAGE_PATH = SERVER_URL + "images/";

    public static final String API_URL = SERVER_URL + "api/v1/";

    public static final String ARRAY_NAME = "VIDEO_STREAMING_APP";

    public static final String HOME_URL = API_URL + "home";
    public static final String LOGIN_URL = API_URL + "login";
    public static final String LOGIN_SOCIAL_URL = API_URL + "login_social";
    public static final String REGISTER_URL = API_URL + "signup";
    public static final String LANGUAGE_URL = API_URL + "languages";
    public static final String GENRE_URL = API_URL + "genres";
    public static final String SHOW_BY_LANGUAGE_URL = API_URL + "shows_by_language";
    public static final String SHOW_BY_GENRE_URL = API_URL + "shows_by_genre";
    public static final String MOVIE_BY_LANGUAGE_URL = API_URL + "movies_by_language";
    public static final String MOVIE_BY_GENRE_URL = API_URL + "movies_by_genre";
    public static final String SPORT_CATEGORY_URL = API_URL + "sports_category";
    public static final String SPORT_BY_CATEGORY_URL = API_URL + "sports_by_category";
    public static final String TV_CATEGORY_URL = API_URL + "livetv_category";
    public static final String TV_BY_CATEGORY_URL = API_URL + "livetv_by_category";
    public static final String MOVIE_DETAILS_URL = API_URL + "movies_details";
    public static final String SPORT_DETAILS_URL = API_URL + "sports_details";
    public static final String TV_DETAILS_URL = API_URL + "livetv_details";
    public static final String SHOW_DETAILS_URL = API_URL + "show_details";
    public static final String EPISODE_LIST_URL = API_URL + "episodes";
    public static final String PLAN_LIST_URL = API_URL + "subscription_plan";
    public static final String PROFILE_URL = API_URL + "profile";
    public static final String EDIT_PROFILE_URL = API_URL + "profile_update";
    public static final String APP_DETAIL_URL = API_URL + "app_details";
    public static final String SEARCH_URL = API_URL + "search";
    public static final String PAYMENT_SETTING_URL = API_URL + "payment_settings";
    public static final String DASH_BOARD_URL = API_URL + "dashboard";
    public static final String TRANSACTION_URL = API_URL + "transaction_add";
    public static final String FORGOT_PASSWORD_URL = API_URL + "forgot_password";
    public static final String STRIPE_TOKEN_URL = API_URL + "stripe_token_get";
    public static final String EPISODE_RECENTLY_URL = API_URL + "episodes_recently_watched";
    public static final String ACTOR_DETAILS_URL = API_URL + "actor_details";
    public static final String DIRECTOR_DETAILS_URL = API_URL + "director_details";
    public static final String APPLY_COUPON_URL = API_URL + "apply_coupon_code";
    public static final String ADD_TO_WATCHLIST_URL = API_URL + "watchlist_add";
    public static final String REMOVE_FROM_WATCHLIST_URL = API_URL + "watchlist_remove";
    public static final String MY_WATCHLIST_WATCHLIST_URL = API_URL + "my_watchlist";
    public static final String BRAIN_TREE_TOKEN_URL = API_URL + "get_braintree_token";
    public static final String BRAIN_TREE_CHECK_OUT_URL = API_URL + "braintree_checkout";
    public static final String HOME_MORE_URL = API_URL + "home_collections";
    public static final String PRO_PAY_U_HASH_URL = API_URL + "get_payu_hash_new";
    public static final String PAYTM_TXN_URL = API_URL + "get_paytm_token_id";
    public static final String INSTA_MOJO_ORDER_URL = API_URL + "get_instamojo_order_id";
    public static final String CASH_FREE_TOKEN_URL = API_URL + "get_cashfree_token";

    public static final String CATEGORY_NAME = "category_name";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_IMAGE = "category_image";

    public static final String LANGUAGE_ID = "language_id";
    public static final String LANGUAGE_NAME = "language_name";
    public static final String LANGUAGE_IMAGE = "language_image";

    public static final String GENRE_ID = "genre_id";
    public static final String GENRE_NAME = "genre_name";
    public static final String GENRE_IMAGE = "genre_image";
    public static final String GENRE_LIST = "genre_list";

    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_DESC = "description";
    public static final String MOVIE_POSTER = "movie_poster";
    public static final String MOVIE_IMAGE = "movie_image";
    public static final String MOVIE_LANGUAGE = "language_name";
    public static final String MOVIE_DATE = "release_date";
    public static final String MOVIE_URL = "video_url";
    public static final String MOVIE_TYPE = "video_type";
    public static final String MOVIE_DURATION = "movie_duration";
    public static final String MOVIE_ACCESS = "movie_access";
    public static final String DOWNLOAD_ENABLE = "download_enable";
    public static final String DOWNLOAD_URL = "download_url";
    public static final String IMDB_RATING = "imdb_rating";
    public static final String RELATED_MOVIE_ARRAY_NAME = "related_movies";
    public static final String MOVIE_SHARE_LINK = "share_url";
    public static final String MOVIE_TRAILER_URL = "movies_trailer_url";
    public static final String MOVIE_CONTENT_RATING = "content_rating";
    public static final String MOVIE_VIEW = "views";

    public static final String SHOW_ID = "show_id";
    public static final String SHOW_NAME = "show_name";
    public static final String SHOW_TITLE = "show_title";
    public static final String SHOW_DESC = "show_info";
    public static final String SHOW_POSTER = "show_poster";
    public static final String SHOW_LANGUAGE = "show_lang";
    public static final String SHOW_ACCESS = "show_access";
    public static final String RELATED_SHOW_ARRAY_NAME = "related_shows";

    public static final String SEASON_ARRAY_NAME = "season_list";
    public static final String SEASON_ID = "season_id";
    public static final String SEASON_NAME = "season_name";
    public static final String SEASON_IMAGE = "season_poster";
    public static final String SEASON_TRAILER = "trailer_url";

    public static final String EPISODE_ID = "episode_id";
    public static final String EPISODE_TITLE = "episode_title";
    public static final String EPISODE_IMAGE = "episode_image";
    public static final String EPISODE_TYPE = "video_type";
    public static final String EPISODE_URL = "video_url";
    public static final String EPISODE_ACCESS = "video_access";
    public static final String EPISODE_DATE = "release_date";
    public static final String EPISODE_DURATION = "duration";
    public static final String EPISODE_DESC = "description";

    public static final String SPORT_ID = "sport_id";
    public static final String SPORT_TITLE = "sport_title";
    public static final String SPORT_IMAGE = "sport_image";
    public static final String SPORT_URL = "video_url";
    public static final String SPORT_DESC = "description";
    public static final String SPORT_TYPE = "video_type";
    public static final String SPORT_CATEGORY = "category_name";
    public static final String SPORT_DATE = "date";
    public static final String SPORT_DURATION = "sport_duration";
    public static final String SPORT_ACCESS = "sport_access";
    public static final String RELATED_SPORT_ARRAY_NAME = "related_sports";
    public static final String RELATED_TV_ARRAY_NAME = "related_live_tv";


    public static final String TV_ID = "tv_id";
    public static final String TV_TITLE = "tv_title";
    public static final String TV_IMAGE = "tv_logo";
    public static final String TV_ACCESS = "tv_access";
    public static final String TV_URL = "tv_url";
    public static final String TV_DESC = "description";
    public static final String TV_TYPE = "tv_url_type";
    public static final String TV_CATEGORY = "category_name";

    public static final String PLAN_ID = "plan_id";
    public static final String PLAN_NAME = "plan_name";
    public static final String PLAN_DURATION = "plan_duration";
    public static final String PLAN_PRICE = "plan_price";
    public static final String CURRENCY_CODE = "currency_code";
    public static final String PAY_PAL_CLIENT = "paypal_client_id";
    public static final String STRIPE_PUBLISHER = "stripe_publishable_key";
    public static final String RAZOR_PAY_KEY = "razorpay_key";
    public static final String PAY_STACK_KEY = "paystack_public_key";
    public static final String PAYMENT_MODE = "mode";
    public static final String PAY_U_MERCHANT_ID = "payu_merchant_id";
    public static final String PAY_U_MERCHANT_KEY = "payu_key";
    public static final String PAYTM_MID = "paytm_merchant_id";
    public static final String CASHFREE_APPID = "cashfree_appid";
    public static final String FW_PUBLIC_KEY = "flutterwave_public_key";
    public static final String FW_ENCRYPTION_KEY = "flutterwave_encryption_key";

    public static final String APP_NAME = "app_name";
    public static final String APP_IMAGE = "app_logo";
    public static final String APP_VERSION = "app_version";
    public static final String APP_AUTHOR = "app_company";
    public static final String APP_CONTACT = "app_contact";
    public static final String APP_EMAIL = "app_email";
    public static final String APP_WEBSITE = "app_website";
    public static final String APP_DESC = "app_about";
    public static final String APP_PRIVACY_POLICY = "app_privacy";
    public static final String APP_TERMS = "app_terms";

    public static final String USER_NAME = "name";
    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONE = "phone";
    public static final String USER_ADDRESS = "user_address";
    public static final String USER_IMAGE = "user_image";
    public static final String USER_PLAN_STATUS = "user_plan_status";
    public static final String USER_WATCHLIST_STATUS = "in_watchlist";
    public static final String UPCOMING_STATUS = "upcoming";

    public static final String FILTER_NEWEST = "new";
    public static final String FILTER_OLDEST = "old";
    public static final String FILTER_ALPHA = "alpha";
    public static final String FILTER_RANDOM = "rand";


    public static final String IS_QUALITY = "video_quality";
    public static final String IS_SUBTITLE = "subtitle_on_off";
    public static final String QUALITY_480 = "video_url_480";
    public static final String QUALITY_720 = "video_url_720";
    public static final String QUALITY_1080 = "video_url_1080";
    public static final String SUBTITLE_LANGUAGE_1 = "subtitle_language1";
    public static final String SUBTITLE_URL_1 = "subtitle_url1";
    public static final String SUBTITLE_LANGUAGE_2 = "subtitle_language2";
    public static final String SUBTITLE_URL_2 = "subtitle_url2";
    public static final String SUBTITLE_LANGUAGE_3 = "subtitle_language3";
    public static final String SUBTITLE_URL_3 = "subtitle_url3";

    public static final String ACTOR_ARRAY = "actor_list";
    public static final String DIRECTOR_ARRAY = "director_list";
    public static final String ACTOR_ID = "ad_id";
    public static final String ACTOR_NAME = "ad_name";
    public static final String ACTOR_IMAGE = "ad_image";

    public static final String WATCHLIST_ID = "id";
    public static final String WATCHLIST_POST_ID = "post_id";
    public static final String WATCHLIST_POST_TITLE = "post_title";
    public static final String WATCHLIST_POST_TYPE = "post_type";
    public static final String WATCHLIST_POST_IMAGE = "post_image";

    public static int GET_SUCCESS_MSG;
    public static final String MSG = "msg";
    public static final String SUCCESS = "success";
    public static final String STATUS = "status";
    public static int adCountIncrement = 0;
    public static int interstitialAdCount;

    public static boolean isBanner = false, isInterstitial = false;
    public static final String admobAd = "1", startAppAd = "2", facebookAd = "3", appLovinMaxAd = "4", wortiseAd = "5";
    public static String adNetworkType;
    public static String bannerId, interstitialId, appIdOrPublisherId;

    public static boolean isAppUpdate = false, isAppUpdateCancel = false;
    public static int appUpdateVersion;
    public static String appUpdateUrl, appUpdateDesc;

    //menu
    public static boolean isMovieMenu = true, isShowMenu = true, isTvMenu = true, isSportMenu = true;
}
