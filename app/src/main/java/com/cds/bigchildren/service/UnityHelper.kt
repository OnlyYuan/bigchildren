package com.cds.bigchildren.service

import android.util.Log
import com.cds.bigchildren.common.route.finishUnity
import com.cds.bigchildren.common.route.unitySmallToBigString
import org.greenrobot.eventbus.EventBus

class UnityHelper {
    fun sendUnityMessage(msg:String){
        val mString = "$unitySmallToBigString?$msg"
        EventBus.getDefault().post(mString)
        Log.i("11","-->调用了sendUnityMessage方法${mString}")
    }

    /**
     * 关闭unity应用
     */
    fun finishUnityPlayer(){
        EventBus.getDefault().post(finishUnity)
    }
}