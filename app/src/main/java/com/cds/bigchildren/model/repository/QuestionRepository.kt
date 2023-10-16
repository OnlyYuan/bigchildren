package com.cds.bigchildren.model.repository

import com.cds.bigchildren.model.bean.StartAnswerBean
import com.cds.bigchildren.model.datasource.NextQuestionDatasource
import com.cds.bigchildren.model.datasource.StartQuestionDatasource


/**
 * 问答数据数据仓库
 */
class QuestionRepository(
    private val startQuestionDatasource: StartQuestionDatasource,
    private val nextQuestionDatasource: NextQuestionDatasource,
) {

    /**
     *开始答题准备
     *@param code  如 Q&A
     *@param sessionId 电话号码+数字
     */
    suspend fun startQuestionFun(code:String,sessionId:String ): StartAnswerBean?{
        return  startQuestionDatasource.load(code,sessionId)
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
                                 userResp:String): StartAnswerBean?{
        return  nextQuestionDatasource.load(code,currentNodeId,sessionId,userResp)
    }


}