package com.appchamp.dragdroptwolists

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appchamp.dragdroptwolists.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val dragInstance: DragListener = DragListener()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerView1.init(arrayListOf("A", "B", "C"), binding.emptyListTextView1)
        binding.recyclerView2.init(arrayListOf("1", "2", "3"), binding.emptyListTextView2)
    }

    private fun RecyclerView.init(list: ArrayList<String>, emptyTextView: TextView) {
        this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = CustomAdapter(list, dragInstance)
        this.adapter = adapter
        emptyTextView.setOnDragListener(dragInstance)
        this.setOnDragListener(dragInstance)
    }
}

