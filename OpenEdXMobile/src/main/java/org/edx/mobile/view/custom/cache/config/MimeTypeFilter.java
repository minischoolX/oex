package org.edx.mobile.view.custom.cache.config;

/**
 * filter some mime type resources without caching.
 * <p>
 * Created by Ryan
 * 2018/2/11 下午2:56
 */
public interface MimeTypeFilter {

    boolean isFilter(String mimeType);

    void addMimeType(String mimeType);

    void removeMimeType(String mimeType);

    void clear();

}
