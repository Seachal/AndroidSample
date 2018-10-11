package com.pinger.sample.data

/**
 * @author Pinger
 * @since 18-7-4 下午6:21
 * 数据提供者
 */

object DataProvider {

    private val mData: ArrayList<String> = ArrayList()

    fun getData(): ArrayList<String> {
        mData.clear()
        for (i in 0..50) {
            mData.add("我是列表的条目$i")
        }
        return mData
    }

    fun getFlowLayoutData(): Array<String> {
        return arrayOf("QQ", "视频", "放开那三国", "电子书", "酒店", "单机", "小说", "斗地主", "优酷", "网游", "WIFI万能钥匙", "播放器", "捕鱼达人2", "机票", "游戏", "熊出没之熊大快跑", "美图秀秀", "浏览器", "单机游戏", "我的世界", "电影电视", "QQ空间", "旅游", "免费游戏", "2048", "刀塔传奇", "壁纸", "节奏大师", "锁屏", "装机必备", "天天动听", "备份", "网盘", "海淘网", "大众点评", "爱奇艺视频", "腾讯手机管家", "百度地图", "猎豹清理大师", "谷歌地图", "hao123上网导航", "京东", "有你", "万年历-农历黄历", "支付宝钱包")
    }

}