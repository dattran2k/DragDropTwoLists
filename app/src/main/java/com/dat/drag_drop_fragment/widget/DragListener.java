package com.dat.drag_drop_fragment.widget;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import com.dat.drag_drop_fragment.R;
import com.dat.drag_drop_fragment.widget.callback.DropAble;
import com.dat.drag_drop_fragment.widget.callback.OnEditWidgetStateChanged;


public class DragListener implements View.OnDragListener {
    private static final String TAG = "DragListener";

    private OnEditWidgetStateChanged onEditWidgetStateChanged;

    public void setOnEditWidgetStateChanged(OnEditWidgetStateChanged onEditWidgetStateChanged) {
        this.onEditWidgetStateChanged = onEditWidgetStateChanged;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        View dragView = event.getLocalState() instanceof View ? (View) event.getLocalState() : null;
        View widget = dragView != null ? (dragView.getTag() instanceof View ? (View) dragView.getTag() : null) : null;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (dragView != null) {
                    dragView.setVisibility(View.INVISIBLE);
                }
                if (onEditWidgetStateChanged != null)
                    onEditWidgetStateChanged.onDragging(dragView);
                break;

            case DragEvent.ACTION_DRAG_ENTERED:
                Log.e(TAG, "onDrag: " + stringByAction(event.getAction()));
                if (v instanceof ScrollTrigger) {
                    if (v.getId() == R.id.scroll_trigger_previous)
                        onEditWidgetStateChanged.onScrollPrevious();
                    else if ((v.getId() == R.id.scroll_trigger_next))
                        onEditWidgetStateChanged.onScrollNext();
                } else if (v instanceof DropAble) {
                    ((DropAble) v).onHover(widget);

                }
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                if (v instanceof DropAble) {
                    ((DropAble) v).onLeaveHover(widget);
                }
                break;

            case DragEvent.ACTION_DRAG_ENDED:
                if (dragView != null) {
                    dragView.post(() -> dragView.setVisibility(View.VISIBLE));
                }
                if (v instanceof Widget) {
                    ((Widget) v).onLeaveHover(widget);
                }
                if (onEditWidgetStateChanged != null) {
                    onEditWidgetStateChanged.onDropped(dragView);
                }

                break;

            case DragEvent.ACTION_DROP:
                if (onEditWidgetStateChanged != null)
                    onEditWidgetStateChanged.onDropped(dragView);
                if (dragView != null) {
                    dragView.setVisibility(View.VISIBLE);
                }
                break;
        }

        return true;
    }


    private String stringByAction(int action) {
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                return "ACTION_DRAG_STARTED";
            case DragEvent.ACTION_DRAG_EXITED:
                return "ACTION_DRAG_EXITED";
            case DragEvent.ACTION_DRAG_ENTERED:
                return "ACTION_DRAG_ENTERED";
            case DragEvent.ACTION_DROP:
                return "ACTION_DROP";
            case DragEvent.ACTION_DRAG_ENDED:
                return "ACTION_DRAG_ENDED";
            case DragEvent.ACTION_DRAG_LOCATION:
                return "ACTION_DRAG_LOCATION";
            default:
                return String.valueOf(action);
        }
    }
}
