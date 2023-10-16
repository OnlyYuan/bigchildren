package com.cds.bigchildren.util

import android.content.Context
import android.util.Log
import java.io.IOException
import java.net.ServerSocket


class MyServerSocket(private val port:Int,private val context : Context):Thread(){

    private  lateinit var serverSocket:ServerSocket
    override fun run() {
        try {
            serverSocket = ServerSocket(8080)
            var clientNum = 0
            Log.i("11","-->服务启动成功")
            while (true){
                val socket = serverSocket.accept()
                socketList.add(socket)
                clientNum++
                Log.i("11","-->链接数量：$clientNum")
                val mThread = Thread(SocketThread(socket))
                mThread.start()
            }
        }catch (iOException: IOException){
            Log.i("11","-->发送失败")
        }
    }



}