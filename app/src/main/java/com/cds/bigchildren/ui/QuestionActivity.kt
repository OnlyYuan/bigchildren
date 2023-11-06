package com.cds.bigchildren.ui

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import androidx.databinding.DataBindingUtil
import com.cds.bigchildren.R
import com.cds.bigchildren.base.BaseActivity
import com.cds.bigchildren.databinding.ActivityQuestionBinding
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.cds.bigchildren.adapter.AnswerAdapter
import com.cds.bigchildren.common.route.questionSelectAnswer
import com.cds.bigchildren.common.route.questiongoBack
import com.cds.bigchildren.common.route.readStartReadBtn
import com.cds.bigchildren.model.bean.StartAnswerBean
import com.cds.bigchildren.util.net.DataHandler
import com.cds.bigchildren.util.totalAnswerScore
import com.cds.bigchildren.util.totalSessionId
import com.cds.bigchildren.viewmodel.QuestionViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

/**
 * 问题页面
 */
class QuestionActivity : BaseActivity(), MediaPlayer.OnPreparedListener {

    private lateinit var mBinding: ActivityQuestionBinding
    private lateinit var answerAdapter: AnswerAdapter
    private val questionNodeList = ArrayList<StartAnswerBean.NodeBean>() //答案list
    private lateinit var currentNode: StartAnswerBean.NodeBean //当前节点
    private var audioPlayer: MediaPlayer?= null //音频播放
    private var curAnswerPosition= 0//当前回答位置
    private var isFirstVoice = true //第一个音频是否读完
    private var isSuccess = false //讀正確

    val mViewModel : QuestionViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_question)
        EventBus.getDefault().register(this)
        totalAnswerScore = 0
        initView()
        initRecycler()
        initAudio()
        initStarAnimation()
        startQuestionFun("Q&A")
    }

    private fun initView() {

        mBinding.totalScore.text = getString(R.string.star_num_string,totalAnswerScore.toString())
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

    private fun initRecycler() {
        answerAdapter = AnswerAdapter()
        val layoutManager = GridLayoutManager(this,2)
        mBinding.answerRecycle.layoutManager = layoutManager
        mBinding.answerRecycle.adapter = answerAdapter
    }

    /**
     * 答题提示播放
     */
    private fun initAudio(){
        audioPlayer = MediaPlayer()
        audioPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        audioPlayer?.setOnPreparedListener(this)
        audioPlayer?.setOnCompletionListener {
            Log.i("11","-->音频完成")
            if (isFirstVoice){//第一个voice，提示音
                isFirstVoice = false
                if (!(currentNode.voicePath.isNullOrEmpty())){
                    playAudio(currentNode.voicePath.toString())
                }
            }
        }
    }

    /**
     * 播放音频
     */
    private fun playAudio(audioString: String){
        audioPlayer?.reset()
        try {
            audioPlayer?.setDataSource(audioString)
            audioPlayer?.prepareAsync()
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    /**
     * 根据分数判断星星数
     * @param score  1 2 3
     */
    private fun doStarAnimationFun(score:Int){
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



    /**
     * 开始答题准备
     *@param code  如 Q&A
     *@param sessionId 电话号码+数字
     */
    private fun startQuestionFun(code:String){

        lifecycleScope.launch {
            DataHandler.performCollect(
                this@QuestionActivity,
                block = {
                    mViewModel.startQuestionFun(code,totalSessionId)
                },
                onError = {
                    Log.i("11","--->失败")

                },
                onSuccess = {
                    it?.let {it1->
                        it1.current?.voicePath?.let { it2 ->
                            playAudio(it2)
                        }
                        startNextQuestion(it1.current?.nodeId?:"")
                    }
                }
            )
        }
    }


    /**
     * 下一题以及上一题的答案
     *@param code  如 Q&A
     *@param currentNodeId  当前ip
     *@param sessionId
     *@param userResp  none/input/select/end
     */
    private fun startNextQuestion(currentNodeId:String,userResp:String?="none"){

        lifecycleScope.launch {
            DataHandler.performCollect(
                this@QuestionActivity,
                block = {
                    mViewModel.startNextQuestion("Q&A",currentNodeId,totalSessionId,userResp?:"none")
                },
                onError = {
                    Log.i("11","--->失败")

                },
                onSuccess = {
                    it?.let {it1->
                        currentNode = it1.current!!
                        questionNodeList.clear()
                        it1.children?.let { it2 ->
                            questionNodeList.addAll(it2)
                            mBinding.questionTextView.text = currentNode.content?:""
                        }
                        answerAdapter.setList(questionNodeList)
                        if (!it1.current!!.voicePath.isNullOrEmpty()){
                            if (!isFirstVoice){
                                playAudio(it1.current!!.voicePath.toString())
                            }
                        }
                        mBinding.showStarView.visibility = View.GONE
                    }
                    if (it != null) {
                        when(it.current?.userRespWay){
                            "none"->{ //回答正确的时候
                                isSuccess = true
                                val myCountIme1 = WaitCounterTime(it.current?.nodeId?:"",2000L,1000L)
                                myCountIme1.start()
                                totalAnswerScore+=3
                                if(it.current!!.content == "Well done!"){
                                    mBinding.scoreImg.setImageResource(R.mipmap.well_done)
                                }else{
                                    mBinding.scoreImg.setImageResource(R.mipmap.good_job)
                                }
                                mBinding.showStarView.visibility = View.VISIBLE
                                doStarAnimationFun(3)
                                mBinding.totalScore.text = getString(R.string.star_num_string,totalAnswerScore.toString())
                            }
                            "end"->{//结束
                                answerAdapter.setList(questionNodeList)
                                mBinding.questionTextView.text = ""
                                mBinding.tip.visibility = View.VISIBLE
                                mBinding.showStarView.visibility = View.VISIBLE
                                mBinding.bear.visibility = View.GONE
                                mBinding.scoreImg.visibility = View.GONE

                                totalAnswerScore+=3
                                doStarAnimationFun(3)
                                mBinding.totalScore.text = getString(R.string.star_num_string,totalAnswerScore.toString())
                                val myCountIme = WaitCounterTime("",5000L,1000L)
                                myCountIme.start()
                               // Toast.makeText(this@QuestionActivity,"答题结束", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        audioPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayer?.reset()
        audioPlayer?.release()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMsg(msg: String){
        val datas = msg.split("?")
        when(datas[0]){
            questionSelectAnswer->{//选择答案的回传
                if (datas.size>1){
                    curAnswerPosition = (datas[1]?:"0").toInt()
                    startNextQuestion(currentNode.nodeId?:"",questionNodeList[curAnswerPosition].nodeId)
                }

            }

            questiongoBack->{//退出
                this@QuestionActivity.finish()
            }
        }

    }


    inner class WaitCounterTime( var nodeId:String,var totalTime:Long,var countInterval:Long ):
        CountDownTimer(totalTime,countInterval){
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {

            if (isSuccess){//成功后，等2s播放下一個
                startNextQuestion(nodeId)
                isSuccess = false
            }else{
                startActivity(Intent(this@QuestionActivity,MainActivity::class.java))
            }

        }

    }

}