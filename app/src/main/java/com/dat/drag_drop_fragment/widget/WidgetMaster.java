package com.dat.drag_drop_fragment.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dat.drag_drop_fragment.R;
import com.dat.drag_drop_fragment.widget.callback.OnDropWidgetMaster;

import java.util.ArrayList;
import java.util.List;

public class WidgetMaster extends LinearLayout implements OnDropWidgetMaster {
    public enum WidgetType {
        FROM,
        TO
    }

    public static final String TAG = "WidgetMaster";
    public static final int MARGIN_START_EDIT_MODE = 40;

    public static final int KEY_WIDGET = R.id.key_widget;
    public int viewPortWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    public int viewPortHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    public boolean isEditMode = false;
    public WidgetType widgetType = WidgetType.TO;

    public static List<View> getAllViews(ViewGroup viewGroup) {
        List<View> views = new ArrayList<>();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            views.add(viewGroup.getChildAt(i));
        }
        return views;
    }

    public static WidgetMaster findWidgetMaster(View view) {
        if (view instanceof WidgetMaster) {
            return (WidgetMaster) view;
        }
        if (view == null || !(view.getParent() instanceof View)) {
            return null;
        }
        View parentView = (View) view.getParent();
        return findWidgetMaster(parentView);
    }

    public WidgetMaster(Context context) {
        super(context);
        init();
    }

    public WidgetMaster(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WidgetMaster(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addTransition();
    }

    public final DragListener dragInstance = new DragListener();

    public void setUpFragment(List<Fragment> listWidget, FragmentManager fragmentManager, WidgetType widgetType) {
        this.widgetType = widgetType;
        setUpFragment(listWidget, fragmentManager);
    }

    public void setUpFragment(List<Fragment> listWidget, FragmentManager fragmentManager) {
        for (Widget w : getAllWidget()) {
            w.destroy();
        }
        removeAllViews();
        for (int index = 0; index < listWidget.size(); index++) {
            Fragment fragment = listWidget.get(index);
            Widget widget;
            widget = new Widget(getContext(), dragInstance, widgetType);
            widget.addFragment(fragment, fragmentManager);
            widget.setTag(KEY_WIDGET, listWidget.get(index));
            addView(widget);
        }

    }

    private List<Widget> getAllWidget() {
        List<Widget> result = new ArrayList<>();
        for (View view : getAllViews(this)) {
            if (view instanceof Widget) {
                result.add((Widget) view);
            }
        }
        return result;
    }

    private void onDropReplace(View dropPlace, View viewDrop) {
        if (dropPlace == viewDrop) {
            return;
        }
        List<View> allView = getAllViews(this);
        int dropPlacePosition = allView.indexOf(dropPlace);
        int viewDropPosition = allView.indexOf(viewDrop);
        if (viewDropPosition == -1 && viewDrop instanceof Widget) {
            ((Widget) viewDrop).updateEditMode();
        }
//        removeTransition();
        removeSelf(dropPlace);
        removeSelf(viewDrop);
//        addTransition();
        addView(viewDrop, dropPlacePosition);
        Log.e(TAG, "onDropReplace: " + dropPlacePosition);
    }

    private void addTransition() {
    }

    private void removeTransition() {
        setLayoutTransition(null);
    }

    @Override
    public void onDropAddView(View dropPlace, View viewDrop) {
        Log.e(TAG, "onDropAddView: ");
        onDropReplace(dropPlace, viewDrop);
    }


    @Override
    public void onHover(View dropPlace, View viewDrop) {
        if (dropPlace == viewDrop || widgetType == WidgetType.FROM) {
            return;
        }
        List<View> allView = getAllViews(this);
        int dropPosition = allView.indexOf(viewDrop);
        int dropPlacePosition = allView.indexOf(dropPlace);
        if (dropPosition == -1 && viewDrop instanceof Widget) {
            ((Widget) viewDrop).widgetType = widgetType;
            ((Widget) viewDrop).updateEditMode();
        }
        View temp = new View(getContext());
        addView(temp, dropPlacePosition);
        removeSelf(dropPlace);
        removeSelf(viewDrop);
        addView(dropPlace, dropPosition);
        addView(viewDrop, dropPlacePosition);
        removeSelf(temp);
        viewDrop.setTag(null);
    }

    @Override
    public void onLeaveHover(View viewDrop) {

    }

    public int toPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    public static void removeSelf(View view) {
        if (view == null) {
            return;
        }

        if (view.getParent() instanceof ViewGroup) {
            ViewGroup parentView = (ViewGroup) view.getParent();
            parentView.removeView(view);
        }
    }

}

