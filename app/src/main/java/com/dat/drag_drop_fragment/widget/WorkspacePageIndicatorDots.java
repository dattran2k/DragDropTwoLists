package com.dat.drag_drop_fragment.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

public class WorkspacePageIndicatorDots extends PageIndicatorDots {


    public WorkspacePageIndicatorDots(Context context) {
        this(context, null);
    }

    public WorkspacePageIndicatorDots(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WorkspacePageIndicatorDots(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mInActiveColor = Color.parseColor("#5E5E5E");
        mActiveColor = Color.parseColor("#BDBDBD");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mNumPages < 2) {
            return;
        }
        super.onDraw(canvas);
    }
}
