package com.cds.bigchildren.ui

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.cds.bigchildren.R
import com.cds.bigchildren.base.BaseActivity
import com.cds.bigchildren.common.route.summaryClose
import com.cds.bigchildren.common.route.summaryGoNext
import com.cds.bigchildren.databinding.ActivitySummaryBinding
import com.cds.bigchildren.model.bean.ConfigDataBean
import com.cds.bigchildren.util.totalAnswerScore
import com.cds.bigchildren.util.totalGameScore
import com.cds.bigchildren.util.totalReadScore
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 总结页面
 */
class SummaryActivity : BaseActivity() {

    private var curContent: ConfigDataBean.ContentData?=null //配置数据
    private lateinit var mBinding: ActivitySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_summary)
        EventBus.getDefault().register(this)
        initData()
        initView()
    }

    private fun initData() {
        curContent = intent.getParcelableExtra("curContent") as ConfigDataBean.ContentData?
    }

    private fun initView() {
        mBinding.answerStar.text = getString(R.string.star_num_string, totalAnswerScore.toString())
        mBinding.readStar.text = getString(R.string.star_num_string, totalReadScore.toString())
        mBinding.gameStar.text = getString(R.string.star_num_string, totalGameScore.toString())
        val totalScore = totalAnswerScore+ totalGameScore+ totalReadScore
        mBinding.totalStar.text = getString(R.string.star_num_string,totalScore.toString())

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMsg(msg: String){
        when(msg){
            summaryGoNext->{//进入下一章
                val intent = Intent()
                intent.putExtra("next",true)
                this@SummaryActivity.setResult(101,intent)
                this@SummaryActivity.finish()

            }
            summaryClose->{//关闭
                this@SummaryActivity.finish()
            }
        }

    }
}