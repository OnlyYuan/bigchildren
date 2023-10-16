package com.cds.bigchildren.model.datasource

import com.cds.bigchildren.model.api.ApiService
import com.cds.bigchildren.model.bean.StartAnswerBean
import com.cds.bigchildren.util.net.RetrofitUtils


/**
 * 开始问答
 */
class NextQuestionDatasource {

    /**
     * 下一题以及上一题的答案
     */
    suspend fun load (code:String,
                      currentNodeId:String,
                      sessionId:String ,
                      userResp:String
    ): StartAnswerBean? {

          return  RetrofitUtils
                .getInstance()
                .getService<ApiService>()
                .startNextQuestion(
                    code,
                    currentNodeId,
                    sessionId,
                    userResp
                )
                .getDataIfSuccess()
    }

}