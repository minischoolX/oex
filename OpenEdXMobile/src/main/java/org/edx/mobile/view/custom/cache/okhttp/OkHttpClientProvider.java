package org.edx.mobile.view.custom.cache.okhttp;

import android.content.Context;

import org.edx.mobile.view.custom.cache.cookie.FastCookieManager;


import android.content.Context;

import androidx.annotation.NonNull;

import org.edx.mobile.BuildConfig;
import org.edx.mobile.R;
import org.edx.mobile.http.authenticator.OauthRefreshTokenAuthenticator;
import org.edx.mobile.http.interceptor.NewVersionBroadcastInterceptor;
import org.edx.mobile.http.interceptor.NoCacheHeaderStrippingInterceptor;
import org.edx.mobile.http.interceptor.OauthHeaderRequestInterceptor;
import org.edx.mobile.http.interceptor.StaleIfErrorHandlingInterceptor;
import org.edx.mobile.http.interceptor.StaleIfErrorInterceptor;
import org.edx.mobile.http.interceptor.UserAgentInterceptor;
import org.edx.mobile.http.util.Tls12SocketFactory;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;



import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Ryan
 * at 2019/9/26
 */
public class OkHttpClientProvider {

    private static final String CACHE_OKHTTP_DIR_NAME = "cached_webview_okhttp";
    private static final int OKHTTP_CACHE_SIZE = 100 * 1024 * 1024;
    private static volatile OkHttpClientProvider sInstance;
    private OkHttpClient mClient;


        private final Context context;

        private final OauthRefreshTokenAuthenticator oauthRefreshTokenAuthenticator;

//        @Inject
//        public OkHttpClientProvider(@ApplicationContext Context context,
//                    OauthRefreshTokenAuthenticator oauthRefreshTokenAuthenticator) {
//            this.context = context;
//            this.oauthRefreshTokenAuthenticator = oauthRefreshTokenAuthenticator;
//        }



    private OkHttpClientProvider(Context context) {
        createOkHttpClient(context);
    }

    private static OkHttpClientProvider getInstance(Context context) {
        if (sInstance == null) {
            synchronized (OkHttpClientProvider.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpClientProvider(context);
                }
            }
        }
        return sInstance;
    }


    private void createOkHttpClient(Context context) {
        String dir = context.getCacheDir() + File.separator + CACHE_OKHTTP_DIR_NAME;
        OkHttpClient.Builder mClient = new OkHttpClient.Builder();
        List<Interceptor> interceptors = mClient.interceptors();

                    interceptors.add(new StaleIfErrorInterceptor());
                    interceptors.add(new StaleIfErrorHandlingInterceptor());
                    mClient.networkInterceptors().add(new NoCacheHeaderStrippingInterceptor());
        
                interceptors.add(new UserAgentInterceptor(
                        System.getProperty("http.agent") + " " +
                                context.getString(R.string.app_name) + "/" +
                                BuildConfig.APPLICATION_ID + "/" +
                                BuildConfig.VERSION_NAME));

                    interceptors.add(new OauthHeaderRequestInterceptor(context));
                    interceptors.add(oauthRefreshTokenAuthenticator);

                interceptors.add(new NewVersionBroadcastInterceptor());
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    interceptors.add(loggingInterceptor);
                }
                mClient.authenticator(oauthRefreshTokenAuthenticator);

                mClient.cookieJar(FastCookieManager.getInstance().getCookieJar(context));
                mClient.cache(new Cache(new File(dir), OKHTTP_CACHE_SIZE));
                mClient.readTimeout(20, TimeUnit.SECONDS);
                mClient.writeTimeout(20, TimeUnit.SECONDS);
                mClient.connectTimeout(20, TimeUnit.SECONDS);
                // auto redirects is not allowed, bc we need to notify webview to do some internal processing.
                mClient.followSslRedirects(false);
                mClient.followRedirects(false);
                mClient.build();
    }

    public static OkHttpClient get(Context context) {
        return getInstance(context).mClient;
    }
}
