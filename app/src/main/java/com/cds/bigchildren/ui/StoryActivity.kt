package com.cds.bigchildren.ui

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.databinding.DataBindingUtil
import com.cds.bigchildren.R
import com.cds.bigchildren.base.BaseActivity
import com.cds.bigchildren.common.route.storyBackBtn
import com.cds.bigchildren.common.route.storyNextBtn
import com.cds.bigchildren.common.route.storyPlayBtn
import com.cds.bigchildren.databinding.ActivityStoryBinding
import com.cds.bigchildren.model.bean.ConfigDataBean
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException

/**
 * 读故事
 */
class StoryActivity : BaseActivity(), OnPreparedListener, SurfaceHolder.Callback {

    private lateinit var mBinding: ActivityStoryBinding
    private var player: MediaPlayer?= null
    private var surfaceHolder: SurfaceHolder?= null
    private var isPrepared = false //加载准备是否就绪
    private var curContent:ConfigDataBean.ContentData?=null //配置数据


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_story)
        EventBus.getDefault().register(this)
        initData()
        initVideo()
    }

    private fun initData() {
        curContent = intent.getParcelableExtra("curContent") as ConfigDataBean.ContentData?
    }

    private fun initVideo() {

        player = MediaPlayer()
        player?.setOnPreparedListener(this)
        player?.setOnCompletionListener {

        }

        //添加监听器
        surfaceHolder = mBinding.videoView.holder
        surfaceHolder?.addCallback(this)
        curContent?.video?.let {
            Log.i("11","-->video$it")
            play(it)
        }

     }

    /**
     * 播放视频111
     */
    private fun play(urlString: String){
        player?.reset()
        try {
            player?.setDataSource(urlString)
            player?.prepareAsync()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mBinding.videoView.visibility = View.VISIBLE
        mBinding.loading.visibility = View.GONE
        isPrepared = true
        player?.seekTo(1)
        player?.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        player?.setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    override fun onDestroy() {
        super.onDestroy()
        player?.reset()
        player?.release()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMsg(msg: String){
        when(msg){
            storyBackBtn->{
                this@StoryActivity.finish()
            }

            storyPlayBtn->{//播放按钮（改为自动播放）
                if (isPrepared){
                //    player?.start()
                }
            }

            storyNextBtn->{//下一项
                val intent = Intent(this@StoryActivity,ReadActivity::class.java)
                intent.putExtra("curContent",curContent)
                startActivity(intent)
                this@StoryActivity.finish()
            }
        }

    }

}