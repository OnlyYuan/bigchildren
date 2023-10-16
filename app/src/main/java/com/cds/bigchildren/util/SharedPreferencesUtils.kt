package com.cds.bigchildren.util

import android.content.Context

class SharedPreferencesUtils {

    companion object{
        val UserInfo = "uerInfo"
        val IP = "ip"
        val NICK_NAME = "nickName"
        val ISLOGIN = "isLogin"
        val PHONE = "phone"


        /**
         * 存string
         */
        fun saveValue(context:Context,name:String,key:String,value:String){
            val sp = context.getSharedPreferences(name,Context.MODE_PRIVATE).edit()
            sp.putString(key,value)
            sp.apply()
        }

        /**
         * 取String
         */
        fun getValue(context:Context,name:String,key:String,defValue:String):String?{
            val sp = context.getSharedPreferences(name,Context.MODE_PRIVATE)
            return  sp.getString(key,"")
        }

    }
}