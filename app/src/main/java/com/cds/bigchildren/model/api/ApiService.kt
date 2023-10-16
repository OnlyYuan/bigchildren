package com.cds.bigchildren.model.api

import com.cds.bigchildren.util.net.ResultModel
import com.cds.bigchildren.model.bean.ConfigDataBean
import com.cds.bigchildren.model.bean.StartAnswerBean
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    //获取全局数据
    @GET("/config")
    suspend fun getConfigData():ResultModel<ConfigDataBean?>

    /**
     * 开始答题准备
     */
    @GET("/imApi/process/startChat")
    suspend fun startAnswer(
        @Query("code") code:String,// Q&A
        @Query("sessionId") sessionId:String, //电话号码 + 随机
    ):ResultModel<StartAnswerBean>


    /**
     * 下一个节点（下一题和上一题的回答）
     */
    @GET("/imApi/process/getChatContent")
    suspend fun startNextQuestion(
        @Query("code") code:String,// Q&A
        @Query("currentNodeId") currentNodeId:String,//当前节点id
        @Query("sessionId") sessionId:String, //电话号码 + 随机
        @Query("userResp") userResp:String, //对话内容
    ):ResultModel<StartAnswerBean>

}