package org.edx.mobile.view.custom.cache.offline;

import android.webkit.WebResourceResponse;

/**
 * Created by Ryan
 * at 2019/9/27
 */
public interface OfflineServer {

    WebResourceResponse get(CacheRequest request);

    void addResourceInterceptor(ResourceInterceptor interceptor);

    void destroy();
}
