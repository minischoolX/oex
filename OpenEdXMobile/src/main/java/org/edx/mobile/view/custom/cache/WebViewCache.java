package org.edx.mobile.view.custom.cache;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import org.edx.mobile.view.custom.cache.offline.Destroyable;

/**
 * Created by Ryan
 * 2018/2/7 下午5:06
 */
public interface WebViewCache extends CacheApi, Destroyable {

    WebResourceResponse getResource(WebResourceRequest request, int webViewCacheMode, String userAgent);

}
