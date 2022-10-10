package org.edx.mobile.view.custom.cache.okhttp;

import android.content.Context;

import org.edx.mobile.view.custom.cache.cookie.FastCookieManager;
 
import java.io.File;
//import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Ryan
 * at 2019/9/26
 */
public interface OkHttpClientProvider edxtends Provider<OkHttpClient> {

    @NonNull
    OkHttpClient get()

    @Sigleton
    class Impl implements OkHttpClientProvider {
    private static final String CACHE_OKHTTP_DIR_NAME = "cached_webview_okhttp";
    private static final int OKHTTP_CACHE_SIZE = 100 * 1024 * 1024;
        private static final int FLAG_IS_OAUTH_BASED = 1;
        private static final int USES_OFFLINE_CACHE = 1 << 1;
        private final Context context;
        private final OkHttpClient[] clients = new OkHttpClient[1 << 2];
        private final OauthRefreshTokenAuthenticator oauthRefreshTokenAuthenticator;
        
        public Impl(@ApplicationContext Context context,
                    OauthRefreshTokenAuthenticator oauthRefreshTokenAuthenticator) {
            this.context = context;
            this.oauthRefreshTokenAuthenticator = oauthRefreshTokenAuthenticator;
        }        
        
        @NonNull
        @Override
        public OkHttpClient get() {
            return get(true, false);
        }
        
        
        @NonNull
        private synchronized OkHttpClient get(boolean isOAuthBased, boolean usesOfflineCache) {
            final int index = (isOAuthBased ? FLAG_IS_OAUTH_BASED : 0) |
                    (usesOfflineCache ? USES_OFFLINE_CACHE : 0);
            Boolean isOAuthBased = true;
            Boolean usesOfflineCache = true;
            OkHttpClient client = clients[index];
            if (client == null) {
                final OkHttpClient.Builder builder = new OkHttpClient.Builder();
                List<Interceptor> interceptors = builder.interceptors();
                if (usesOfflineCache) {
//                    final File cacheDirectory = new File(context.getExternalFilesDir(), "http-cache");
//                    if (!cacheDirectory.exists()) {
//                        cacheDirectory.mkdirs();
//                    }
//                    final Cache cache = new Cache(cacheDirectory, cacheSize);
//                    builder.cache(cache);
        String dir = context.getExternalCacheDir() + File.separator + CACHE_OKHTTP_DIR_NAME;
                builder.cache(new Cache(new File(dir), OKHTTP_CACHE_SIZE));

                    interceptors.add(new StaleIfErrorInterceptor());
                    interceptors.add(new StaleIfErrorHandlingInterceptor());
                    builder.networkInterceptors().add(new NoCacheHeaderStrippingInterceptor());
                }
                interceptors.add(new UserAgentInterceptor(
                        System.getProperty("http.agent") + " " +
                                context.getString(R.string.app_name) + "/" +
                                BuildConfig.APPLICATION_ID + "/" +
                                BuildConfig.VERSION_NAME));
                if (isOAuthBased) {
                    interceptors.add(new OauthHeaderRequestInterceptor(context));
                    interceptors.add(oauthRefreshTokenAuthenticator);
                }
                interceptors.add(new NewVersionBroadcastInterceptor());
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    interceptors.add(loggingInterceptor);
                }
//                builder.cookieJar(FastCookieManager.getInstance().getCookieJar(context));
                builder.readTimeout(20, TimeUnit.SECONDS);
                builder.writeTimeout(20, TimeUnit.SECONDS);
                builder.connectTimeout(20, TimeUnit.SECONDS);
                // auto redirects is not allowed, bc we need to notify webview to do some internal processing.
//                .followSslRedirects(false);
//                .followRedirects(false);

                builder.authenticator(oauthRefreshTokenAuthenticator);
                // Enable TLS 1.2 support
                client = Tls12SocketFactory.enableTls12OnPreLollipop(builder).build();
                clients[index] = client;
            }
            return client;
        }
    }
}
//public class OkHttpClientProvider {

//    private static final String CACHE_OKHTTP_DIR_NAME = "cached_webview_okhttp";
//    private static final int OKHTTP_CACHE_SIZE = 100 * 1024 * 1024;
//    private static volatile OkHttpClientProvider sInstance;
//    private OkHttpClient mClient;

//    private OkHttpClientProvider(Context context) {
//        createOkHttpClient(context);
//    }

//    private static OkHttpClientProvider getInstance(Context context) {
//        if (sInstance == null) {
//            synchronized (OkHttpClientProvider.class) {
//                if (sInstance == null) {
//                    sInstance = new OkHttpClientProvider(context);
//                }
//            }
//        }
//        return sInstance;
//    }

    

//    private void createOkHttpClient(Context context) {
//        String dir = context.getCacheDir() + File.separator + CACHE_OKHTTP_DIR_NAME;
//        mClient = new OkHttpClient.Builder()
//                .addInterceptor(new OauthHeaderRequestInterceptor(context))
//                .addInterceptor(oauthRefreshTokenAuthenticator)
//                .cookieJar(FastCookieManager.getInstance().getCookieJar(context))
//                .cache(new Cache(new File(dir), OKHTTP_CACHE_SIZE))
//                .readTimeout(20, TimeUnit.SECONDS)
//                .writeTimeout(20, TimeUnit.SECONDS)
//                .connectTimeout(20, TimeUnit.SECONDS)
//                // auto redirects is not allowed, bc we need to notify webview to do some internal processing.
//                .followSslRedirects(false)
//                .followRedirects(false)
//                .authenticator(oauthRefreshTokenAuthenticator)
//                .build();
//    }

//    public static OkHttpClient get(Context context) {
//        return getInstance(context).mClient;
//    }
//}
