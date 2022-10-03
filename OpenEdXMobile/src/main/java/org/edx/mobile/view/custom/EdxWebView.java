package org.edx.mobile.view.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.edx.mobile.BuildConfig;
import org.edx.mobile.R;

import org.edx.mobile.view.custom.cache.CacheApi;
import org.edx.mobile.view.custom.cache.config.CacheConfig;
import org.edx.mobile.view.custom.cache.config.FastCacheMode;
import org.edx.mobile.view.custom.cache.cookie.FastCookieManager;
import org.edx.mobile.view.custom.cache.offline.ResourceInterceptor;
import org.edx.mobile.view.custom.cache.utils.LogUtils;


public class EdxWebView extends WebView implements CacheApi {

    private boolean mRecycled = false;

    @SuppressLint("SetJavaScriptEnabled")
    public EdxWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setDomStorageEnabled(true);
        settings.setUserAgentString(
                settings.getUserAgentString() + " " +
                        context.getString(R.string.app_name) + "/" +
                        BuildConfig.APPLICATION_ID + "/" +
                        BuildConfig.VERSION_NAME
        );
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void destroy() {
        release();
        super.destroy();
    }


    public void release() {
        stopLoading();
        loadUrl("");
        setRecycled(true);
        setWebViewClient(null);
        setWebChromeClient(null);
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setBlockNetworkImage(false);
        clearHistory();
        clearCache(true);
        removeAllViews();
        ViewParent viewParent = this.getParent();
        if (viewParent != null && viewParent instanceof ViewGroup) {
            ((ViewGroup) viewParent).removeView(this);
        }
        if (mFastClient != null) {
            mFastClient.destroy();
        }
        getFastCookieManager().destroy();
    }

    public void setCacheMode(FastCacheMode mode) {
        setCacheMode(mode, null);
    }

    @Override
    public void setCacheMode(FastCacheMode mode, CacheConfig cacheConfig) {
        //TODO: add this
    }

    public void addResourceInterceptor(ResourceInterceptor interceptor) {
        //TODO: add this
    }

}
