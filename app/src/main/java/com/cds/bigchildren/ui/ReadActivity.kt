package com.cds.bigchildren.ui


import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import androidx.databinding.DataBindingUtil
import com.cds.bigchildren.R
import com.cds.bigchildren.base.BaseActivity
import com.cds.bigchildren.common.route.readBackeBtn
import com.cds.bigchildren.common.route.readGoAnswerBtn
import com.cds.bigchildren.common.route.readNextVoiceBtn
import com.cds.bigchildren.common.route.readScore
import com.cds.bigchildren.common.route.readStartReadBtn
import com.cds.bigchildren.databinding.ActivityReadBinding
import com.cds.bigchildren.model.bean.ConfigDataBean
import com.cds.bigchildren.util.totalReadScore
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException

/**
 * 跟读
 */
class ReadActivity : BaseActivity(), MediaPlayer.OnPreparedListener, SurfaceHolder.Callback {

    private lateinit var mBinding: ActivityReadBinding
    private var curContent:ConfigDataBean.ContentData?=null //配置数据
    private var genduList = ArrayList<ConfigDataBean.GenDuBean>()//跟读列表
    private var curPosition = 0//跟读的position
    private var player: MediaPlayer?= null
    private var surfaceHolder: SurfaceHolder?= null
    private var isPrepared = false //加载准备是否就绪
    private var audioPlayer: MediaPlayer?= null //音频播放
    private var isVideoOrAudio = true // true表示 Video  false audio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding  = DataBindingUtil.setContentView(this, R.layout.activity_read)
        EventBus.getDefault().register(this)
        initData()
        initView()
        initVideo()
        initAudio()
    }

    private fun initData() {
        totalReadScore = 0
    }

    private fun initView() {
        curContent = intent.getParcelableExtra("curContent") as ConfigDataBean.ContentData?
        curContent?.genDu?.let {
            genduList.addAll(it)
        }
        mBinding.totalScore.text="0"
    }

    private fun initVideo() {
        player = MediaPlayer()
        player?.setOnPreparedListener(this)
        player?.setOnCompletionListener {
            Log.i("11","-->视频播放完成")
            genduList[curPosition].audio?.let {
                playAudio(it)
            }
        }
        //添加监听器
        surfaceHolder = mBinding.videoView.holder
        surfaceHolder?.addCallback(this)
        genduList[curPosition].video?.let {
            Log.i("11","-->video$it")
            play(it)
        }
    }

    //audio
    private fun initAudio(){
        audioPlayer = MediaPlayer()
        audioPlayer?.setOnPreparedListener(this)
        audioPlayer?.setOnCompletionListener {
            Log.i("11","-->音频完成")
            EventBus.getDefault().post(readStartReadBtn)
        }
    }


    /**
     * 播放音频
     */
    private fun playAudio(audioString: String){
        isVideoOrAudio = false
        audioPlayer?.reset()
        try {
            Log.i("11","audio${audioString}")
            audioPlayer?.setDataSource(audioString)
            audioPlayer?.prepareAsync()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    /**
     * 播放视频
     */
    private fun play(urlString: String){
        isVideoOrAudio = true
        player?.reset()
        try {
            player?.setDataSource(urlString)
            player?.prepareAsync()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }


    override fun onPrepared(mp: MediaPlayer?) {
        if (isVideoOrAudio)
            player?.start()
        else
            audioPlayer?.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (isVideoOrAudio){
            player?.setDisplay(holder)
        }

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    override fun onDestroy() {
        super.onDestroy()
        player?.reset()
        player?.release()
        audioPlayer?.reset()
        audioPlayer?.release()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMsg(msg: String){
        var msgArray = msg.split("?")
        when(msgArray[0]){
            readBackeBtn->{//返回
                this@ReadActivity.finish()
            }

            readNextVoiceBtn->{//读下一段
                curPosition++
                if (curPosition<genduList.size){
                    play(genduList[curPosition].video?:"")
                }
            }

            readGoAnswerBtn->{//进入问答
                val intent = Intent(this@ReadActivity,QuestionActivity::class.java)
                intent.putExtra("curContent",genduList)
                startActivity(intent)
            }

            readScore->{//得分
                if (msgArray.size>1){
                    totalReadScore += msgArray[1].toInt()
                }
                mBinding.totalScore.text = totalReadScore.toString()
            }
        }
    }
}