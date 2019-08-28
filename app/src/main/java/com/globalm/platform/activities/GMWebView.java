//package com.globalm.platform.activities;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//
//import com.globalm.platform.R;
//
//import static com.globalm.platform.activities.LoginActivity.REQUEST_CODE_SIGN_IN_WITH_SOCIAL_NETWORK;
//
//public class GMWebView extends Activity {
//
//    WebView mWebView;
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_web_view);
//        String url = getIntent().getStringExtra("url");
//
//        try {
//            mWebView = findViewById(R.id.web_view);
//            mWebView.setWebViewClient(new WebViewClient() {
//                @Override
//                public void onPageFinished(WebView view, String url) {
//                    if (url.contains("code")) {
//                        Intent intent = new Intent();
//                        intent.putExtra("code", url.substring(view.getUrl().indexOf("code") + 7));
//                        setResult(REQUEST_CODE_SIGN_IN_WITH_SOCIAL_NETWORK, intent);
//                        finish();
//                    }
//                }
//            });
//
//            mWebView.getSettings().setJavaScriptEnabled(true);
//            mWebView.getSettings().setDomStorageEnabled(true);
//            mWebView.getSettings().setSavePassword(false);
//            mWebView.getSettings().setSaveFormData(false);
//            mWebView.getSettings().setSupportZoom(false);
//            mWebView.loadUrl(url);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}