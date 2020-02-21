package com.ccr.library.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ccr.library.util.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @Created on 2018/5/28.
 * @autthor Acheng
 * @Email 345887272@qq.com
 * @Description
 */

public class ARichEditor extends WebView {

    public enum Type {
        BOLD,
        ITALIC,
        SUBSCRIPT,
        SUPERSCRIPT,
        STRIKETHROUGH,
        UNDERLINE,
        BLOCKQUOTE,
        H1,
        H2,
        H3,
        H4,
        H5,
        H6
    }

    public interface OnTextChangeListener {

        void onTextChange(String text);
    }

    public interface OnDecorationStateListener {

        void onStateChangeListener(String text, List<Type> types);
    }

    public interface AfterInitialLoadListener {

        void onAfterInitialLoad(boolean isReady);
    }

    private static final String SETUP_HTML = "file:///android_asset/editor.html";
    private static final String CALLBACK_SCHEME = "re-callback://";
    private static final String STATE_SCHEME = "re-state://";
    private boolean isReady = false;
    private String mContents;
    private OnTextChangeListener mTextChangeListener;
    private OnDecorationStateListener mDecorationStateListener;
    private AfterInitialLoadListener mLoadListener;
    private OnScrollChangedCallback mOnScrollChangedCallback;

    public ARichEditor(Context context) {
        this(context, null);
    }

    public ARichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public ARichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(createWebviewClient());
        loadUrl(SETUP_HTML);

