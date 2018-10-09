package com.example.user.mobilemicroscopy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class SubmitActivity extends AppCompatActivity {


    private WebView webview;

    /**
     * passed annotated image file name from DetailsActivity
     */
    String mCurrentOriginalImageFileName;
    String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        // get the intent passed in
        Intent intent = getIntent();

        // get the link
        Bundle bundle = intent.getExtras();
        mCurrentOriginalImageFileName = bundle.getString("originalFileName");
        mUsername = bundle.getString("username");

        webview =(WebView)findViewById(R.id.webView);

        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);


        //Retrieve web page to submit assignment
        webview.loadUrl("http://ec2-13-210-117-22.ap-southeast-2.compute.amazonaws.com/webviewaassginment.php?imageID="+mCurrentOriginalImageFileName+"&username="+mUsername);

        Toast.makeText(getApplicationContext(), "Submit Work", Toast.LENGTH_SHORT).show();

    }
}
