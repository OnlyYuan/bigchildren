package com.cds.bigchildren.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import com.cds.bigchildren.R
import com.cds.bigchildren.common.route.unitySmallToBigString
import com.cds.bigchildren.util.MyUnityPlayer
import com.unity3d.player.UnityPlayer
import com.unity3d.player.UnityPlayerActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MyUnityActivity : UnityPlayerActivity() {

    private var isCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_my_unity)
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMsg(msg: String){
        val msgArr = msg.split("?")

        when(msgArr[0]){
            unitySmallToBigString->{

                if (msgArr.size>1){
                    UnityPlayer.UnitySendMessage("GameManager","ReceiveMsg",msgArr[1])
                }

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    // C#的Base64转java的Base64
    private fun getBitMap(){}

}