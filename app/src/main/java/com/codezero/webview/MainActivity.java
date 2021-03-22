package com.codezero.webview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicLongArray;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    String myUrl= "https://shamim438.blogspot.com/";
    ProgressBar progressBar;
    RelativeLayout relativeLayout;
    Button retry;
    ProgressDialog pd;
    SwipeRefreshLayout sRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView_id);
        progressBar = findViewById(R.id.progress_id);
        relativeLayout = findViewById(R.id.relativeLayout_id);
        retry = findViewById(R.id.retry_id);
        sRL = findViewById(R.id.sRl_id);
        sRL.setColorSchemeColors(Color.RED,Color.YELLOW,Color.GREEN);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading please wait");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        checkPermission();
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                checkPermission();
            }
        });
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                sRL.setRefreshing(false);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                view.loadUrl(myUrl);
                return true;
            }
        });
        sRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl(myUrl);
            }
        });

        /* // if full screen size
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                setTitle("Loading...");
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if (newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                    setTitle(view.getTitle());
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    public void checkPermission(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = cm.getNetworkInfo(cm.TYPE_MOBILE);
        NetworkInfo wifi = cm.getNetworkInfo(cm.TYPE_WIFI);
        if (mobile.isConnected()){
            pd.dismiss();
            webView.loadUrl(myUrl);
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.INVISIBLE);

        }
        else if (wifi.isConnected()){
            pd.dismiss();
            webView.loadUrl(myUrl);
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.INVISIBLE);

        }
        else {
            pd.dismiss();
            Toast.makeText(this, "Please connect to wifi or data", Toast.LENGTH_SHORT).show();
            webView.setVisibility(View.INVISIBLE);
            relativeLayout.setVisibility(View.VISIBLE);

        }

    }



    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }
        else{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Are you sure you want to exit")
                    .setNegativeButton("No",null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    }).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_previous:
                onBackPressed();
                break;

            case R.id.nav_next:
                if (webView.canGoForward()){
                    webView.goForward();
                }
                break;

            case R.id.nav_refresh:
                webView.reload();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}