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
 */
object ScreenShotUtils {


    /**
     * 截取当前窗体的截图，包括当前窗体的状态栏
     */
    fun captureWindow(activity: Activity): Bitmap?{
        return captureWindow(activity,true)
    }


    /**
     * 截取当前窗体的截图，根据[isShowStatusBar]判断是否包含当前窗体的状态栏
     */
    fun captureWindow(activity: Activity, isShowStatusBar:Boolean): Bitmap?{
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()

        val bitmap = if (isShowStatusBar) {
            Bitmap.createBitmap(view.drawingCache, 0, 0, view.measuredWidth, view.measuredHeight)
        }else{
            // 获取状态栏高度
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val display = activity.windowManager.defaultDisplay

            Bitmap.createBitmap(view.drawingCache, 0,
                    rect.top, display.width, display.height - rect.top)
        }

        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return bitmap
    }



    /**
     * View已经在界面上展示了，可以直接获取View的缓存
     * 对View进行量测，布局后截图
     * View为固定大小的View，包括TextView,ImageView,LinearLayout,FrameLayout,RelativeLayout等
     * @param view 截取的View,View必须有固定的大小，不然drawingCache返回null
     * @return 生成的Bitmap
     */
    fun captureView(view: View): Bitmap? {
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        view.measure(View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.height, View.MeasureSpec.EXACTLY))
        view.layout(view.x.toInt(), view.y.toInt(), view.x.toInt() + view.measuredWidth,
                view.y.toInt() + view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.drawingCache, 0, 0, view.measuredWidth,
                view.measuredHeight)
        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return bitmap
    }


    /**
     * 截取ScrollerView
     */
    fun captureScrollView(scrollView: ScrollView): Bitmap?{
        // 获取子View的高度
        var h = 0
        for (i in 0 until scrollView.childCount) {
            h += scrollView.getChildAt(i).height
            // 设置子View的背景颜色，不设置默认是黑色的
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        val bitmap = Bitmap.createBitmap(scrollView.width, h, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        scrollView.draw(canvas)
        return bitmap
    }



    /**
     * 截取ListView
     */
    fun captureListView(listView: ListView): Bitmap?{
        val adapter = listView.adapter
        val itemCount = adapter.count
        var allitemsheight = 0
        val bitmaps = ArrayList<Bitmap>()

        for (i in 0 until itemCount) {
            val childView = adapter.getView(i, null, listView)
            childView.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

            childView.layout(0, 0, childView.measuredWidth, childView.measuredHeight)
            childView.isDrawingCacheEnabled = true
            childView.buildDrawingCache()
            bitmaps.add(childView.drawingCache)
            allitemsheight += childView.measuredHeight
        }

        val bitmap = Bitmap.createBitmap(listView.measuredWidth, allitemsheight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        var iHeight = 0f

        for (i in bitmaps.indices) {
            val bmp: Bitmap = bitmaps[i]
            canvas.drawBitmap(bmp, 0f, iHeight, paint)
            iHeight += bmp.height

            bmp.recycle()
        }
        return bitmap
    }



    /**
     * 截取RecyclerView
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
            val bitmapCache = LruCache<String,Bitmap>(cacheSize)
            for (i in 0 until size) {
                val holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                holder.itemView.layout(0, 0, holder.itemView.measuredWidth,
                        holder.itemView.measuredHeight)
                holder.itemView.isDrawingCacheEnabled = true
                holder.itemView.buildDrawingCache()
                val drawingCache = holder.itemView.drawingCache
                if (drawingCache != null) {
                    bitmapCache.put(i.toString(), drawingCache)
                }
                height += holder.itemView.measuredHeight
            }

            bigBitmap = Bitmap.createBitmap(recyclerView.measuredWidth, height, Bitmap.Config.ARGB_8888)
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
     */
    fun captureWebView(webView: WebView): Bitmap?{
        //　重新调用WebView的measure方法测量实际View的大小（将测量模式设置为UNSPECIFIED模式也就是需要多大就可以获得多大的空间）
        webView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        //　调用layout方法设置布局（使用新测量的大小）
        webView.layout(0, 0, webView.measuredWidth, webView.measuredHeight)
        //　开启WebView的缓存(当开启这个开关后下次调用getDrawingCache()方法的时候会把view绘制到一个bitmap上)
        webView.isDrawingCacheEnabled = true
        //　强制绘制缓存（必须在setDrawingCacheEnabled(true)之后才能调用，否者需要手动调用destroyDrawingCache()清楚缓存）
        webView.buildDrawingCache()
        //　根据测量结果创建一个大小一样的bitmap
        val bitmap = Bitmap.createBitmap(webView.measuredWidth,
                webView.measuredHeight, Bitmap.Config.ARGB_8888)
        //　已picture为背景创建一个画布
        val canvas = Canvas(bitmap)  // 画布的宽高和 WebView 的网页保持一致
        val paint = Paint()
        //　设置画笔的定点位置，也就是左上角
        canvas.drawBitmap(bitmap, 0f, webView.measuredHeight * 1f, paint)
        //　将webview绘制在刚才创建的画板上
        webView.draw(canvas)
        return bitmap
    }


}