package com.pinger.sample.screenshot

import android.app.Activity
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.util.LruCache
import android.view.View
import android.webkit.WebView
import android.widget.ListView
import android.widget.ScrollView


/**
 * @author Pinger
 * @since 2018/7/1 下午5:27
 * 实现截屏和截图的工具类
 * 参考：[https://blog.csdn.net/billy_zuo/article/details/71077681]
 * 参考:[https://github.com/PGSSoft/scrollscreenshot]
 */
object ScreenShotUtils {


    /**
     * 截取当前窗体的截图，包括当前窗体的状态栏
     */
    fun captureWindow(activity: Activity): Bitmap? {
        return captureWindow(activity, true)
    }


    /**
     * 截取当前窗体的截图，根据[isShowStatusBar]判断是否包含当前窗体的状态栏
     * 原理是获取当前窗体decorView的缓存生成图片
     */
    fun captureWindow(activity: Activity, isShowStatusBar: Boolean): Bitmap? {
        // 获取当前窗体的View对象
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        // 生成缓存
        view.buildDrawingCache()

        val bitmap = if (isShowStatusBar) {
            // 绘制整个窗体，包括状态栏
            Bitmap.createBitmap(view.drawingCache, 0, 0, view.measuredWidth, view.measuredHeight)
        } else {
            // 获取状态栏高度
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val display = activity.windowManager.defaultDisplay

            // 减去状态栏高度
            Bitmap.createBitmap(view.drawingCache, 0,
                    rect.top, display.width, display.height - rect.top)
        }

        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return bitmap
    }


