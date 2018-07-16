package com.pinger.sample.screenrotate

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings


/**
 * @author Pinger
 * @since 18-7-16 下午5:52
 * 重力感应处理工具类
 */

class ScreenRotateUtils(context: Context) {


    private var mActivity: Activity? = null
    private var isClickFullScreen: Boolean = false        // 记录全屏按钮的状态，默认false
    private var isOpenSensor = true      // 是否打开传输，默认打开
    private var isLandscape = false      // 默认是竖屏
    private var isChangeOrientation = true  // 记录点击全屏后屏幕朝向是否改变，默认会自动切换

    private var isEffectSysSetting = false   // 手机系统的重力感应设置是否生效，默认无效，想要生效改成true就好了

    private var sm: SensorManager? = null
    private var listener: OrientationSensorListener? = null
    private var sensor: Sensor? = null

    companion object {
        private const val DATA_X = 0
        private const val DATA_Y = 1
        private const val DATA_Z = 2
        const val ORIENTATION_UNKNOWN = -1
        @SuppressLint("StaticFieldLeak")
        private var instance: ScreenRotateUtils? = null


        /**
         * 初始化，获取实例
         *
         * @param context
         * @return
         */
        fun getInstance(context: Context): ScreenRotateUtils {
            if (instance == null) {
                instance = ScreenRotateUtils(context)
            }
            return instance!!
        }
    }

