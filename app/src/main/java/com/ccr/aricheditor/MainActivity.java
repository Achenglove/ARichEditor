package com.ccr.aricheditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.ccr.aricheditor.view.ColorData;
import com.ccr.library.view.ARichEditor;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private InputMethodManager imm;//软键盘管理器
    private RelativeLayout rl_layout_editor;
    private RadioButton action_undo, action_redo, action_bold, action_italic, action_under, action_font, action_color, action_image;
    private ARichEditor mEditor;
    private LinearLayout ll_layout_color, ll_layout_font;//添加布局，字体布局
    private RadioGroup colorRadioGroup, fontRadioGroup;
//    private CustomSeekbar mCustomSeekbar;

    String string="<img src=\"/storage/emulated/0/Tencent/QQfile_recv/u=276711516,3135253545&amp;fm=173&amp;s=7E08762B6661710D.JPEG\"><br><br>";

    ImageView mimageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        initViews();

        initEvents();
    }

    private void initViews() {
//        mCustomSeekbar=(CustomSeekbar) findViewById(R.id.mCustomSeekbar);
        mimageView=(ImageView) findViewById(R.id.imageView);
        mimageView.setImageURI(Uri.parse("/storage/emulated/0/Tencent/QQfile_recv/u=276711516,3135253545&amp;fm=173&amp;s=7E08762B6661710D.JPEG"));
        //富文本编辑初始化
        mEditor = (ARichEditor) findViewById(R.id.editor);
        mEditor.setEditorFontSize(15);
        mEditor.setPadding(10, 10, 10, 100);
        mEditor.setPlaceholder("*项目详情：不得少于100字，说明项目的情况\\n如：项目介绍，筹款如何使用，自我介绍等");

        rl_layout_editor = (RelativeLayout) findViewById(R.id.rl_layout_editor);
        ll_layout_color = (LinearLayout) findViewById(R.id.ll_layout_color);
        ll_layout_font = (LinearLayout) findViewById(R.id.ll_layout_font);
        colorRadioGroup = (RadioGroup) findViewById(R.id.RadioGroup_color);
        fontRadioGroup = (RadioGroup) findViewById(R.id.RadioGroup_font);

        action_undo = (RadioButton) findViewById(R.id.action_undo);
        action_redo = (RadioButton) findViewById(R.id.action_redo);
        action_bold = (RadioButton) findViewById(R.id.action_bold);
        action_italic = (RadioButton) findViewById(R.id.action_italic);
        action_under = (RadioButton) findViewById(R.id.action_under);
        action_font = (RadioButton) findViewById(R.id.action_font);
        action_color = (RadioButton) findViewById(R.id.action_color);
        action_image = (RadioButton) findViewById(R.id.action_image);

        action_undo.setOnClickListener(this);
        action_redo.setOnClickListener(this);
        action_bold.setOnClickListener(this);
        action_italic.setOnClickListener(this);
        action_under.setOnClickListener(this);
        action_font.setOnClickListener(this);
        action_color.setOnClickListener(this);
        action_image.setOnClickListener(this);

        mEditor.setFontSize(ColorData.DEFAULTSIZE);
        mEditor.setTextColor(ColorData.DEFAULT);
        colorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setColor(checkedId);
            }
        });
        fontRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setFontSize(checkedId);
            }
        });

        mEditor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    rl_layout_editor.setVisibility(View.VISIBLE);
                } else {
                    imm.hideSoftInputFromWindow(mEditor.getWindowToken(), 0); //强制隐藏键盘
                    rl_layout_editor.setVisibility(View.INVISIBLE);
                }
            }
        });

        //布局全局改变监听
        rl_layout_editor.getViewTreeObserver().addOnGlobalLayoutListener(onGroupCollapseListener);




