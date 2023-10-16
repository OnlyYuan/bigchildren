package com.cds.bigchildren.model.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * 数据
 */
@Parcelize
data class ConfigDataBean(
    var contentList:ArrayList<ContentData>?,
) : Parcelable {

    @Parcelize
    data class ContentData(
        var qa:String?,
        var name:String?,//内容卡名称
        var genDu:ArrayList<GenDuBean>,//跟读历史
        var id:String?,
        var video:String?,//内容卡对应的视频
    ):Parcelable

    @Parcelize
    data class GenDuBean(
       var id:String?,
       var video:String?,//视频
       var audio:String?,//音频
       var text:String? //跟读内容
    ):Parcelable

}