        applyAttributes(context, attrs);
    }

    protected EditorWebViewClient createWebviewClient() {
        return new EditorWebViewClient();
    }

    public void setOnTextChangeListener(OnTextChangeListener listener) {
        mTextChangeListener = listener;
    }

    public void setOnDecorationChangeListener(OnDecorationStateListener listener) {
        mDecorationStateListener = listener;
    }

    public void setOnInitialLoadListener(AfterInitialLoadListener listener) {
        mLoadListener = listener;
    }

    private void callback(String text) {
        mContents = text.replaceFirst(CALLBACK_SCHEME, "");
        if (mTextChangeListener != null) {
            mTextChangeListener.onTextChange(mContents);
        }
        return;
    }


    /**
     * WebView的滚动事件
     *
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l - oldl, t - oldt);
        }

    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(
            final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }


    /**
     * Impliment in the activity/fragment/view that you want to listen to the webview
     */
    public interface OnScrollChangedCallback {
        void onScroll(int dx, int dy);
    }


    private void stateCheck(String text) {

        if (!text.contains("@_@")) {
            String state = text.replaceFirst(STATE_SCHEME, "").toUpperCase(Locale.ENGLISH);
            List<Type> types = new ArrayList<>();
            for (Type type : Type.values()) {
                if (TextUtils.indexOf(state, type.name()) != -1) {
                    types.add(type);
                }
            }

            if (mDecorationStateListener != null) {
                mDecorationStateListener.onStateChangeListener(state, types);
            }
            return;
        }

        String state = text.replaceFirst(STATE_SCHEME, "").split("@_@")[0].toUpperCase(Locale.ENGLISH);
        List<Type> types = new ArrayList<>();
        for (Type type : Type.values()) {
            if (TextUtils.indexOf(state, type.name()) != -1) {
                types.add(type);
            }
        }

        if (mDecorationStateListener != null) {
            mDecorationStateListener.onStateChangeListener(state, types);
        }

        if (text.replaceFirst(STATE_SCHEME, "").split("@_@").length > 1) {
            mContents = text.replaceFirst(STATE_SCHEME, "").split("@_@")[1];
            if (mTextChangeListener != null) {
                mTextChangeListener.onTextChange(mContents);
            }
        }
    }

    private void applyAttributes(Context context, AttributeSet attrs) {
        final int[] attrsArray = new int[]{
                android.R.attr.gravity
        };
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);

        int gravity = ta.getInt(0, NO_ID);
        switch (gravity) {
            case Gravity.LEFT:
                exec("javascript:RE.setTextAlign(\"left\")");
                break;
            case Gravity.RIGHT:
                exec("javascript:RE.setTextAlign(\"right\")");
                break;
            case Gravity.TOP:
                exec("javascript:RE.setVerticalAlign(\"top\")");
                break;
            case Gravity.BOTTOM:
                exec("javascript:RE.setVerticalAlign(\"bottom\")");
                break;
            case Gravity.CENTER_VERTICAL:
                exec("javascript:RE.setVerticalAlign(\"middle\")");
                break;
            case Gravity.CENTER_HORIZONTAL:
                exec("javascript:RE.setTextAlign(\"center\")");
                break;
            case Gravity.CENTER:
                exec("javascript:RE.setVerticalAlign(\"middle\")");
                exec("javascript:RE.setTextAlign(\"center\")");
                break;
        }

        ta.recycle();
    }

    /**
     * setText
     *
     * @param contents
     */
    public void setHtml(String contents) {
        if (contents == null) {
            contents = "";
        }
        try {
            exec("javascript:RE.setHtml('" + URLEncoder.encode(contents, "UTF-8") + "');");
        } catch (UnsupportedEncodingException e) {
            // No handling
        }
        mContents = contents;
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);

    }


    /**
     * getText
     *
     * @return
     */
    public String getHtml() {
        return mContents;
    }

    public void setEditorFontColor(int color) {
        String hex = convertHexColorString(color);
        exec("javascript:RE.setBaseTextColor('" + hex + "');");
    }

    public void setEditorFontSize(int px) {
        exec("javascript:RE.setBaseFontSize('" + px + "px');");
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        exec("javascript:RE.setPadding('" + left + "px', '" + top + "px', '" + right + "px', '" + bottom
                + "px');");
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        // still not support RTL.
        setPadding(start, top, end, bottom);
    }

    public void setEditorBackgroundColor(int color) {
        setBackgroundColor(color);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    @Override
    public void setBackgroundResource(int resid) {
        Bitmap bitmap = Utils.decodeResource(getContext(), resid);
        String base64 = Utils.toBase64(bitmap);
        bitmap.recycle();

        exec("javascript:RE.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    @Override
    public void setBackground(Drawable background) {
        Bitmap bitmap = Utils.toBitmap(background);
        String base64 = Utils.toBase64(bitmap);
        bitmap.recycle();

        exec("javascript:RE.setBackgroundImage('url(data:image/png;base64," + base64 + ")');");
    }

    public void setBackground(String url) {
        exec("javascript:RE.setBackgroundImage('url(" + url + ")');");
    }

    public void setEditorWidth(int px) {
        exec("javascript:RE.setWidth('" + px + "px');");
    }

    public void setEditorHeight(int px) {
        exec("javascript:RE.setHeight('" + px + "px');");
    }

    public void setPlaceholder(String placeholder) {
        exec("javascript:RE.setPlaceholder('" + placeholder + "');");
    }

    //加载css
    public void loadCSS(String cssFile) {
        String jsCSSImport = "(function() {" +
                "    var head  = document.getElementsByTagName(\"head\")[0];" +
                "    var link  = document.createElement(\"link\");" +
                "    link.rel  = \"stylesheet\";" +
                "    link.type = \"text/css\";" +
                "    link.href = \"" + cssFile + "\";" +
                "    link.media = \"all\";" +
                "    head.appendChild(link);" +
                "}) ();";
        exec("javascript:" + jsCSSImport + "");
    }

    public void setWebImageClick(WebView view) {
        String jsCode = "javascript:(function(){" +
                "var imgs=document.getElementsByTagName(\"img\");" +
                " var array=new Array(); " +
                " for(var j=0;j<imgs.length;j++){ array[j]=imgs[j].src; }" +
                "for(var i=0;i<imgs.length;i++){" +

                "imgs[i].onclick=function(){" +
                "window.jsCallJavaObj.openImage(this.src,array);" +
                "}}})()";
        view.loadUrl(jsCode);
    }

    public void setWebImageClick() {
        String jsCode = "javascript:(function(){" +
                "var imgs=document.getElementsByTagName(\"img\");" +
                " var array=new Array(); " +
                " for(var j=0;j<imgs.length;j++){ array[j]=imgs[j].src; }" +
                "for(var i=0;i<imgs.length;i++){" +

                "imgs[i].onclick=function(){" +
                "window.jsCallJavaObj.openImage(this.src,array);" +
                "}}})()";
        exec("javascript:" + jsCode + "");
    }

    //撤销
    public void undo() {
        exec("javascript:RE.undo();");
    }

    //反撤销
    public void redo() {
        exec("javascript:RE.redo();");
    }

    //字体加粗
    public void setBold() {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setBold();");
    }

    //字体斜体
    public void setItalic() {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setItalic();");
    }

    //字体下标
    public void setSubscript() {
        exec("javascript:RE.setSubscript();");
    }

    //字体上标
    public void setSuperscript() {
        exec("javascript:RE.setSuperscript();");
    }

    //字体删除线
    public void setStrikeThrough() {
        exec("javascript:RE.setStrikeThrough();");
    }

    //下划线
    public void setUnderline() {
        exec("javascript:RE.setUnderline();");
    }

    //字体颜色
    public void setTextColor(int color) {
        exec("javascript:RE.prepareInsert();");

        String hex = convertHexColorString(color);
        exec("javascript:RE.setTextColor('" + hex + "');");
    }

    //字体背景颜色
    public void setTextBackgroundColor(int color) {
        exec("javascript:RE.prepareInsert();");

        String hex = convertHexColorString(color);
        exec("javascript:RE.setTextBackgroundColor('" + hex + "');");
    }

    //设置字体大小
    public void setFontSize(int fontSize) {
        if (fontSize > 7 || fontSize < 1) {
            Log.e("RichEditor", "Font size should have a value between 1-7");
        }
        exec("javascript:RE.setFontSize('" + fontSize + "');");
    }

    //移除格式
    public void removeFormat() {
        exec("javascript:RE.removeFormat();");
    }

    //设置字号大小
    public void setHeading(int heading) {
        exec("javascript:RE.setHeading('" + heading + "');");
    }

    public void setHeading(int heading, boolean b, boolean isItalic, boolean isBold, boolean isStrikeThrough) {
        exec("javascript:RE.prepareInsert();");
//        if (!b) {
//            if (isItalic)
//                exec("javascript:RE.setItalic();");
//            if (isBold)
//                exec("javascript:RE.setBold();");
//            if (isStrikeThrough)
//                exec("javascript:RE.setStrikeThrough();");
//        }
        exec("javascript:RE.setHeading('" + heading + "'," + b + ");");
    }

    //字体缩进
    public void setIndent() {
        exec("javascript:RE.setIndent();");
    }

    //字体缩进还原
    public void setOutdent() {
        exec("javascript:RE.setOutdent();");
    }

    //左对齐
    public void setAlignLeft() {
        exec("javascript:RE.setJustifyLeft();");
    }

    //居中
    public void setAlignCenter() {
        exec("javascript:RE.setJustifyCenter();");
    }

    //右对齐
    public void setAlignRight() {
        exec("javascript:RE.setJustifyRight();");
    }

    //块引用
    public void setBlockquote() {
        exec("javascript:RE.setBlockquote();");
    }

    public void setBlockquote(boolean b, boolean isItalic, boolean isBold, boolean isStrikeThrough) {
        exec("javascript:RE.prepareInsert();");
//        if (!b) {
//            if (isItalic)
//                exec("javascript:RE.setItalic();");
//            if (isBold)
//                exec("javascript:RE.setBold();");
//            if (isStrikeThrough)
//                exec("javascript:RE.setStrikeThrough();");
//        }
        exec("javascript:RE.setBlockquote(" + b + ");");
    }

    //设置点点
    public void setBullets() {
        exec("javascript:RE.setBullets();");
    }

    //设置点点
    public void setBullets(boolean b) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setBullets(" + b + ");");
        Log.d("Acheng", b + ":Bullets");
    }

    //设置数字
    public void setNumbers() {
        exec("javascript:RE.setNumbers();");
    }

    //设置数字
    public void setNumbers(boolean b) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setNumbers(" + b + ");");
        Log.d("Acheng", b + ":Numbers");
    }

    //添加图片图片
    public void insertImage(String url, String alt) {
        //exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertImage('" + url + "', '" + alt + "');");
    }

    // 插入分割线
    public void insertHr() {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertHr();");
    }


    //添加链接
    public void insertLink(String href, String title) {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.insertLink('" + href + "', '" + title + "');");
    }

    public void insertTodo() {
        exec("javascript:RE.prepareInsert();");
        exec("javascript:RE.setTodo('" + Utils.getCurrentTime() + "');");
    }

    //获取焦点
    public void focusEditor() {
        requestFocus();
        exec("javascript:RE.focus();");
    }

    //清除焦点
    public void clearFocusEditor() {
        exec("javascript:RE.blurFocus();");
    }


    private String convertHexColorString(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    protected void exec(final String trigger) {
        if (isReady) {
            load(trigger);
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    exec(trigger);
                }
            }, 100);
        }
    }

    private void load(String trigger) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(trigger, null);
        } else {
            loadUrl(trigger);
        }
    }

    protected class EditorWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            isReady = url.equalsIgnoreCase(SETUP_HTML);
            if (mLoadListener != null) {
                mLoadListener.onAfterInitialLoad(isReady);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String decode;
            try {
                decode = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // No handling
                return false;
            }

            if (TextUtils.indexOf(url, CALLBACK_SCHEME) == 0) {
                callback(decode);
                return true;
            } else if (TextUtils.indexOf(url, STATE_SCHEME) == 0) {
                stateCheck(decode);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
