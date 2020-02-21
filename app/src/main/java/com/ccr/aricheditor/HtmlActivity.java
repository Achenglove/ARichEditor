package com.ccr.aricheditor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ccr.library.view.ARichEditor;

/**
 * @author Acheng
 * @Created on 2020/2/21.
 * @Email 345887272@qq.com
 * @Description 说明:
 */
public class HtmlActivity extends AppCompatActivity {
    String html = "https://ke.kchuangqi.com/mobileWX/app/index.html#/aericleText";

    ARichEditor richEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.html_layout);
        richEditor = findViewById(R.id.mARichEditor);
        //richEditor.setWebImageClick();
        //richEditor.setWebImageClick(richEditor);
        richEditor.loadUrl(html);

        richEditor.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(final WebView view, String url) {
                super.onPageFinished(view, url);
                richEditor.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        richEditor.setWebImageClick(view);
                    }
                },1000);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用当前WebView处理跳转
                view.loadUrl(url);
                // true表示此事件在此处被处理，不需要再广播
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.i("onReceivedError", error + "");
            }
        });

        richEditor.addJavascriptInterface(new JsCallJavaObj() {
            @JavascriptInterface
            @Override
            public void openImage(String url,String[] imgs) {
                Toast.makeText(HtmlActivity.this, url, Toast.LENGTH_SHORT).show();

                if (imgs != null && imgs.length >0){
                    for (int i = 0; i < imgs.length; i++) {
                        //imageUrls.add(imgs[i]);
                    }
                }

            }

            @JavascriptInterface
            @Override
            public void getImageUrl(String url) {
                //imageUrls.add(url);
//                LogUtil.INSTANCE.i("getImageUrl",url);
            }
        },"jsCallJavaObj");

    }
    /**
     * Js調用Java接口
     */
    private interface JsCallJavaObj{
        void openImage(String url,String[] imgs);

        void getImageUrl(String url);
    }
}
