package com.jinchao.registration.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.jinchao.registration.config.Constants;


/**
 * Created by Dmytro Denysenko on 5/6/15.
 */
public class CanaroTextView extends android.support.v7.widget.AppCompatTextView{
    public static Typeface canaroExtraBold;
    public CanaroTextView(Context context) {
        this(context, null);
    }

    public CanaroTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanaroTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.CANARO_EXTRA_BOLD_PATH));
    }

}
