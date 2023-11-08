package com.dat.drag_drop_fragment.widget;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.dat.drag_drop_fragment.R;
import com.dat.drag_drop_fragment.ViewModel;
import com.dat.drag_drop_fragment.widget.callback.DropAble;


public class Widget extends FrameLayout implements DropAble, Observer<Boolean> {
    public WidgetMaster.WidgetType widgetType = WidgetMaster.WidgetType.TO;
    public int viewPortWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    public int viewPortHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    public static final String TAG = "Widget";
    private DragListener dragListener;
    private boolean isEditMode = false;
    private View wrapView;

    public Widget(Context context) {
        super(context);
        init();
    }

    public Widget(Context context, DragListener dragListener, WidgetMaster.WidgetType widgetType) {
        super(context);
        this.dragListener = dragListener;
        this.widgetType = widgetType;
        init();
    }

    public Widget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Widget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewModel.INSTANCE.isEnableEditModel().observeForever(this);
        updateEditMode();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ViewModel.INSTANCE.isEnableEditModel().removeObserver(this);
        setOnLongClick();
    }

    @Override
    public void onChanged(Boolean aBoolean) {
        Log.d(TAG, "onChanged: isEditMode :  " + aBoolean);
        boolean currentEditMode = isEditMode;
        this.isEditMode = aBoolean;
        if (currentEditMode != aBoolean)
            updateEditMode();

    }

    private void init() {
        wrapView = new View(getContext());
        wrapView.setElevation(10);
        wrapView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(wrapView);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_widget));
        setOnDragListener(dragListener);
    }

    @Override
    public void onHover(View viewDrop) {
        if (getChildCount() > 0) {
            if (viewDrop == this) {
                return;
            }
            if (viewDrop != null) {
                WidgetMaster widgetMaster = WidgetMaster.findWidgetMaster(this);
                if (widgetMaster != null) {
                    widgetMaster.onHover(this, viewDrop);
                }
            }
        }
    }

    @Override
    public void onLeaveHover(View viewDrop) {

    }

    @Override
    public void onDrop(View viewDrop) {
        WidgetMaster widgetMaster = WidgetMaster.findWidgetMaster(this);
        if (widgetMaster != null) {
            widgetMaster.onDropAddView(this, viewDrop);
        }
    }

    View fragment;

    public void addFragment(Fragment fragmentAdd, FragmentManager fragmentManager) {
        FragmentContainerView fragmentContainerView = new FragmentContainerView(getContext());
        fragmentContainerView.setId(View.generateViewId());
        addView(fragmentContainerView);
        FragmentTransaction t = fragmentManager.beginTransaction();
        t.replace(fragmentContainerView.getId(), fragmentAdd, fragmentAdd.getClass().getSimpleName());
        t.commit();
        fragment = fragmentContainerView;
    }

    public void updateEditMode() {
        setOnLongClick();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
        int currentViewPos = WidgetMaster.findWidgetMaster(this).indexOfChild(this);
        int marginStart = currentViewPos % 2 == 0 && isEditMode ? toPx(16) : 0;
        if (marginStart != layoutParams.getMarginStart()) {
            layoutParams.setMarginStart(marginStart);
        }

        int targetWidth;
        int targetHeight;
        if (widgetType == WidgetMaster.WidgetType.TO) {
            if (isEditMode) {
                targetWidth = viewPortWidth / 4;
                targetHeight = viewPortHeight / 2;
            } else {
                targetWidth = viewPortWidth / 2;
                targetHeight = viewPortHeight;
            }
        } else {
            targetWidth = viewPortWidth / 6;
            targetHeight = viewPortHeight / 3;
        }
        ValueAnimator widthAnimator = ObjectAnimator.ofInt(getWidth(), targetWidth);
        ValueAnimator heightAnimator = ObjectAnimator.ofInt(getHeight(), targetHeight);

        widthAnimator.addUpdateListener(animation -> {
            layoutParams.width = (int) animation.getAnimatedValue();
            setLayoutParams(layoutParams);
        });

        heightAnimator.addUpdateListener(animation -> {
            layoutParams.height = (int) animation.getAnimatedValue();
            setLayoutParams(layoutParams);
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(heightAnimator, widthAnimator);
        // animatorSet.playTogether(scaleXAnimator, scaleYAnimator); // Uncomment if you want to include scale animations

        animatorSet.setDuration(500); // Adjust the duration as needed (in milliseconds)

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Animation ended
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // Animation canceled
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // Animation repeated
            }
        });
        animatorSet.start();
    }

    public int toPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    public void removeFragment() {
        if (fragment != null)
            removeView(fragment);
    }

    public void destroy() {
        removeFragment();
        removeAllViews();
    }

    public void setOnLongClick() {
        if (wrapView != null) {
            wrapView.setOnLongClickListener(this.isEditMode ? v -> {
                boolean draggable = fragment != null && this.isEditMode;
                Log.e(TAG, "setOnLongClickListener: " + this);
                if (draggable) {
                    DragShadowBuilder ds = new DragShadowBuilder(this);
                    ClipData data = ClipData.newPlainText("", "");
                    fragment.setTag(this);
                    this.startDragAndDrop(data, ds, fragment, View.DRAG_FLAG_OPAQUE);
                }
                return draggable;
            } : null);
        }
    }


}
