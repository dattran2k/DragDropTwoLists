package com.dat.drag_drop_fragment

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dat.drag_drop_fragment.databinding.ActivityMainRvBinding
import com.dat.drag_drop_fragment.fragment.MyAdapter
import com.dat.drag_drop_fragment.fragment.PreCachingLayoutManager
import com.dat.drag_drop_fragment.widget.DragListener

class MainActivityNestedScroll : AppCompatActivity() {
    val dragInstance: DragListener = DragListener()
    private lateinit var binding: ActivityMainRvBinding
    val list = arrayListOf(1, 2, 3, 4, 5)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainRvBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val homeAdapter = MyAdapter(list, this)
        binding.rv.layoutManager = PreCachingLayoutManager(
            this, Resources.getSystem().displayMetrics.widthPixels * 99, LinearLayoutManager.HORIZONTAL, false
        )
        binding.rv.adapter = homeAdapter
        binding.rv.setItemViewCacheSize(99)
        binding.btn.setOnClickListener {
            homeAdapter.updateEditMode(binding.rv)
        }
    }
}