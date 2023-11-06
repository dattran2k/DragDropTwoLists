package com.dat.drag_drop_fragment.widget.callback;

import android.view.View;

public interface OnDropWidgetMaster {
    void onDropAddView(View dropPlace, View viewDrop);
    void onHover(View dropPlace,View viewDrop);
    void onLeaveHover(View viewDrop);
}
