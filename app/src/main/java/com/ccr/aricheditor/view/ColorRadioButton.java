package com.ccr.aricheditor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.ccr.aricheditor.R;


/**
 * @Created on 2018/5/25.
 * @autthor Acheng
 * @Email 345887272@qq.com
 * @Description
 */

public class ColorRadioButton extends RadioButton {
    private static final String TAG = "ColorRadioButton";
    private int unCheckedResId;
    private int checkedResId;

    public ColorRadioButton(Context context) {
        this(context, null);
    }

    public ColorRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorRadioButton, defStyleAttr, 0);
        unCheckedResId = ta.getResourceId(R.styleable.ColorRadioButton_unCheckedResId, 0);
        checkedResId = ta.getResourceId(R.styleable.ColorRadioButton_checkedResId, 0);
        updateView();
        this.setClickable(true);
        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e(TAG, "onCheckedChanged: " + isChecked);
                updateView();
            }
        });

    }

    private void updateView() {
        if (isChecked()) {
            this.setBackgroundResource(checkedResId);
        } else {
            this.setBackgroundResource(unCheckedResId);
        }
    }

    public void setIconResId(int unCheckedResId, int checkedResId) {
        this.unCheckedResId = unCheckedResId;
        this.checkedResId = checkedResId;
        updateView();
    }
}
