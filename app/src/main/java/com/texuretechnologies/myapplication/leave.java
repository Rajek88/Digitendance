package com.texuretechnologies.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class leave extends Activity {

    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        browser = (WebView) findViewById(R.id.urlLoader);


        browser.setWebViewClient(new WebViewClient());
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        browser.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSfNrmdqADotdPEu7incpIl3TvJT5n7jB4X2iZyIsX2G5svceg/viewform?usp=sf_link");


    }
}
