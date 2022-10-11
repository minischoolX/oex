package org.edx.mobile.view.custom.cache.okhttp;

import android.content.Context;

import org.edx.mobile.view.custom.cache.cookie.FastCookieManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;


import org.edx.mobile.BuildConfig;
import org.edx.mobile.R;
import org.edx.mobile.http.authenticator.OauthRefreshTokenAuthenticator;
import org.edx.mobile.http.interceptor.NewVersionBroadcastInterceptor;
import org.edx.mobile.http.interceptor.OauthHeaderRequestInterceptor;
import org.edx.mobile.http.interceptor.UserAgentInterceptor;

/**
 * Created by Ryan
 * at 2019/9/26
 */
public class OkHttpClientProvider {

    private static final String CACHE_OKHTTP_DIR_NAME = "cached_webview_okhttp";
    private static final int OKHTTP_CACHE_SIZE = 100 * 1024 * 1024;
    private static volatile OkHttpClientProvider sInstance;
    private OkHttpClient mClient;

    private OauthRefreshTokenAuthenticator oauthRefreshTokenAuthenticator;

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

        this.oauthRefreshTokenAuthenticator = oauthRefreshTokenAuthenticator;

        String dir = context.getCacheDir() + File.separator + CACHE_OKHTTP_DIR_NAME;
        mClient = new OkHttpClient.Builder()
                .cookieJar(FastCookieManager.getInstance().getCookieJar(context))
                .cache(new Cache(new File(dir), OKHTTP_CACHE_SIZE))
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                // auto redirects is not allowed, bc we need to notify webview to do some internal processing.
                .followSslRedirects(false)
                .followRedirects(false)

                .addInterceptor(new UserAgentInterceptor(
                        System.getProperty("http.agent") + " " +
                                context.getString(R.string.app_name) + "/" +
                                BuildConfig.APPLICATION_ID + "/" +
                                BuildConfig.VERSION_NAME))
                .addInterceptor(new OauthHeaderRequestInterceptor(context))
                .addInterceptor(oauthRefreshTokenAuthenticator)
                .addInterceptor(new NewVersionBroadcastInterceptor())
                .authenticator(oauthRefreshTokenAuthenticator)

                .build();
    }

    public static OkHttpClient get(Context context) {
        return getInstance(context).mClient;
    }
}