    /**
     * 接收重力感应监听的结果，来改变屏幕朝向
     */
    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 888) {
                val orientation = msg.arg1

                /**
                 * 根据手机屏幕的朝向角度，来设置内容的横竖屏，并且记录状态
                 */
                if (orientation in 46..134) {
                    mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    isLandscape = true
                } else if (orientation in 136..224) {
                    mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    isLandscape = false
                } else if (orientation in 226..314) {
                    mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    isLandscape = true
                } else if (orientation in 316..359 || orientation in 1..44) {
                    mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    isLandscape = false
                }
            }

        }
    }


    /**
     * 重力感应监听者
     */
    inner class OrientationSensorListener(private val rotateHandler: Handler?) : SensorEventListener {

        override fun onAccuracyChanged(arg0: Sensor, arg1: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            val values = event.values
            var orientation = ORIENTATION_UNKNOWN
            val x = -values[DATA_X]
            val y = -values[DATA_Y]
            val z = -values[DATA_Z]
            val magnitude = x * x + y * y
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= z * z) {
                // 屏幕旋转时
                val oneEightyOverPi = 57.29577957855f
                val angle = Math.atan2((-y).toDouble(), x.toDouble()).toFloat() * oneEightyOverPi
                orientation = 90 - Math.round(angle)
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360
                }
                while (orientation < 0) {
                    orientation += 360
                }
            }


            /**
             * 获取手机系统的重力感应开关设置，这段代码看需求，不要就删除
             * screenchange = 1 表示开启，screenchange = 0 表示禁用
             * 要是禁用了就直接返回
             */
            if (isEffectSysSetting) {
                try {
                    val isRotate = Settings.System.getInt(mActivity!!.contentResolver, Settings.System.ACCELEROMETER_ROTATION)

                    // 如果用户禁用掉了重力感应就直接return
                    if (isRotate == 0) return
                } catch (e: Settings.SettingNotFoundException) {
                    e.printStackTrace()
                }

            }


            // 只有点了按钮时才需要根据当前的状态来更新状态
            if (isClickFullScreen) {
                if (isLandscape && screenIsPortrait(orientation)) {           // 之前是横屏，并且当前是竖屏的状态
                    println("onSensorChanged: 横屏 ----> 竖屏")
                    updateState(false, false, true, true)
                } else if (!isLandscape && screenIsLandscape(orientation)) {  // 之前是竖屏，并且当前是横屏的状态
                    println("onSensorChanged: 竖屏 ----> 横屏")
                    updateState(true, false, true, true)
                } else if (isLandscape && screenIsLandscape(orientation)) {    // 之前是横屏，现在还是横屏的状态
                    println("onSensorChanged: 横屏 ----> 横屏")
                    isChangeOrientation = false
                } else if (!isLandscape && screenIsPortrait(orientation)) {  // 之前是竖屏，现在还是竖屏的状态
                    println("onSensorChanged: 竖屏 ----> 竖屏")
                    isChangeOrientation = false
                }
            }

            // 判断是否要进行中断信息传递
            if (!isOpenSensor) {
                return
            }

            rotateHandler?.obtainMessage(888, orientation, 0)?.sendToTarget()
        }

    }

    /**
     * 更新状态
     *
     * @param isLandscape         横屏
     * @param isClickFullScreen   全屏点击
     * @param isOpenSensor        打开传输
     * @param isChangeOrientation 朝向改变
     */
    private fun updateState(isLandscape: Boolean, isClickFullScreen: Boolean, isOpenSensor: Boolean, isChangeOrientation: Boolean) {
        this.isLandscape = isLandscape
        this.isClickFullScreen = isClickFullScreen
        this.isOpenSensor = isOpenSensor
        this.isChangeOrientation = isChangeOrientation
    }


    /**
     * 当前屏幕朝向是否横屏
     *
     * @param orientation
     * @return
     */
    private fun screenIsLandscape(orientation: Int): Boolean {
        return orientation in 46..135 || orientation in 226..315
    }

    /**
     * 当前屏幕朝向是否竖屏
     *
     * @param orientation
     * @return
     */
    private fun screenIsPortrait(orientation: Int): Boolean {
        return orientation in 316..360 || orientation in 0..45 || orientation in 136..225
    }

    /**
     * 根据朝向来改变屏幕朝向
     *
     * @param isLandscape
     * @param isNeedChangeOrientation 是否需要改变判断值
     */
    private fun changeOrientation(isLandscape: Boolean, isNeedChangeOrientation: Boolean) {
        if (isLandscape) {
            // 切换成竖屏
            mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            if (isNeedChangeOrientation) this.isLandscape = false
        } else {
            // 切换成横屏
            mActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            if (isNeedChangeOrientation) this.isLandscape = true
        }
    }


    /**
     * 开启监听
     * 绑定切换横竖屏Activity的生命周期
     *
     * @param activity
     */
    fun start(activity: Activity) {
        mActivity = activity
        sm?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    /**
     * 注销监听
     */
    fun stop() {
        sm?.unregisterListener(listener)
        mActivity = null  // 防止内存泄漏
    }


    /**
     * 当前屏幕的朝向，是否是横屏
     *
     * @return
     */
    fun isLandscape(): Boolean {
        return this.isLandscape
    }


    /**
     * 设置系统横竖屏按钮是否生效，默认无效
     *
     * @param isEffect
     */
    fun setEffetSysSetting(isEffect: Boolean) {
        isEffectSysSetting = isEffect
    }

    /**
     * 旋转的开关，全屏按钮点击时调用
     */
    fun toggleRotate() {

        /**
         * 先判断是否已经开启了重力感应，没开启就直接普通的切换横竖屏
         */
        if (isEffectSysSetting) {
            try {
                val isRotate = Settings.System.getInt(mActivity!!.contentResolver, Settings.System.ACCELEROMETER_ROTATION)

                // 如果用户禁用掉了重力感应就直接切换
                if (isRotate == 0) {
                    changeOrientation(isLandscape, true)
                    return
                }
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }

        }

        /**
         * 如果开启了重力i感应就需要修改状态
         */
        isOpenSensor = false
        isClickFullScreen = true
        if (isChangeOrientation) {
            changeOrientation(isLandscape, false)
        } else {
            isLandscape = !isLandscape
            changeOrientation(isLandscape, false)
        }
    }

    init {
        sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        sensor = sm!!.getDefaultSensor(Sensor.TYPE_GRAVITY)
        listener = OrientationSensorListener(mHandler)
    }

}