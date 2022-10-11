package org.edx.mobile.view.custom.cache.okhttp;

import android.content.Context;

import androidx.annotation.Nullable;

import org.edx.mobile.view.custom.cache.cookie.FastCookieManager;
import org.edx.mobile.http.interceptor.OauthHeaderRequestInterceptor;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionSpec;
import okhttp3.TlsVersion;
import okhttp3.Interceptor;
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

    private static @Nullable OkHttpClient sClient;

    public static OkHttpClient getOkHttpClient() {
        if (sClient == null) {
            sClient = createClient();
        }
        retutn sClient;
    }

    public static void replaceOkHttpClient(OkHttpClient client) {
        sClient = client;
    }

    public static OkHttpClient createClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        List<Interceptor> interceptors = mClientBuilder.interceptors();
        interceptors.add(new OauthHeaderRequestInterceptor(context));

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(FastCookieManager.getInstance().getCookieJar(context))
                .cache(new Cache(new File(dir), OKHTTP_CACHE_SIZE))
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                // auto redirects is not allowed, bc we need to notify webview to do some internal processing.
                .followSslRedirects(false)
                .followRedirects(false);

                return enableTls().build();
//                .build();
    }

    public static OkHttpClient.Builder enableTls(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                client.sslSocketFactory(new TLSSocketFactory());

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.ConnectionSpec(specs);
            } catch (Exception exc) {
                FLog.e("OkHttpClientProvider","Error while enabling TLS 1.2",exc);
            }
        }
        return client;
    }

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

        OkHttpClient.Builder mClientBuilder = new OkHttpClient.Builder();
        List<Interceptor> interceptors = mClientBuilder.interceptors();
        interceptors.add(new OauthHeaderRequestInterceptor(context));
        mClient = mClientBuilder
                .cookieJar(FastCookieManager.getInstance().getCookieJar(context))
                .cache(new Cache(new File(dir), OKHTTP_CACHE_SIZE))
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                // auto redirects is not allowed, bc we need to notify webview to do some internal processing.
                .followSslRedirects(false)
                .followRedirects(false)
                .build();
    }

    public static OkHttpClient get(Context context) {
        return getInstance(context).mClient;
    }
}
