package com.cds.bigchildren.util.net

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser


private val mGson by lazy {
    Gson()
}
private val mJsonParser by lazy {
    JsonParser()
}

/**
 * 把 [Map<String, Any?>] 类型的参数转换成 api 接口需要的 [JsonObject] 类型的参数。
 */
fun Map<String, Any?>.toJsonObjectForApi(): JsonObject {
    var params = JsonObject()
    if (this.isNotEmpty()) {
        params =  mJsonParser.parse(mGson.toJson(this)).asJsonObject
    }

//    if (this.isNotEmpty()) {
//
//        params.add("query", mJsonParser.parse(mGson.toJson(this)))
//    }
//    params.add("payload", JsonObject().apply {
//      //  addProperty("token", ILoginService.getInstance()?.getUserInfo()?.token)
//        addProperty("token", "9b09PtOCGZ5El-HSOqgla2rO_20")
//    })
    return params
}

fun Map<String, Any?>.toFilterEmpty(): Map<String, Any?> {

    var mMap = mutableMapOf<String, Any?>()
    this.forEach{
        if (!(it.value ==""||it.value==null)){
            mMap.put(it.key,it.value)
        }
    }

    return mMap
}