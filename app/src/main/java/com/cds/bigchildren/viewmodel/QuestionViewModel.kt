package com.cds.bigchildren.viewmodel

import androidx.lifecycle.ViewModel
import com.cds.bigchildren.model.bean.StartAnswerBean
import com.cds.bigchildren.model.repository.QuestionRepository


class QuestionViewModel(
    private val questionRepository: QuestionRepository
) : ViewModel() {

    /**
     * 开始答题准备
     *@param code  如 Q&A
     *@param sessionId 电话号码+数字
     */
    suspend fun startQuestionFun(code:String,sessionId:String):  StartAnswerBean?{
        return questionRepository.startQuestionFun(code,sessionId)
    }

    /**
     * 下一题以及上一题的答案
     *@param code  如 Q&A
     *@param currentNodeId  当前ip
     *@param sessionId
     *@param userResp  none/input/select/end
     */
    suspend fun startNextQuestion(code:String,
                                  currentNodeId:String,
                                  sessionId:String ,
                                  userResp:String):  StartAnswerBean?{
        return questionRepository.startNextQuestion(code,currentNodeId,sessionId,userResp)
    }

}