package com.cds.bigchildren.ui

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.cds.bigchildren.R
import com.cds.bigchildren.base.BaseActivity
import com.cds.bigchildren.databinding.ActivityQuestionBinding
import com.cds.bigchildren.databinding.ActivityStoryBinding
import com.cds.bigchildren.model.bean.ConfigDataBean


import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
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


    val mViewModel : QuestionViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_question)
        EventBus.getDefault().register(this)
        totalAnswerScore = 0
        initRecycler()
        initAudio()
        startQuestionFun("Q&A")
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
            EventBus.getDefault().post(readStartReadBtn)
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
                            answerAdapter.setList(questionNodeList)
                            mBinding.questionTextView.text = currentNode.content?:""
                        }
                        if (!it1.current!!.voicePath.isNullOrEmpty()){
                            playAudio(it1.current!!.voicePath.toString())
                        }
                    }
                    if (it != null) {
                        when(it.current?.userRespWay){
                            "none"->{ //回答正确的时候
                                startNextQuestion(it.current?.nodeId?:"")
                                totalAnswerScore+=3
                            }
                            "end"->{//结束
                                answerAdapter.setList(questionNodeList)
                                mBinding.questionTextView.text = ""
                                mBinding.tip.visibility = View.VISIBLE
                                Toast.makeText(this@QuestionActivity,"答题结束", Toast.LENGTH_SHORT).show()
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


}