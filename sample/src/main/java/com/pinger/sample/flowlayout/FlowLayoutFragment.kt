package com.pinger.sample.flowlayout

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.fungo.baselib.base.page.BasePageFragment
import com.pinger.sample.R
import com.pinger.sample.data.DataProvider
import kotlinx.android.synthetic.main.fragment_flow_layout.*
import java.util.*


/**
 * @author Pinger
 * @since 18-7-16 下午4:17
 *
 */

class FlowLayoutFragment : BasePageFragment() {

    override fun getContentResId(): Int {
        return R.layout.fragment_flow_layout
    }

    override fun getPageTitle(): String? {
        return getString(R.string.sample_flow_layout)
    }


    override fun initData() {

        val random = Random()

        val datas = DataProvider.getFlowLayoutData()

        // 循环添加TextView到容器
        for (i in 0 until datas.size) {
            val view = TextView(context)
            view.text = datas[i]
            view.setTextColor(Color.WHITE)
            view.setPadding(5, 5, 5, 5)
            view.gravity = Gravity.CENTER
            view.textSize = 14f

            // 设置点击事件
            view.setOnClickListener {
                Toast.makeText(context, view.text.toString(), Toast.LENGTH_SHORT).show()
            }

            // 设置彩色背景
            val normalDrawable = GradientDrawable()
            normalDrawable.shape = GradientDrawable.RECTANGLE
            val a = 255
            val r = 50 + random.nextInt(150)
            val g = 50 + random.nextInt(150)
            val b = 50 + random.nextInt(150)
            normalDrawable.setColor(Color.argb(a, r, g, b))

            // 设置按下的灰色背景
            val pressedDrawable = GradientDrawable()
            pressedDrawable.shape = GradientDrawable.RECTANGLE
            pressedDrawable.setColor(Color.GRAY)

            // 背景选择器
            val stateDrawable = StateListDrawable()
            stateDrawable.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
            stateDrawable.addState(intArrayOf(), normalDrawable)

            // 设置背景选择器到TextView上
            view.background = stateDrawable

            flowLayout.addView(view)
        }
    }
}