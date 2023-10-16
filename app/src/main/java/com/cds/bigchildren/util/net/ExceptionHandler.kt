package com.cds.bigchildren.util.net

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.net.ConnectException

fun Throwable.getCustomErrorMessage(): String {
    Log.e("ExceptionHandler", this.toString())
    return when (this) {
        is ConnectException -> "网络连接不可用，请检查设备联网是否正常"
        is IOException -> "无法连接到服务器"
        is ResultException -> this.msg
        else -> this.message ?: "unknown error"
    }
}


object ExceptionHandler {

    /**
     * 处理错误
     */
    fun  handle(context: Context, throwable: Throwable, showToast: Boolean = true){

        if (throwable is kotlinx.coroutines.CancellationException){
            return
        }

        if (showToast){
           Toast.makeText(context,throwable.getCustomErrorMessage(),Toast.LENGTH_SHORT).show()
        }

        when (throwable) {
            is ResultException -> {
                when (throwable.code) {
                    ResultModel.CODE_TOKEN_ERROR -> {//刷新token失败，去登录。
                        Log.i("11","-->刷新token失败，去登录。")


                    }
                }
            }
        }

    }

}