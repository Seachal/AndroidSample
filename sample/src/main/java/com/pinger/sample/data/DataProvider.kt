package com.pinger.sample.data

/**
 * @author Pinger
 * @since 18-7-4 下午6:21
 * 数据提供者
 */

object DataProvider {

    private val mData:ArrayList<String> = ArrayList()

    fun getData():ArrayList<String>{
        mData.clear()
        for (i in 0..50){
            mData.add("我是列表的条目$i")
        }
        return mData
    }

}