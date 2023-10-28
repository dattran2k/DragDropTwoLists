package com.appchamp.dragdroptwolists

import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentTransaction
import com.appchamp.dragdroptwolists.databinding.ActivityMainBinding
import com.appchamp.dragdroptwolists.databinding.FragmentContainerTestBinding
import com.appchamp.dragdroptwolists.databinding.ListItemBinding


fun View.animateAlpha(from: Float, to: Float) {
    val animation1 = AlphaAnimation(from, to)
    animation1.duration = 100
    animation1.fillAfter = true
    this.startAnimation(animation1)
}

fun Context.toPx(dp: Int): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
).toInt()

fun Fragment.toPx(dp: Int): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp.toFloat(),
    resources.displayMetrics
).toInt()

class MainActivity : AppCompatActivity() {
    val dragInstance: DragListener = DragListener()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val adapter = MyPagerAdapter(supportFragmentManager)
        binding.pager.adapter = adapter
        //Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        //Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        binding.pager.offscreenPageLimit = adapter.count
        //A little space between pages
        //A little space between pages


        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.

        //If hardware acceleration is enabled, you should also remove
        // clipping on the pager for its children.
        binding.pager.clipChildren = false
    }

    inner class MyPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        val list = arrayListOf(1, 2, 3, 4, 5, 6, 7)
//        override fun instantiateItem(container: ViewGroup, position: Int): Any {
//            val view = TextView(this@MainActivity)
//            view.text = "Item $position"
//            view.gravity = Gravity.CENTER
//            view.setBackgroundColor(Color.argb(255, position * 50, position * 10, position * 50))
//            container.addView(view)
//            view.setOnClickListener {
//                Log.e("", "instantiateItem: $position")
//            }
//            view.setOnLongClickListener { v ->
//                val ds = DragShadow(v)
//                val data = ClipData.newPlainText("", "")
//                v?.startDragAndDrop(data, ds, v, View.DRAG_FLAG_OPAQUE)
//                v?.visibility = View.VISIBLE
//                true
//            }
//            view.setOnDragListener(dragInstance)
//            return view
//        }


        override fun getCount(): Int {
            return list.size / 2
        }

        override fun getPageWidth(position: Int): Float {
            return 0.5f
        }

        override fun getItem(position: Int): Fragment {
            return ContainerFragment.newInstance(
                list.subList(position * 2, list.size - 1).take(2).joinToString()
            )
        }
    }
}

class ContainerFragment : Fragment() {
    val TAG = "ContainerFragment"
    val dragInstance: DragListener by lazy {
        DragListener()
    }

    companion object {
        val NAME = "123123"
        fun newInstance(id: String): ContainerFragment {
            val args = Bundle()
            args.putString(NAME, id)
            val fragment = ContainerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var binding: FragmentContainerTestBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContainerTestBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: ")
    }

    class DataTag(val fragment: Fragment, val fm: FragmentManager, val name: String?) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString(NAME)?.split(",") ?: listOf()
        Log.e(TAG, "onViewCreated: ")
        val name1 = name.getOrNull(0)
        val name2 = name.getOrNull(1)
        val fragment1 = name1?.let { TextFragment.newInstance(it) }
        val fragment2 = name2?.let {
            TextFragment.newInstance(name2)
        }
        binding.frag1.tag = fragment1?.let { DataTag(it, childFragmentManager, name1) }
        binding.frag2.tag = fragment2?.let {
            DataTag(
                it, childFragmentManager, name2
            )
        }

        binding.view1.setOnDragListener(dragInstance)
        binding.view1.setOnLongClickListener { v ->
            val ds = DragShadow(v)
            val data = ClipData.newPlainText("123", "123")
            v?.startDragAndDrop(data, ds, v, View.DRAG_FLAG_OPAQUE)
            true
        }
        binding.view2.setOnDragListener(dragInstance)
        binding.view2.setOnLongClickListener { v ->
            val ds = DragShadow(v)
            val data = ClipData.newPlainText("123", "123")
            v?.startDragAndDrop(data, ds, v, View.DRAG_FLAG_OPAQUE)
            true
        }
        if (fragment1 != null) {
            val t: FragmentTransaction = childFragmentManager.beginTransaction()
            t.add(
                binding.frag1.id,
                fragment1,
                name1
            )
            t.commit()
        }
        if (fragment2 != null) {
            val t: FragmentTransaction = childFragmentManager.beginTransaction()
            t.add(
                binding.frag2.id,
                fragment2,
                name2
            )
            t.commit()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "onDestroyView: ")
    }
}

class TextFragment : Fragment() {
    val TAG = "TextFragment"

    companion object {
        val NAME = "123123"
        fun newInstance(id: String): TextFragment {
            val args = Bundle()
            args.putString(NAME, id)
            val fragment = TextFragment()
            fragment.arguments = args
            return fragment
        }
    }

    lateinit var binding: ListItemBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: ")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString(NAME) ?: ""
        binding.text.text = name
        view.tag = name
        Log.e(TAG, "onViewCreated: $name")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "onDestroyView: ")
    }
}
