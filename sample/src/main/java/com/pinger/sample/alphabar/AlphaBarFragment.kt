package com.pinger.sample.alphabar

import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fungo.baselib.base.page.BasePageFragment
import com.pinger.sample.R
import kotlinx.android.synthetic.main.fragment_alpha_bar.*

/**
 * @author Pinger
 * @since 18-8-2 下午12:00
 *
 */

class AlphaBarFragment : BasePageFragment() {

    override fun getContentResId(): Int {
        return R.layout.fragment_alpha_bar
    }


    override fun isShowToolBar(): Boolean = false


    private val mData = ArrayList<String>()

    override fun initPageView() {
        getPageActivity()?.setSupportActionBar(toolbar)


        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        generateData()
        recyclerView.adapter = TestAdapter()


        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val alphaScale = (-verticalOffset) * 1f / (mAppBarHeight - mToolBarHeight)

            alphaView.alpha = alphaScale
        })
    }


    private val mAppBarHeight by lazy {
        appBar.measuredHeight
    }

    private val mToolBarHeight by lazy {
        toolbar.measuredHeight
    }


    private fun generateData() {
        for (i in 0..50) {
            mData.add("我是条目啊$i")
        }
    }


    inner class TestAdapter : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TestViewHolder {
            return TestViewHolder(LayoutInflater.from(context).inflate(R.layout.holder_recycler, null))

        }

        override fun onBindViewHolder(p0: TestViewHolder, p1: Int) {
            p0.title.text = mData[p1]

        }


        override fun getItemCount(): Int {
            return mData.size
        }

        inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title = itemView.findViewById<TextView>(R.id.title)!!
        }

    }


}