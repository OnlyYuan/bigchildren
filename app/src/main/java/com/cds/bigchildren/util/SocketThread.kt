package com.cds.bigchildren.util

import android.util.Log
import org.greenrobot.eventbus.EventBus
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

/**
 * 处理socket的消息
 */
class SocketThread(private var socket: Socket):Runnable {

    private var br :BufferedReader? = null
    private var bufferedWriter :BufferedWriter? = null

    override fun run() {
        br = BufferedReader(InputStreamReader(socket.getInputStream()))
        bufferedWriter = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
        try {
            Log.i("11","--->进入到读取到信息")
            var content :String?= ""
            while ((readFromClient().also { content = it })!="-1"){
                if (content?.isNotEmpty() == true){
                    Log.i("11","--->读取到信息${content}")
                    EventBus.getDefault().post(content)
                }
            }

        }catch (e:Exception){
            Log.i("11","--->printStackTrace${e.printStackTrace()}")
        }
    }

    private fun readFromClient():String?{
        try {
            val  string = br?.readLine()
            //Log.i("222","--->读取readFromClient}")
            return  string
        }catch (e:Exception){
            socketList.remove(socket)
            Log.i("222","--->Exception${e.message.toString()}")
        }
         return "-1"
    }

    /**
     * 发送信息给客户端
     */
    private fun sendMessage(msg:String){
        Log.i("11","-->发送准备")
        try {
            bufferedWriter!!.write(msg+"\n")
            bufferedWriter!!.flush()
            Log.i("11","-->发送成功${msg}")
        }catch (iOException: IOException){
            Log.i("11","-->发送失败")
        }
    }
}