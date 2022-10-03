package org.edx.mobile.view.custom.cache;

import android.content.Context;
import android.content.MutableContextWrapper;
import androidx.util.Pools;

import org.edx.mobile.view.custom.cache.utils.LogUtils;
import org.edx.mobile.view.custom.EdxWebView;
import com.edx.mobile.BuildConfig;

/**
 * A simple webview instance pool.
 * Reduce webview initialization time about 100ms.
 * my test env: vivo-x23, android api: 8.1
 * <p>
 * Created by Ryan
 * at 2019/11/4
 */
public class EdxWebViewPool {

    private static final int MAX_POOL_SIZE = 2;
    private static final Pools.Pool<EdxWebView> sPool = new Pools.SynchronizedPool<>(MAX_POOL_SIZE);

    public static void prepare(Context context) {
        release(acquire(context.getApplicationContext()));
    }

    public static EdxWebView acquire(Context context) {
        EdxWebView webView = sPool.acquire();
        if (webView == null) {
            MutableContextWrapper wrapper = new MutableContextWrapper(context);
            webView = new EdxWebView(wrapper);
            LogUtils.d("create new webview instance.");
        } else {
            MutableContextWrapper wrapper = (MutableContextWrapper) webView.getContext();
            wrapper.setBaseContext(context);
            LogUtils.d("obtain webview instance from pool.");
        }
        return webView;
    }

    public static void release(EdxWebView webView) {
        if (webView == null) {
            return;
        }
        webView.release();
        MutableContextWrapper wrapper = (MutableContextWrapper) webView.getContext();
        wrapper.setBaseContext(wrapper.getApplicationContext());
        sPool.release(webView);
        LogUtils.d("release webview instance to pool.");
    }
}
