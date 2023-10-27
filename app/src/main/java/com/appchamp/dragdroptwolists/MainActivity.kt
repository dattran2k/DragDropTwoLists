package com.appchamp.dragdroptwolists

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.appchamp.dragdroptwolists.databinding.ActivityMainBinding


fun View.animateAlpha(from : Float,to : Float) {
    val animation1 = AlphaAnimation(from, to)
    animation1.duration = 100
    animation1.fillAfter = true
    this.startAnimation(animation1)
}
fun Context.toPx(dp: Int): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics).toInt()
class MainActivity : AppCompatActivity() {
    val dragInstance: DragListener = DragListener({
        val data = it.tag as String?
        if (data?.contains("Empty") == true) {
            it.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
            val params = it.layoutParams
            params.width = toPx(70)
            it.layoutParams = params
        }
    }, {
        val data = it.tag as String?
        if (data?.contains("Empty") == true) {
            it.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_200))
            val params = it.layoutParams
            params.width = toPx(50)
            it.layoutParams = params
        }
    })
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerView1.adapter = CustomAdapter(this@MainActivity, arrayListOf("A","Empty1","B","Empty2"), dragInstance)
        binding.recyclerView2.adapter = CustomAdapter(this@MainActivity, arrayListOf("E","Empty3", "F","Empty4"), dragInstance)
    }

}

