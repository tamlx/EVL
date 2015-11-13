package com.thanhle.englishvocabulary.utils;

import com.thanhle.englishvocabulary.resource.LibraryInfosResource;
import com.thanhle.englishvocabulary.resource.LibraryResource;

import java.util.ArrayList;

public class Consts {
    public static final String MY_LIBRARY = "my_library";
    public static final String DEFAULT_LIBRARY = "1000words";
    public static final int MAX_WORD_SELECT = 10;
    public static final int DICTIONARY_COUNT = 387524;
    public static final String VOICE_FOLDER_NAME = "voices";
    public static final String DICT_FOLDER_NAME = "dict";
    public static final String EXTRACT_FOLDER_NAME = "extract";

    public static final int MAX_NEW_WORD_PER_TEST = 10;
    public static final int MAX_WORD_PER_TEST = 20;
    public static final int MAX_TESTING = 3;
    public static final int MAX_TEST_TIMER = 60;
    public static final int MAX_TEST_TIMER_CHANGE = 5;
    public static final int COUNT_SHOW_RATE = 10;

    public static final String ADS_TEST_ACTIVITY = "ca-app-pub-6665274619412953/2882042826";
    public static final String ADS_LIBRARY_ACTIVITY = "ca-app-pub-6665274619412953/1896334020";
    public static String USER_FB_ID = "";
    public static String USER_GG_ID = "";
    public static String PERSON_PHOTO_URL = "";
    public static String USERNAME_FB_GG = "";
    public static final String LOGINFACEBOOK = "login_facebook";
    public static final String LOGINGOOGLE = "login_google";
    public static final String LINK_PICTURE = "link_pictrue";
    public static ArrayList<LibraryResource> LIST_LIBRARY = new ArrayList<LibraryResource>();
    public static ArrayList<LibraryResource> LIST_LIBRARY_SEARCH = new ArrayList<LibraryResource>();
    public static LibraryInfosResource LIBRARY_INFO = new LibraryInfosResource();


    public static boolean updateprofile = false;
    public static boolean install_new_library = false;

    public static final String PURCHASE_REMOVE_ADS = "com.thanhle.englishvocabulary.removeads";
    public static final String PURCHASE_LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhZ3VcPEedi2W4n8c76moIZwzzflYpQe7GIcxdCMAW0xH+h8zMhBdxqsyiea7l5Zd0TnHEfbVoMc1BN26gsHGDSza/Ph2ET4zXGRDSXHK0xiqaN2c9+LQsYpMCdi+tmF/Wg85AfjAwcN6tojs4aWe2/dM48Rk6OOG8EMWDHz9Ap1QLDKFLwuh95MbD2CJR0nnelHmIiGOhGWJTUpwu+jRf/BNLmyXPD7hh0P0Crl28pnTUfdszZQBbRpP42aAxWK6B6V8bVx0+HSbUuNeXPX1/LSMOUvQnsRYoaFzKg8ungltDKnv6ez+ec1dO1yjGWnsABTUYFkiRepqIyonmLh1gQIDAQAB";

    public static final String HOST_API = "http://vocabulary-learning.herokuapp.com/";

    public class WearConsts {
        public static final String SYNC_PATH = "/sync";
        public static final String CARDS_KEY = "cards";
        public static final String LIBRARY_KEY = "library";
        public static final String DATA_ITEM_RECEIVED_PATH = "/data_item_received";
        public static final String START_ACTIVITY_PATH = "/start_activity";
    }

    public class PARAMConsts {
        public static final String USERNAME = "username";
        public static final String FULLNAME = "fullname";
        public static final String PASSWORD = "password";
        public static final String TOKEN = "token";
        public static final String OLDPASSWORD = "oldpassword";
        public static final String EMAIL = "email";
        public static final String PROVIDER = "provider";
        public static final String KEY_SEARCH = "key_search";
        public static final String KEY_LIBRARY_ID = "library_id";

    }

    public class URLConsts {
        public static final String LOGIN_URL = HOST_API + "/api/user/login";
        public static final String REGISTER_URL = HOST_API + "/api/user/register";
        public static final String UPDATEPROFILE_URL = HOST_API + "/api/user/";
        public static final String GETLISTLIBRARY = HOST_API + "/api/library";
        public static final String SEARCHLIBRARY = HOST_API + "api/library/search/";
        public static final String GETLIBRARYINFO = HOST_API + "api/library/";
        public static final String DOWNLOAD_LIBRARY = HOST_API + "api/library/download/";
    }
}
