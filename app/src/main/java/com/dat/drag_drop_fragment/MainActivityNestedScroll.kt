package com.dat.drag_drop_fragment

import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.dat.drag_drop_fragment.databinding.ActivityMainRvBinding
import com.dat.drag_drop_fragment.fragment.TextFragment
import com.dat.drag_drop_fragment.widget.DragListener
import com.dat.drag_drop_fragment.widget.WidgetMaster.Companion.MARGIN_START_EDIT_MODE

class MainActivityNestedScroll : AppCompatActivity() {
    val dragInstance: DragListener = DragListener()
    private lateinit var binding: ActivityMainRvBinding
    val list = arrayListOf(1, 2, 3, 4, 5)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainRvBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val listFragment = list.map {
            TextFragment.newInstance(it.toString())
        }

        binding.widgetMaster.setUpFragment(listFragment, supportFragmentManager)
        ViewModel.isEnableEditModel.observe(this) {
            binding.widgetMaster.updateEditMode(it)
            binding.widgetMaster.updateLayoutParams<MarginLayoutParams> {
                marginStart = if (it == true) toPx(MARGIN_START_EDIT_MODE) else 0
            }
        }
        binding.btn.setOnClickListener {
            ViewModel.isEnableEditModel.value = ViewModel.isEnableEditModel.value != true

        }
    }
}