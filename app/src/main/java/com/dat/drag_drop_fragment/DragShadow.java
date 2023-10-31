package com.dat.drag_drop_fragment;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

public class DragShadow extends View.DragShadowBuilder {
    View greyBox;

    public DragShadow(View view) {
        super(view);
        greyBox = view;
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        greyBox.draw(canvas);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        View v = getView();
        int height = v.getHeight();
        int width = v.getWidth();
        shadowSize.set(width, height);
        shadowTouchPoint.set((width / 2), (height / 2));
    }
}
