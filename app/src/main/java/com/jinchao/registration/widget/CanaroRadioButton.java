package com.jinchao.registration.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.jinchao.registration.config.Constants;

/**
 * Created by user on 2017/3/19.
 */

public class CanaroRadioButton extends android.support.v7.widget.AppCompatRadioButton {
    public CanaroRadioButton(Context context) {
        this(context,null);
    }

    public CanaroRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public CanaroRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.CANARO_EXTRA_BOLD_PATH));
    }


}
