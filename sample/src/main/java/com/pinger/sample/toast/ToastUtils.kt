package com.pinger.sample.toast

import android.content.Context
import android.graphics.PixelFormat
import android.os.SystemClock
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.fungo.baselib.utils.SpUtils
import com.pinger.sample.R


/**
 * @author Pinger
 * @since 18-7-16 下午2:26
 *
 */

class ToastUtils(context: Context) {

    private var mContext: Context? = context
    private var mView: View? = null
    private var mWm: WindowManager? = null
    private var mParams: WindowManager.LayoutParams? = null

    private var mTextView: TextView? = null


    /**
     * 定义点击次数的数组，数组的数字是几就可以定义几击事件
     */
    private val mHits1 = LongArray(2)
    private val mHits2 = LongArray(3)


    /**
     * 初始化窗体属性
     */
    private fun initParams() {
        mWm = mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager?

        mParams = WindowManager.LayoutParams()

        mParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT
        mParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mParams!!.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        // 类型
        mParams!!.type = WindowManager.LayoutParams.TYPE_PHONE

        // 透明，不透明会出现重叠效果
        mParams!!.format = PixelFormat.TRANSLUCENT

        // 位置属性
        mParams!!.gravity = Gravity.TOP + Gravity.LEFT  // 左上

        // 进来的时候把存储的位置读取显示出来
        mParams!!.x = SpUtils.getInt("lastX")
        mParams!!.y = SpUtils.getInt("lastY")

        // 初始化吐司窗口布局
        mView = View.inflate(mContext, R.layout.layout_toast, null)

        mTextView = mView!!.findViewById(R.id.tvToastText) as TextView

        // 设置吐司的双击事件，点击之后会到中心点
        mView!!.setOnClickListener({
            // 双击事件处理逻辑
            System.arraycopy(mHits1, 1, mHits1, 0, mHits1.size - 1)
            mHits1[mHits1.size - 1] = SystemClock.uptimeMillis()
            if (mHits1[0] >= SystemClock.uptimeMillis() - 500) {
                // 双击之后执行
                // 让吐司移动到x中心，y不需要对中
                // 更新窗体的坐标
                mParams!!.x = (mWm!!.defaultDisplay.width - mView!!.width) / 2
                mWm?.updateViewLayout(mView, mParams)

                // 点击完退出的时候也把位置信息存储起来
                SpUtils.putInt("lastX", mParams!!.x)
                SpUtils.putInt("lastY", mParams!!.y)
            }


            // 三击事件处理逻辑
            System.arraycopy(mHits2, 1, mHits2, 0, mHits2.size - 1)
            mHits2[mHits2.size - 1] = SystemClock.uptimeMillis()
            if (mHits2[0] >= SystemClock.uptimeMillis() - 600) {
                // 点击之后将吐司移除掉
                if (mView != null) {
                    if (mView!!.parent != null) {
                        mWm?.removeView(mView)
                    }
                }
            }
        })


        var startX = 0
        var startY = 0
        // 设置吐司的触摸滑动事件
        mView!!.setOnTouchListener({ _, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN // 按下
                -> {
                    // 手指按下时的坐标位置
                    startX = event.rawX.toInt()
                    startY = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE // 移动
                -> {

                    // 移动后的坐标位置
                    val newX = event.rawX.toInt()
                    val newY = event.rawY.toInt()

                    // 偏移量
                    val dx = newX - startX
                    val dy = newY - startY

                    // 给偏移量设置边距
                    // 小于x轴
                    if (mParams!!.x < 0) {
                        mParams!!.x = 0
                    }
                    // 小于y轴
                    if (mParams!!.y < 0) {
                        mParams!!.y = 0
                    }

                    // 超出x轴
                    if (mParams!!.x > mWm!!.defaultDisplay.width - mView!!.width) {
                        mParams!!.x = mWm!!.defaultDisplay.width - mView!!.width
                    }
                    // 超出y轴
                    if (mParams!!.y > mWm!!.defaultDisplay.height - mView!!.height) {
                        mParams!!.y = mWm!!.defaultDisplay.height - mView!!.height
                    }

                    // 更新窗体的坐标
                    mParams!!.x += dx
                    mParams!!.y += dy
                    mWm?.updateViewLayout(mView, mParams)

                    // 重新赋值起始坐标
                    startX = event.rawX.toInt()
                    startY = event.rawY.toInt()
                }
                MotionEvent.ACTION_UP // 抬起
                -> {
                    // 抬起来的时候保存最后一次的位置，下次进来时直接显示出来
                    SpUtils.putInt("lastX", mParams!!.x)
                    SpUtils.putInt("lastX", mParams!!.y)
                }
                else -> {
                }
            }
            false
        })

    }


    /**
     * 弹出自定义吐司
     */
    fun popToast(text: String) {
        // 设置显示的文字
        mTextView?.text = text

        if (mView?.parent != null) {
            mWm?.removeView(mView)
        }
        // 添加到窗体管理器中才能显示出来
        mWm?.addView(mView, mParams)

        // 可以开启倒计时执行消失
    }

    /**
     * 从父窗体中移除吐司
     */
    fun hideToast() {
        if (mView?.parent != null) {
            mWm?.removeView(mView)
        }
    }

    init {
        initParams()
    }
}