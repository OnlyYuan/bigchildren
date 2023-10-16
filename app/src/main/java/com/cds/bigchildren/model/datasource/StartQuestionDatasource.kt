package com.cds.bigchildren.model.datasource

import com.cds.bigchildren.model.api.ApiService
import com.cds.bigchildren.model.bean.StartAnswerBean
import com.cds.bigchildren.util.net.RetrofitUtils


/**
 * 开始问答
 */
class StartQuestionDatasource {

    /**
     * 开始答题准备
     */
    suspend fun load (code:String,sessionId:String ): StartAnswerBean? {

          return  RetrofitUtils
                .getInstance()
                .getService<ApiService>()
                .startAnswer(
                    code,
                    sessionId
                )
                .getDataIfSuccess()
    }

}