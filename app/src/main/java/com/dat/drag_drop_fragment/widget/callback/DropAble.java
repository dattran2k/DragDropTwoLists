package com.dat.drag_drop_fragment.widget.callback;

import android.view.View;

public interface DropAble {
    void onHover(View viewDrop);

    void onLeaveHover(View viewDrop);

    void onDrop(View viewDrop);
}
