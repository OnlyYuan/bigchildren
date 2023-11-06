package com.cds.bigchildren.ui


import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.cds.bigchildren.R
import com.cds.bigchildren.base.BaseActivity
import com.cds.bigchildren.common.route.readBackeBtn
import com.cds.bigchildren.common.route.readGoAnswerBtn
import com.cds.bigchildren.common.route.readNextVoiceBtn
import com.cds.bigchildren.common.route.readRecordOkScore
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
    private var isTypeVoice = 0  // 播放的声音类型 0.跟读提示音  1.重复提示音  2.评分时的提示音

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding  = DataBindingUtil.setContentView(this, R.layout.activity_read)
        EventBus.getDefault().register(this)
        initData()
        initView()
        initVideo()
        initAudio()
        initStarAnimation()
    }

    private fun initStarAnimation() {
        Glide.with(this)
            .load(R.mipmap.star_move)
            .into(mBinding.star1)

        Glide.with(this)
            .load(R.mipmap.star_move)
            .into(mBinding.star2)

        Glide.with(this)
            .load(R.mipmap.star_move)
            .into(mBinding.star3)

    }


    private fun initData() {
        totalReadScore = 0
    }

    private fun initView() {
        curContent = intent.getParcelableExtra("curContent") as ConfigDataBean.ContentData?
        curContent?.genDu?.let {
            genduList.addAll(it)
        }
        mBinding.totalScore.text=getString(R.string.star_num_string, totalReadScore.toString())
    }

    private fun initVideo() {
        player = MediaPlayer()
        player?.setOnPreparedListener(this)
        player?.setOnCompletionListener {
            Log.i("11","-->视频播放完成")
            isTypeVoice = 0
            genduList[curPosition].audio?.let {
                playAudio(it)
            }
            initStarAnimation()
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
            when(isTypeVoice){
                0->{//跟读提示音
                    mBinding.recordBtn.setImageResource(R.mipmap.reconder_icon)
                    EventBus.getDefault().post(readStartReadBtn)
                }

                1->{//错误 复读提示音
                  //  playAudio("http://114.255.82.226:9311/video/wrong.mp3")
                    replayVideo()
                }

                2->{//评分提示音
                  //  playAudio("http://114.255.82.226:9311/video/goodjob.mp3")

                }
            }
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


    /**
     * 根据分数判断星星数
     * @param score  1 2 3
     */
    private fun doStarAnimationFun(score:Int){
        //播放评分提示
        isTypeVoice = 2
        playAudio("http://114.255.82.226:9311/video/goodjob.mp3")
        when(score){

            1->{ //一颗星
                mBinding.star1.visibility = View.VISIBLE
                mBinding.star2.visibility = View.INVISIBLE
                mBinding.star3.visibility = View.INVISIBLE
                startAnimationFun(mBinding.star1.x,mBinding.star1.y,mBinding.star1)
            }

            2->{
                mBinding.star1.visibility = View.VISIBLE
                mBinding.star2.visibility = View.VISIBLE
                mBinding.star3.visibility = View.INVISIBLE
                startAnimationFun(mBinding.star1.x,mBinding.star1.y,mBinding.star1)
                startAnimationFun(mBinding.star2.x,mBinding.star2.y,mBinding.star2)

            }

            3->{
                mBinding.star1.visibility = View.VISIBLE
                mBinding.star2.visibility = View.VISIBLE
                mBinding.star3.visibility = View.VISIBLE
                startAnimationFun(mBinding.star1.x,mBinding.star1.y,mBinding.star1)
                startAnimationFun(mBinding.star2.x,mBinding.star2.y,mBinding.star2)
                startAnimationFun(mBinding.star3.x,mBinding.star3.y,mBinding.star3)
            }
        }

    }


    /**
     *
     */

    /**
     * 星星动画
     */
    private fun startAnimationFun(imgX:Float,imgY:Float,view: View){
        val animationSet = AnimationSet(true)
        val rotateAnimation = RotateAnimation(0f,360f,
            Animation.RELATIVE_TO_SELF,0.5f,
            Animation.RELATIVE_TO_SELF,0.5f)
        rotateAnimation.duration = 3000

        //mBinding.gitImg.startAnimation(rotateAnimation)
        val targetX = mBinding.bottomStarLayout.x + mBinding.totalStarImg.width/4
        val targetY = mBinding.bottomStarLayout.y
        Log.i("11","-->x $targetX  y $targetY")
        Log.i("11","-->imgX $imgX  imgY $imgY")
        val translateAnimation = TranslateAnimation(0f,targetX-imgX,0f,targetY-imgY   )
        translateAnimation.duration = 3000

        val scaleAnimation = ScaleAnimation(1.0f,0.3f,1.0f,0.3f)
        scaleAnimation.duration  =3000

        animationSet.addAnimation(rotateAnimation)
        animationSet.addAnimation(scaleAnimation)
        animationSet.addAnimation(translateAnimation)
        view.startAnimation(animationSet)

        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                view.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
    }

    override fun onPrepared(mp: MediaPlayer?) {

        if (isVideoOrAudio){
            player?.seekTo(1)
            mBinding.showStarView.visibility = View.GONE
            player?.start()
        } else{
            audioPlayer?.start()
        }

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

            readRecordOkScore->{//录音结束
                mBinding.recordBtn.setImageResource(R.mipmap.reconder_close_icon)
            }

            readNextVoiceBtn->{//读下一段
                curPosition++
                if (curPosition<genduList.size){
                    play(genduList[curPosition].video?:"")
                }else{
                    mBinding.showStarView.visibility = View.VISIBLE
                    mBinding.overTag.visibility = View.VISIBLE
                }
            }

            readGoAnswerBtn->{//进入问答
                val intent = Intent(this@ReadActivity,QuestionActivity::class.java)
                intent.putExtra("curContent",genduList)
                startActivity(intent)
                this@ReadActivity.finish()
            }

            readScore->{//得分
                if (msgArray.size>1){
                    val mScore = msgArray[1].toInt()
                    val mRePlay = msgArray[2].toInt()
                    //显示图片
                    if (mScore >1){
                        totalReadScore += mScore
                        doStarAnimationFun(mScore)
                        mBinding.showStarView.visibility = View.VISIBLE
                        mBinding.scoreImg.visibility = View.VISIBLE
                    }else if(mRePlay==3){//得分为1
                        totalReadScore += mScore
                        doStarAnimationFun(mScore)
                        mBinding.showStarView.visibility = View.VISIBLE
                        mBinding.scoreImg.visibility = View.GONE
                    }else{//重播
                        //重播提示音
                        isTypeVoice = 1
                        playAudio("http://114.255.82.226:9311/video/wrong.mp3")
                    }

                }
                mBinding.totalScore.text = getString(R.string.star_num_string, totalReadScore.toString())
            }
        }
    }


    /**
     * 重播
     */
    private fun replayVideo(){

        if (curPosition<genduList.size){
            play(genduList[curPosition].video?:"")
        }else{
            mBinding.showStarView.visibility = View.VISIBLE
            mBinding.overTag.visibility = View.VISIBLE
        }
    }
}