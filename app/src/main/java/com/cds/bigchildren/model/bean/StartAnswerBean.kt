package com.cds.bigchildren.model.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 问答开始bean
 */
@Parcelize
data class StartAnswerBean(
    var children:ArrayList<NodeBean>?, //子节点
    var current:NodeBean?,//当前节点
) : Parcelable {

    @Parcelize
    data class NodeBean(
        var content:String?, //答案图片地址
        var contentType:String?,//内容类型 static静态 dynamic动态
        var createTime:String?,//
        var id:String?,
        var nodeId:String?,//节点id
        var nodeType:String?,//节点类型
        var processId:String?,//流程id
        var robotRespWay:String?,//解析回复
        var userRespWay:String?,//用户回复  none input select end
        var voicePath:String?,//声音
    ): Parcelable

}