    /**
     * View已经在界面上展示了，可以直接获取View的缓存
     * 对View进行量测，布局后生成View的缓存
     * View为固定大小的View，包括TextView,ImageView,LinearLayout,FrameLayout,RelativeLayout等
     * @param view 截取的View,View必须有固定的大小，不然drawingCache返回null
     * @return 生成的Bitmap
     */
    fun captureView(view: View): Bitmap? {
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        // 重新测量一遍View的宽高
        view.measure(View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY))
        // 确定View的位置
        view.layout(view.x.toInt(), view.y.toInt(), view.x.toInt() + view.measuredWidth,
                view.y.toInt() + view.measuredHeight)
        // 生成View宽高一样的Bitmap
        val bitmap = Bitmap.createBitmap(view.drawingCache, 0, 0, view.measuredWidth,
                view.measuredHeight)
        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return bitmap
    }


    /**
     * 截取ScrollerView
     * 原理是获取scrollView的子View的高度，然后创建一个子View宽高的画布，将ScrollView绘制在画布上
     * @param scrollView 控件
     * @return 返回截图后的Bitmap
     */
    fun captureScrollView(scrollView: ScrollView): Bitmap? {
        var h = 0
        for (i in 0 until scrollView.childCount) {
            val childView = scrollView.getChildAt(i)
            // 获取子View的高度
            h += childView.height
            // 设置背景颜色，避免布局里未设置背景颜色，截的图背景黑色
            childView.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        val bitmap = createBitmap(scrollView.width, h)
        val canvas = Canvas(bitmap)
        // 将ScrollView绘制在画布上
        scrollView.draw(canvas)
        return bitmap
    }


    /**
     * 截取ListView
     * 原理：获取到每一个子View，将子View生成的bitmap存入集合，并且累积ListView高度
     * 遍历完成后，创建一个ListView大小的画布，将集合的Bitmap绘制到画布上
     * @param listView 截图控件对象
     * @return 生成的截图对象
     */
    fun captureListView(listView: ListView): Bitmap? {
        val adapter = listView.adapter
        val itemCount = adapter.count
        var allitemsheight = 0
        val bitmaps = ArrayList<Bitmap>()

        for (i in 0 until itemCount) {
            // 获取每一个子View
            val childView = adapter.getView(i, null, listView)
            // 测量宽高
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

            // 布局位置
            childView.layout(0, 0, childView.measuredWidth, childView.measuredHeight)
            // 设置背景颜色，避免是黑色的
            childView.setBackgroundColor(Color.parseColor("#FFFFFF"))
            childView.isDrawingCacheEnabled = true
            // 生成缓存
            childView.buildDrawingCache()
            // 将每一个View的截图加入集合
            bitmaps.add(childView.drawingCache)
            // 叠加截图高度
            allitemsheight += childView.measuredHeight
        }

        // 创建和ListView宽高一样的画布
        val bitmap = createBitmap(listView.measuredWidth, allitemsheight)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        var iHeight = 0f

        for (i in bitmaps.indices) {
            val bmp: Bitmap = bitmaps[i]
            // 将每一个生成的bitmap绘制在画布上
            canvas.drawBitmap(bmp, 0f, iHeight, paint)
            iHeight += bmp.height

            bmp.recycle()
        }
        return bitmap
    }


    /**
     * 截取RecyclerView
     * 原理和ListView集合是一样的，获取到每一个Holder的截图放入集合，最后统一绘制到Bitmap上
     * @param recyclerView　要截图的控件
     * @return 生成的截图
     */
    fun captureRecyclerView(recyclerView: RecyclerView): Bitmap? {
        val adapter = recyclerView.adapter
        var bigBitmap: Bitmap? = null
        if (adapter != null) {
            val size = adapter.itemCount
            var height = 0
            val paint = Paint()
            var iHeight = 0
            val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

            // Use 1/8th of the available memory for this memory cache.
            val cacheSize = maxMemory / 8
            val bitmapCache = LruCache<String, Bitmap>(cacheSize)
            for (i in 0 until size) {
                val holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                holder.itemView.layout(0, 0, holder.itemView.measuredWidth,
                        holder.itemView.measuredHeight)
                holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.itemView.isDrawingCacheEnabled = true
                holder.itemView.buildDrawingCache()
                val drawingCache = holder.itemView.drawingCache
                if (drawingCache != null) {
                    bitmapCache.put(i.toString(), drawingCache)
                }
                height += holder.itemView.measuredHeight
            }

            bigBitmap = createBitmap(recyclerView.measuredWidth, height)
            val bigCanvas = Canvas(bigBitmap!!)
            val lBackground = recyclerView.background
            if (lBackground is ColorDrawable) {
                val lColor = lBackground.color
                bigCanvas.drawColor(lColor)
            }

            for (i in 0 until size) {
                val bitmap = bitmapCache.get(i.toString())
                bigCanvas.drawBitmap(bitmap, 0f, iHeight.toFloat(), paint)
                iHeight += bitmap.height
                bitmap.recycle()
            }
        }
        return bigBitmap
    }


    /**
     * 截取WebView，包含WebView的整个长度
     * 在WebView渲染之前要加上以下代码，开启Html缓存，不然会截屏空白
     *  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
     *      WebView.enableSlowWholeDocumentDraw()
     *  }
     *
     *  WebView的截图很容易遇到内存溢出的问题，因为WebView可以加载很多内容，导致生成的图片特别长，创建Bitmap时容易OOM
     */
    fun captureWebView(webView: WebView): Bitmap? {
        //　重新调用WebView的measure方法测量实际View的大小（将测量模式设置为UNSPECIFIED模式也就是需要多大就可以获得多大的空间）
        webView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        //　调用layout方法设置布局（使用新测量的大小）
        webView.layout(0, 0, webView.measuredWidth, webView.measuredHeight)
        //　开启WebView的缓存(当开启这个开关后下次调用getDrawingCache()方法的时候会把view绘制到一个bitmap上)
        webView.isDrawingCacheEnabled = true
        //　强制绘制缓存（必须在setDrawingCacheEnabled(true)之后才能调用，否者需要手动调用destroyDrawingCache()清楚缓存）
        webView.buildDrawingCache()

        val bitmap = createBitmap(webView.measuredWidth, webView.measuredHeight)

        //　已picture为背景创建一个画布
        val canvas = Canvas(bitmap)  // 画布的宽高和 WebView 的网页保持一致
        val paint = Paint()
        //　设置画笔的定点位置，也就是左上角
        canvas.drawBitmap(bitmap, 0f, webView.measuredHeight * 1f, paint)
        //　将WebView绘制在刚才创建的画板上
        webView.draw(canvas)
        webView.isDrawingCacheEnabled = false
        webView.destroyDrawingCache()
        return bitmap
    }


    /**
     * 创建一个指定大小的bitmap
     * 如果指定的宽高过大会造成内存溢出，这里做简单的处理，一般也不会截取这么大的图片
     */
    private fun createBitmap(width: Int, height: Int): Bitmap? {
        return try {
            Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        } catch (e: OutOfMemoryError) {
            System.gc()
            System.runFinalization()
            Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        }
    }

}