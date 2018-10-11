# Android截屏和指定View生成截图分享

做项目开发过程中有遇到过根据用户行为动态生成图片进行分享的需求，当时因为后端有大神，这个活就被大神给揽走了。后端是使用JavaAPI进行绘制的，具体的细节不清楚，但是实现起来也是挺难的。

后来大神溜了，新的活只能自己揽下来了。据我所知Android端的View是可以生成图片的，但是具体的细节不是很清楚，在网上查找了很多Demo，研究了一阵时间，重新整理记录下来。

当前页面截图（截取整个屏幕）

截取当前Activity页面的截图，可以通过窗体最底层的decorView进行缓存，然后根据这个缓存对象生成一张图片。有的需要不需要状态栏，也可以指定生成图片的宽高，把状态栏去除。

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
截取常用的View（TextView，RelativeLayout...）

截取之前一定要View已经绘制完毕了，所以要注意使用post方法确保View绘制完毕。有的分享出去的截图页面并不是当前展示给用户的UI样式，所以可以在当前布局中隐藏一个容器，专门用来存放截图，这个容器不展示给用户。

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
截取ScrollView

ScrollView截图的难点在于ScrollView高度不确定，如果能确定高度，就可以使用ScrollView的缓存生成图片。ScrollView只有一个子View，所以只需要对子View进行测量，获取到子View的高度就可以确定ScrollView的高度。

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
截取ListView

ListView截图原理是获取到每一个子View的Bitmap对象，然后根据子View的高度，使用Paint将子View的截图拼接绘制到Canvas上，最后生成一张包含所有子View的截图。

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
截取RecyclerView

RecyclerView截图和ListView截图原理一样，都是将子View的截图进行拼接，最后生成一张大的截图。

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
截取WebView

WebView可以加载很长很复杂的页面，所以进行截图很容易发生内存溢出，不过一般的需求也不会有那个大的图片去分享，这里只做简单的截图。

/**
 * 截取WebView，包含WebView的整个长度
 * 在WebView渲染之前要加上以下代码，开启Html缓存，不然会截屏空白
 *  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
 *      WebView.enableSlowWholeDocumentDraw()
 *  }
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
基本常用的View都可以进行截图，但是像WebView这种可以加载很长很复杂页面的控件，截图容易发生OOM，所以要考虑好用什么控件进行截图。上面的代码亲测都可以使用。