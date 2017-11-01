package com.kosta148.team1.goodmorning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by 정다린 on 2017-04-27.
 */

public class WebViewActivity extends AppCompatActivity {
    Handler handler = new Handler();
    String address = "";
    String title = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // 액션바
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("기사 상세보기");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        address = intent.getStringExtra("link");
        title = intent.getStringExtra("title");
        WebView wv = (WebView) findViewById(R.id.webview);
        wv.loadUrl(address);
        final ProgressDialog pd = new ProgressDialog(WebViewActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("잠시만 기다려주세요");
        final long time = System.currentTimeMillis();
        WebSettings setting = wv.getSettings();
        setting.setJavaScriptEnabled(true); // 자바스크립트 사용
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        // 자바스크립트가 윈도우 오픈가능
        setting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // 플러그인 사용
        setting.setSupportZoom(true); // 줌 제스쳐
        setting.setBuiltInZoomControls(true); // 줌인/줌아웃
        wv.setWebViewClient(new WebViewClient(){ // 현재화면의 WebView 사이트로 이동
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pd.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                    }
                }, 1500);
            } // 웹페이지 로딩 시작할 때
            @Override
            public void onPageFinished(WebView view, String url) {
                // 웹페이지 로딩시간 측정
//                float ms = (System.currentTimeMillis() - time)/1000f;
//                Toast.makeText(getApplicationContext(), ms+"ms", Toast.LENGTH_SHORT).show();
            } // 웹 페이지로딩 끝났을 때
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            } // 에러가 났을 때
            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {

            } // 화면이 변경되었을 때
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            } // 새로운 URL 불러올 때
        });
    } // end of onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_webview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_kakao_send) {
            String sendMsg = "\"" + title + "\"" +
                    "\n- 웹 링크 : " +
                    "\n" + address;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "[ 모닝 메이트 ]\n");
            intent.putExtra(Intent.EXTRA_TEXT, sendMsg);
            intent.setPackage("com.kakao.talk");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
