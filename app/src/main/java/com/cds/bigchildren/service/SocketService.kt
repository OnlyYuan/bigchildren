package com.cds.bigchildren.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.cds.bigchildren.common.route.readNextVoiceBtn
import com.cds.bigchildren.common.route.readStartReadBtn
import com.cds.bigchildren.util.MyServerSocket
import com.cds.bigchildren.util.socketList
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.util.concurrent.Executors

class SocketService : Service() {

    private lateinit var myServerSocket:MyServerSocket
    private  val mThreadPool  = Executors.newCachedThreadPool()


    override fun onCreate() {
        EventBus.getDefault().register(this)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startServiceSocket()


        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 启动ServiceSocket
     */
    private fun startServiceSocket(){

        myServerSocket = MyServerSocket(8080,this)
        myServerSocket.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
    /**
     * 接收点击信息 发送给服务端
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMsg(msg: String){
        when(msg){
            readStartReadBtn->{//开始跟读
                sendMessageToClient(readStartReadBtn)
            }
            readNextVoiceBtn->{//跟读下一个
               sendMessageToClient(readNextVoiceBtn)
            }

            else->{

            }
        }
    }

    /**
     * 给客户端发送消息
     */
    private fun sendMessageToClient(msg: String){

        mThreadPool.execute {
            Log.i("11","-->发送准备socketList的大小${socketList.size}")
            for (mSocket in socketList){
                val mOutStream = mSocket.getOutputStream()
                val bufferedWriter = BufferedWriter(OutputStreamWriter(mOutStream))
                Log.i("11","-->发送准备")
                try {
                    bufferedWriter.write(msg+"\n")
                    bufferedWriter.flush()
                    Log.i("11","-->发送成功${msg}")
                }catch (iOException: IOException){
                    Log.i("11","-->发送失败")
                }
            }

        }
    }
}