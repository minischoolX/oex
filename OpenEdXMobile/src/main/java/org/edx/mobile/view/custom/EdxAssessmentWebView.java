package org.edx.mobile.view.custom;

import android.content.Context;
import android.util.AttributeSet;

public class EdxAssessmentWebView extends EdxWebView {

    private static Context mContext;

    public EdxAssessmentWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getSettings().setSupportZoom(false);
    }

    public static Context getContext() {
        EdxAssessmentWebView.mContext = getApplicationContext();
        return EdxAssessmentWebView.mContext;
    }

}
