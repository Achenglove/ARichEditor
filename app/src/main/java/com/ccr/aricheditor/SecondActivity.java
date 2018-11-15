package com.ccr.aricheditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.ccr.library.view.ACRichEditor;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private InputMethodManager imm;//软键盘管理器
    private RelativeLayout rl_layout_editor;
    private ImageButton action_undo, action_redo, action_list, action_image, action_add, action_setting;
    private ImageButton action_num, action_spot, action_bold, action_italic, action_strikethrough, action_blockquote, action_heading1, action_heading2, action_heading3, action_heading4;
    private ImageButton action_line, action_link;
    private ACRichEditor mEditor;
    private LinearLayout ll_layout_editor, ll_layout_font, ll_layout_add;//添加布局，字体布局

    private boolean flag1, flag2, flag3, flag4, flag5, flag6, flag7, flag8,flag9,flag10;
    boolean isclick = true;
    boolean isItalic;//是否斜体
    boolean isBold;//是否加粗
    boolean isStrikeThrough;//是否有删除线

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initViews();
        initEvents();
    }


    private void initViews() {
        rl_layout_editor = findViewById(R.id.rl_layout_editor);
        ll_layout_editor = findViewById(R.id.ll_layout_editor);
        ll_layout_font = findViewById(R.id.ll_layout_font);
        ll_layout_add = findViewById(R.id.ll_layout_add);
        mEditor = findViewById(R.id.editor);

        action_undo = findViewById(R.id.action_undo);
        action_redo = findViewById(R.id.action_redo);
        action_list = findViewById(R.id.action_list);
        action_image = findViewById(R.id.action_image);
        action_add = findViewById(R.id.action_add);
        action_setting = findViewById(R.id.action_setting);


        action_num = findViewById(R.id.action_num);
        action_spot = findViewById(R.id.action_spot);
        action_bold = findViewById(R.id.action_bold);
        action_italic = findViewById(R.id.action_italic);
        action_strikethrough = findViewById(R.id.action_strikethrough);
        action_blockquote = findViewById(R.id.action_blockquote);
        action_heading1 = findViewById(R.id.action_heading1);
        action_heading2 = findViewById(R.id.action_heading2);
        action_heading3 = findViewById(R.id.action_heading3);
        action_heading4 = findViewById(R.id.action_heading4);

        action_line = findViewById(R.id.action_line);
        action_link = findViewById(R.id.action_link);


        //富文本编辑初始化
        mEditor.setEditorFontSize(15);
        mEditor.setPadding(10, 10, 10, 50);
        mEditor.setPlaceholder("*项目详情：不得少于100字，说明项目的情况\\n如：项目介绍，筹款如何使用，自我介绍等");

    }

    private void initEvents() {

        action_undo.setOnClickListener(this);
        action_redo.setOnClickListener(this);
        action_list.setOnClickListener(this);
        action_image.setOnClickListener(this);
        action_add.setOnClickListener(this);
        action_setting.setOnClickListener(this);


        action_num.setOnClickListener(this);
        action_spot.setOnClickListener(this);
        action_bold.setOnClickListener(this);
        action_italic.setOnClickListener(this);
        action_strikethrough.setOnClickListener(this);
        action_blockquote.setOnClickListener(this);
        action_heading1.setOnClickListener(this);
        action_heading2.setOnClickListener(this);
        action_heading3.setOnClickListener(this);
        action_heading4.setOnClickListener(this);

        action_line.setOnClickListener(this);
        action_link.setOnClickListener(this);


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

        getFocus();
        //布局全局改变监听
        rl_layout_editor.getViewTreeObserver().addOnGlobalLayoutListener(onGroupCollapseListener);

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
                if (ll_layout_add.getVisibility() == View.VISIBLE) {
                    ll_layout_add.setVisibility(View.GONE);
                }

                if (ll_layout_font.getVisibility() == View.VISIBLE) {
                    ll_layout_font.setVisibility(View.GONE);
                }
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_undo:
                mEditor.undo();
                break;
            case R.id.action_redo:
                mEditor.redo();
                break;
            case R.id.action_list:
                if (ll_layout_font.getVisibility() == View.VISIBLE) {
                    ll_layout_font.setVisibility(View.GONE);
                } else {
                    if (ll_layout_add.getVisibility() == View.VISIBLE) {
                        ll_layout_add.setVisibility(View.GONE);
                    }
                    ll_layout_font.setVisibility(View.VISIBLE);
                    startAnimation(ll_layout_font);
                }
                break;
            case R.id.action_image://添加图片
                selectImg(REQUEST_CODE);
                break;
            case R.id.action_add://
                if (ll_layout_add.getVisibility() == View.VISIBLE) {
                    ll_layout_add.setVisibility(View.GONE);
                } else {
                    if (ll_layout_font.getVisibility() == View.VISIBLE) {
                        ll_layout_font.setVisibility(View.GONE);
                    }
                    ll_layout_add.setVisibility(View.VISIBLE);
                    startAnimation(ll_layout_add);
                }
                break;
            case R.id.action_setting://设置
                break;

            case R.id.action_bold://加粗
                if (flag1) {
                    action_bold.setImageResource(R.mipmap.photpartyedit_b);
                    flag1 = false;
                    isBold = false;
                } else {
                    action_bold.setImageResource(R.mipmap.photpartyedit_b2);
                    flag1 = true;
                    isBold = true;
                }
                mEditor.setBold();
                break;
            case R.id.action_italic://斜体
                if (flag2) {
                    action_italic.setImageResource(R.mipmap.photpartyedit_i);
                    flag2 = false;
                    isItalic = false;
                } else {
                    action_italic.setImageResource(R.mipmap.photpartyedit_i2);
                    flag2 = true;
                    isItalic = true;
                }
                mEditor.setItalic();
                break;
            case R.id.action_strikethrough://删除线
                if (flag3) {
                    action_strikethrough.setImageResource(R.mipmap.strikethrough_d);
                    flag3 = false;
                    isStrikeThrough = false;
                } else {
                    action_strikethrough.setImageResource(R.mipmap.strikethrough_l);
                    flag3 = true;
                    isStrikeThrough = true;
                }
                mEditor.setStrikeThrough();
                break;
            case R.id.action_blockquote:
                if (flag4) {
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent);
                    flag4 = false;
                    isclick = false;
                } else {
                    flag4 = true;
                    flag5 = false;
                    flag6 = false;
                    flag7 = false;
                    flag8 = false;
                    isclick = true;
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent2);
                    action_heading1.setImageResource(R.mipmap.h1_d);
                    action_heading2.setImageResource(R.mipmap.h2_d);
                    action_heading2.setImageResource(R.mipmap.h3_d);
                    action_heading4.setImageResource(R.mipmap.h4_d);
                }
                Log.d("BlockQuote", "isItalic:" + isItalic + "，isBold：" + isBold + "，isStrikeThrough:" + isStrikeThrough);
                mEditor.setBlockquote(isclick, isItalic, isBold, isStrikeThrough);
                break;
            case R.id.action_heading1:
                if (flag5) {
                    action_heading1.setImageResource(R.mipmap.h1_d);
                    flag5 = false;
                    isclick = false;

                    //使加粗灰显并去除效果
                    action_bold.setImageResource(R.mipmap.photpartyedit_b);
                    flag1 = false;
                    isBold = false;
                } else {
                    flag4 = false;
                    flag5 = true;
                    flag6 = false;
                    flag7 = false;
                    flag8 = false;
                    flag9 = false;
                    flag10=false;
                    isclick = true;
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent);
                    action_heading1.setImageResource(R.mipmap.h1_l);
                    action_heading2.setImageResource(R.mipmap.h2_d);
                    action_heading3.setImageResource(R.mipmap.h3_d);
                    action_heading4.setImageResource(R.mipmap.h4_d);
                }
                mEditor.setHeading(1, isclick, isItalic, isBold, isStrikeThrough);
                break;
            case R.id.action_heading2:
                if (flag6) {
                    action_heading2.setImageResource(R.mipmap.h2_d);
                    flag6 = false;
                    isclick = false;

                    //使加粗灰显并去除效果
                    action_bold.setImageResource(R.mipmap.photpartyedit_b);
                    flag1 = false;
                    isBold = false;
                } else {
                    flag4 = false;
                    flag5 = false;
                    flag6 = true;
                    flag7 = false;
                    flag8 = false;

                    isclick = true;
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent);
                    action_heading1.setImageResource(R.mipmap.h1_d);
                    action_heading2.setImageResource(R.mipmap.h2_l);
                    action_heading3.setImageResource(R.mipmap.h3_d);
                    action_heading4.setImageResource(R.mipmap.h4_d);
                }
                mEditor.setHeading(2, isclick, isItalic, isBold, isStrikeThrough);
                break;
            case R.id.action_heading3:
                if (flag7) {
                    action_heading3.setImageResource(R.mipmap.h3_d);
                    flag7 = false;
                    isclick = false;

                    //使加粗灰显并去除效果
                    action_bold.setImageResource(R.mipmap.photpartyedit_b);
                    flag1 = false;
                    isBold = false;
                } else {
                    flag4 = false;
                    flag5 = false;
                    flag6 = false;
                    flag7 = true;
                    flag8 = false;
                    isclick = true;
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent);
                    action_heading1.setImageResource(R.mipmap.h1_d);
                    action_heading2.setImageResource(R.mipmap.h2_d);
                    action_heading3.setImageResource(R.mipmap.h3_l);
                    action_heading4.setImageResource(R.mipmap.h4_d);
                }
                mEditor.setHeading(3, isclick, isItalic, isBold, isStrikeThrough);
                break;
            case R.id.action_heading4:
                if (flag8) {
                    action_heading4.setImageResource(R.mipmap.h4_d);
                    flag8 = false;
                    isclick = false;

                    //使加粗灰显并去除效果
                    action_bold.setImageResource(R.mipmap.photpartyedit_b);
                    flag1 = false;
                    isBold = false;
                } else {
                    flag4 = false;
                    flag5 = false;
                    flag6 = false;
                    flag7 = false;
                    flag8 = true;
                    isclick = true;
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent);
                    action_heading1.setImageResource(R.mipmap.h1_d);
                    action_heading2.setImageResource(R.mipmap.h2_d);
                    action_heading3.setImageResource(R.mipmap.h3_d);
                    action_heading4.setImageResource(R.mipmap.h4_l);
                }
                mEditor.setHeading(4, isclick, isItalic, isBold, isStrikeThrough);
                break;
            case R.id.action_num://数字
                if(flag9){
                    action_num.setImageResource(R.mipmap.photpartyedit_num_list);
                    flag9 = false;
                    isBold = false;
                }else{
                    action_num.setImageResource(R.mipmap.photpartyedit_num_list2);
                    action_spot.setImageResource(R.mipmap.photpartyedit_spot_list);
                    flag9 = true;
                    flag10=false;
                }
                mEditor.setNumbers();
                break;
            case R.id.action_spot://点
                if(flag10){
                    action_spot.setImageResource(R.mipmap.photpartyedit_spot_list);
                    flag10 = false;
                    isBold = false;
                }else{
                    action_spot.setImageResource(R.mipmap.photpartyedit_spot_list2);
                    action_num.setImageResource(R.mipmap.photpartyedit_num_list);
                    flag10 = true;
                    flag9=false;
                }
                mEditor.setBullets();
                break;
            case R.id.action_line:
                mEditor.insertHr();
                break;
            case R.id.action_link:
                showInsertLinkDialog();
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
                    Toast.makeText(SecondActivity.this, "请输入超链接地址", Toast.LENGTH_SHORT);
                } else if (TextUtils.isEmpty(linkTitle)) {
                    Toast.makeText(SecondActivity.this, "请输入超链接标题", Toast.LENGTH_SHORT);
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


    /**
     * 选着图片
     */
    private static final int REQUEST_CODE_PERMISSION_PHOTO_PICKER = 1;

    private void selectImg(int REQUEST_CODE) {
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, REQUEST_CODE);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_CODE_PERMISSION_PHOTO_PICKER) {
            Toast.makeText(this, "您拒绝了「图片选择」所需要的相关权限!", Toast.LENGTH_SHORT).show();
        }
    }


    public final static int REQUEST_CODE = 020;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String picturePath = c.getString(columnIndex);
            //插入图片
            mEditor.insertImage(picturePath, "图片");
            c.close();

        }
    }
    public void getFocus() {
        /**
         *获取点击出文本的标签类型
         */
        mEditor.setOnDecorationChangeListener(new ACRichEditor.OnDecorationStateListener() {
            @Override
            public void onStateChangeListener(String text, List<ACRichEditor.Type> types) {

                if (types.contains(ACRichEditor.Type.BOLD)) {
                    action_bold.setImageResource(R.mipmap.photpartyedit_b2);
                    flag1 = true;
                    isBold = true;
                } else {
                    action_bold.setImageResource(R.mipmap.photpartyedit_b);
                    flag1 = false;
                    isBold = false;
                }

                if (types.contains(ACRichEditor.Type.ITALIC)) {
                    action_italic.setImageResource(R.mipmap.photpartyedit_i2);
                    flag2 = true;
                    isItalic = true;
                } else {
                    action_italic.setImageResource(R.mipmap.photpartyedit_i);
                    flag2 = false;
                    isItalic = false;
                }

                if (types.contains(ACRichEditor.Type.STRIKETHROUGH)) {
                    action_strikethrough.setImageResource(R.mipmap.strikethrough_l);
                    flag3 = true;
                    isStrikeThrough = true;
                } else {
                    action_strikethrough.setImageResource(R.mipmap.strikethrough_d);
                    flag3 = false;
                    isStrikeThrough = false;
                }

                //块引用
                if (types.contains(ACRichEditor.Type.BLOCKQUOTE)) {
                    flag4 = true;
                    flag5 = false;
                    flag6 = false;
                    flag7 = false;
                    flag8 = false;
                    isclick = true;
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent2);
                    action_heading1.setImageResource(R.mipmap.h1_d);
                    action_heading2.setImageResource(R.mipmap.h2_d);
                    action_heading3.setImageResource(R.mipmap.h3_d);
                    action_heading4.setImageResource(R.mipmap.h4_d);
                } else {
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent);
                    flag4 = false;
                    isclick = false;
                }


                if (types.contains(ACRichEditor.Type.H1)) {
                    flag4 = false;
                    flag5 = true;
                    flag6 = false;
                    flag7 = false;
                    flag8 = false;

                    isclick = true;
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent);
                    action_heading1.setImageResource(R.mipmap.h1_l);
                    action_heading2.setImageResource(R.mipmap.h2_d);
                    action_heading3.setImageResource(R.mipmap.h3_d);
                    action_heading4.setImageResource(R.mipmap.h4_d);
                } else {
                    action_heading1.setImageResource(R.mipmap.h1_d);
                    flag5 = false;
                    isclick = false;
                }

                if (types.contains(ACRichEditor.Type.H2)) {
                    flag4 = false;
                    flag5 = false;
                    flag6 = true;
                    flag7 = false;
                    flag8 = false;

                    isclick = true;
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent);
                    action_heading1.setImageResource(R.mipmap.h1_d);
                    action_heading2.setImageResource(R.mipmap.h2_l);
                    action_heading3.setImageResource(R.mipmap.h3_d);
                    action_heading4.setImageResource(R.mipmap.h4_d);
                } else {
                    action_heading2.setImageResource(R.mipmap.h2_d);
                    flag6 = false;
                    isclick = false;
                }

                if (types.contains(ACRichEditor.Type.H3)) {
                    flag4 = false;
                    flag5 = false;
                    flag6 = false;
                    flag7 = true;
                    flag8 = false;
                    isclick = true;
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent);
                    action_heading1.setImageResource(R.mipmap.h1_d);
                    action_heading2.setImageResource(R.mipmap.h2_d);
                    action_heading3.setImageResource(R.mipmap.h3_l);
                    action_heading4.setImageResource(R.mipmap.h4_d);
                } else {
                    action_heading4.setImageResource(R.mipmap.h3_d);
                    flag7 = false;
                    isclick = false;
                }

                if (types.contains(ACRichEditor.Type.H4)) {
                    flag4 = false;
                    flag5 = false;
                    flag6 = false;
                    flag7 = false;
                    flag8 = true;
                    isclick = true;
                    action_blockquote.setImageResource(R.mipmap.photpartyedit_indent);
                    action_heading1.setImageResource(R.mipmap.h1_d);
                    action_heading2.setImageResource(R.mipmap.h2_d);
                    action_heading3.setImageResource(R.mipmap.h3_d);
                    action_heading4.setImageResource(R.mipmap.h4_l);
                } else {
                    action_heading4.setImageResource(R.mipmap.h4_d);
                    flag8 = false;
                    isclick = false;
                }
            }
        });

    }
}
