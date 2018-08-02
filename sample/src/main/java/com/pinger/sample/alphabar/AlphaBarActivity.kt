package com.pinger.sample.alphabar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.pinger.sample.R
import kotlinx.android.synthetic.main.activity_alpha_bar.*

/**
 * @author Pinger
 * @since 18-8-2 下午12:00
 *
 */

class AlphaBarActivity : AppCompatActivity() {

    private val mData = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_alpha_bar)
        initToolBar()
        initRecyclerView()
    }


    private val mAppBarHeight by lazy {
        appBar.measuredHeight
    }

    private val mToolBarHeight by lazy {
        toolbar.measuredHeight
    }

    private fun initToolBar() {
        setSupportActionBar(toolbar)

        appBar.addOnOffsetChangedListener { _, verticalOffset ->
            val alphaScale = (-verticalOffset) * 1f / (mAppBarHeight - mToolBarHeight)

            alphaView.alpha = alphaScale
        }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        generateData()
        recyclerView.adapter = TestAdapter()
    }

    private fun generateData() {
        for (i in 0..50) {
            mData.add("我是条目啊$i")
        }
    }


    inner class TestAdapter : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TestViewHolder {
            return TestViewHolder(LayoutInflater.from(this@AlphaBarActivity).inflate(R.layout.holder_recycler, null))
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        override fun onBindViewHolder(holder: TestViewHolder?, position: Int) {
            holder?.title?.text = mData[position]
        }

        inner class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title = itemView.findViewById<TextView>(R.id.title)
        }

    }


}