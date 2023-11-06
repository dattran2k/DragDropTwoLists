package com.dat.drag_drop_fragment.widget.callback;

import android.view.View;

public interface OnEditWidgetStateChanged {
    void onDragging(View dragView);
    void onDropped(View dragView);
    void onDeleted(View dragView);
    void onScrollNext();
    void onScrollPrevious();
}