//        ArrayList<String> volume_sections=new ArrayList<>();
//        volume_sections.add("小号");
//        volume_sections.add("正常");
//        volume_sections.add("大号");
//        volume_sections.add("超大号");
//        mCustomSeekbar.initData(volume_sections);
//        mCustomSeekbar.setProgress(volume_sections.size());
//        mCustomSeekbar.setResponseOnTouch(new ResponseOnTouch() {
//            @Override
//            public void onTouchResponse(int position) {
//                Log.d("Acheng",position+"");
//            }
//        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String s= String.format("<img src=\"%s\" />", TextUtils.isEmpty(span.getUrl()) ?
//                        span.getFilePath() : span.getUrl());
                Log.d("Acheng",mEditor.getHtml());
            }
        });
    }

    private void initEvents() {


        /**
         *获取点击出文本的标签类型

         mEditor.setOnDecorationChangeListener(new RichEditor.OnDecorationStateListener() {
        @Override public void onStateChangeListener(String text, List<RichEditor.Type> types) {

        if (types.contains(RichEditor.Type.BOLD)) {
        ib_Bold.setImageResource(R.mipmap.bold_l);
        flag1 = true;
        isBold = true;
        } else {
        ib_Bold.setImageResource(R.mipmap.bold_d);
        flag1 = false;
        isBold = false;
        }

        if (types.contains(RichEditor.Type.ITALIC)) {
        ib_Italic.setImageResource(R.mipmap.italic_l);
        flag2 = true;
        isItalic = true;
        } else {
        ib_Italic.setImageResource(R.mipmap.italic_d);
        flag2 = false;
        isItalic = false;
        }

        if (types.contains(RichEditor.Type.STRIKETHROUGH)) {
        ib_StrikeThough.setImageResource(R.mipmap.strikethrough_l);
        flag3 = true;
        isStrikeThrough = true;
        } else {
        ib_StrikeThough.setImageResource(R.mipmap.strikethrough_d);
        flag3 = false;
        isStrikeThrough = false;
        }

        //块引用
        if (types.contains(RichEditor.Type.BLOCKQUOTE)) {
        flag4 = true;
        flag5 = false;
        flag6 = false;
        flag7 = false;
        flag8 = false;
        isclick = true;
        ib_BlockQuote.setImageResource(R.mipmap.blockquote_l);
        ib_H1.setImageResource(R.mipmap.h1_d);
        ib_H2.setImageResource(R.mipmap.h2_d);
        ib_H3.setImageResource(R.mipmap.h3_d);
        ib_H4.setImageResource(R.mipmap.h4_d);
        } else {
        ib_BlockQuote.setImageResource(R.mipmap.blockquote_d);
        flag4 = false;
        isclick = false;
        }


        if (types.contains(RichEditor.Type.H1)) {
        flag4 = false;
        flag5 = true;
        flag6 = false;
        flag7 = false;
        flag8 = false;

        isclick = true;
        ib_BlockQuote.setImageResource(R.mipmap.blockquote_d);
        ib_H1.setImageResource(R.mipmap.h1_l);
        ib_H2.setImageResource(R.mipmap.h2_d);
        ib_H3.setImageResource(R.mipmap.h3_d);
        ib_H4.setImageResource(R.mipmap.h4_d);
        } else {
        ib_H1.setImageResource(R.mipmap.h1_d);
        flag5 = false;
        isclick = false;
        }

        if (types.contains(RichEditor.Type.H2)) {
        flag4 = false;
        flag5 = false;
        flag6 = true;
        flag7 = false;
        flag8 = false;

        isclick = true;
        ib_BlockQuote.setImageResource(R.mipmap.blockquote_d);
        ib_H1.setImageResource(R.mipmap.h1_d);
        ib_H2.setImageResource(R.mipmap.h2_l);
        ib_H3.setImageResource(R.mipmap.h3_d);
        ib_H4.setImageResource(R.mipmap.h4_d);
        } else {
        ib_H2.setImageResource(R.mipmap.h2_d);
        flag6 = false;
        isclick = false;
        }

        if (types.contains(RichEditor.Type.H3)) {
        flag4 = false;
        flag5 = false;
        flag6 = false;
        flag7 = true;
        flag8 = false;
        isclick = true;
        ib_BlockQuote.setImageResource(R.mipmap.blockquote_d);
        ib_H1.setImageResource(R.mipmap.h1_d);
        ib_H2.setImageResource(R.mipmap.h2_d);
        ib_H3.setImageResource(R.mipmap.h3_l);
        ib_H4.setImageResource(R.mipmap.h4_d);
        } else {
        ib_H4.setImageResource(R.mipmap.h3_d);
        flag7 = false;
        isclick = false;
        }

        if (types.contains(RichEditor.Type.H4)) {
        flag4 = false;
        flag5 = false;
        flag6 = false;
        flag7 = false;
        flag8 = true;
        isclick = true;
        ib_BlockQuote.setImageResource(R.mipmap.blockquote_d);
        ib_H1.setImageResource(R.mipmap.h1_d);
        ib_H2.setImageResource(R.mipmap.h2_d);
        ib_H3.setImageResource(R.mipmap.h3_d);
        ib_H4.setImageResource(R.mipmap.h4_l);
        } else {
        ib_H4.setImageResource(R.mipmap.h4_d);
        flag8 = false;
        isclick = false;
        }
        }
        });
         */

        //布局全局改变监听
        rl_layout_editor.getViewTreeObserver().addOnGlobalLayoutListener(onGroupCollapseListener);


//        /**
//         * 插入链接
//         */
//        findViewById(R.id.action_link).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showInsertLinkDialog();
//            }
//        });


//        /**
//         * 插入分割线
//         */
//        findViewById(R.id.action_split).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mEditor.insertHr();
//            }
//        });


    }

    ViewTreeObserver.OnGlobalLayoutListener onGroupCollapseListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            int height = wm.getDefaultDisplay().getHeight();

            if (rl_layout_editor.getHeight() <= height * 0.75) {//当布局y轴坐标小于于屏幕高度的3/4，居于中部
                rl_layout_editor.setVisibility(View.VISIBLE);

            } else if (rl_layout_editor.getHeight() > height * 0.75) {
                rl_layout_editor.setVisibility(View.INVISIBLE);
                if (ll_layout_color.getVisibility() == View.VISIBLE) {
                    ll_layout_color.setVisibility(View.GONE);
                }

                if (ll_layout_font.getVisibility() == View.VISIBLE) {
                    ll_layout_font.setVisibility(View.GONE);
                }
            }
        }
    };

    /**
     * 颜色区域
     */
    int colorInt;
    private void setColor(int checkedIdColor) {
        if (checkedIdColor == R.id.mrb_font_option_black) {
            colorInt = ColorData.BLACK;
        } else if (checkedIdColor == R.id.mrb_font_option_gray) {
            colorInt = ColorData.GRAY;
        } else if (checkedIdColor == R.id.mrb_font_option_white) {
            colorInt = ColorData.LGRAY;
        } else if (checkedIdColor == R.id.mrb_font_option_blackgray) {
            colorInt = ColorData.DEEPBLUE;
        } else if (checkedIdColor == R.id.mrb_font_option_blue) {
            colorInt = ColorData.BLUE;
        } else if (checkedIdColor == R.id.mrb_font_option_green) {
            colorInt = ColorData.GREEN;
        } else if (checkedIdColor == R.id.mrb_font_option_red) {
            colorInt = ColorData.RED;
        } else if (checkedIdColor == R.id.mrb_font_option_violet) {
            colorInt = ColorData.PURPLE;
        } else if (checkedIdColor == R.id.mrb_font_option_yellow) {
            colorInt = ColorData.YELLOW;
        } else {
            colorInt = ColorData.DEFAULT;
        }
        ll_layout_color.setVisibility(View.GONE);
        mEditor.setTextColor(colorInt);
    }

    /**
     * 字体区域
     */
    int fontSize;
    private void setFontSize(int checkedIdColor) {
        if (checkedIdColor == R.id.oversize_size) {
            fontSize = ColorData.OVERSIZE;
        } else if (checkedIdColor == R.id.large_size) {
            fontSize = ColorData.LARGESIZE;
        } else if (checkedIdColor == R.id.default_size) {
            fontSize = ColorData.DEFAULTSIZE;
        } else if (checkedIdColor == R.id.small_size) {
            fontSize = ColorData.SMALLSIZE;
        }
        ll_layout_color.setVisibility(View.GONE);
        mEditor.setFontSize(fontSize);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.action_undo://撤回
                mEditor.undo();
                break;
            case R.id.action_redo://复原
                mEditor.redo();
                break;
            case R.id.action_bold:
                mEditor.setBold();
                break;
            case R.id.action_italic: //斜体
                mEditor.setItalic();
                break;
            case R.id.action_under://下划线
                mEditor.setUnderline();
                break;
            //删除线
//            case R.id.action_strikethrough:
//
//                mEditor.setStrikeThrough();
//                break;
            //块引用
            case R.id.action_font: //字体
                if (ll_layout_font.getVisibility() == View.VISIBLE) {
                    ll_layout_font.setVisibility(View.GONE);
                } else {
                    if (ll_layout_color.getVisibility() == View.VISIBLE) {
                        ll_layout_color.setVisibility(View.GONE);
                    }
                    ll_layout_font.setVisibility(View.VISIBLE);
                    startAnimation(ll_layout_font);
                }
                break;
            case R.id.action_color://颜色
                if (ll_layout_color.getVisibility() == View.VISIBLE) {
                    ll_layout_color.setVisibility(View.GONE);
                } else {
                    if (ll_layout_font.getVisibility() == View.VISIBLE) {
                        ll_layout_font.setVisibility(View.GONE);
                    }
                    ll_layout_color.setVisibility(View.VISIBLE);
                    startAnimation(ll_layout_color);
                }
                break;
            case R.id.action_image:
                Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(picture, RICH_IMAGE_CODE);
                break;

        }

    }

    // 执行动画效果
    public void startAnimation(View mView) {

        AlphaAnimation aa = new AlphaAnimation(0.4f, 1.0f); // 0完全透明 1 完全不透明
        // 以(0%,0.5%)为基准点，从0.5缩放至1
        ScaleAnimation sa = new ScaleAnimation(0.5f, 1, 0.5f, 1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // 添加至动画集合
        AnimationSet as = new AnimationSet(false);
        as.addAnimation(aa);
        as.addAnimation(sa);
        as.setDuration(500);
        // 执行动画
        mView.startAnimation(as);
    }


    private AlertDialog linkDialog;

    /**
     * 插入链接Dialog
     */
    private void showInsertLinkDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        linkDialog = adb.create();

        View view = getLayoutInflater().inflate(R.layout.dialog_insertlink, null);

        final EditText et_link_address = (EditText) view.findViewById(R.id.et_link_address);
        final EditText et_link_title = (EditText) view.findViewById(R.id.et_link_title);

        Editable etext = et_link_address.getText();
        Selection.setSelection(etext, etext.length());

        //点击确实的监听
        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String linkAddress = et_link_address.getText().toString();
                String linkTitle = et_link_title.getText().toString();

                if (linkAddress.endsWith("http://") || TextUtils.isEmpty(linkAddress)) {
                    Toast.makeText(MainActivity.this, "请输入超链接地址", Toast.LENGTH_SHORT);
                } else if (TextUtils.isEmpty(linkTitle)) {
                    Toast.makeText(MainActivity.this, "请输入超链接标题", Toast.LENGTH_SHORT);
                } else {
                    mEditor.insertLink(linkAddress, linkTitle);
                    linkDialog.dismiss();
                }
            }
        });
        //点击取消的监听
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkDialog.dismiss();
            }
        });
        linkDialog.setCancelable(false);
        linkDialog.setView(view, 0, 0, 0, 0); // 设置 view
        linkDialog.show();
    }

    private ArrayList<String> richpImage = null; //新增加的存储图片的集合
    public final static int RICH_IMAGE_CODE = 0x33;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RICH_IMAGE_CODE && resultCode == Activity.RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String picturePath = c.getString(columnIndex);
            Log.i("dgs", "picturePath----" + picturePath);
            //插入图片
            mEditor.insertImage(picturePath, "图片");
            c.close();
            //获取图片并显示

        }
    }
}

