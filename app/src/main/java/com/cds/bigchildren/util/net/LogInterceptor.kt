package com.cds.bigchildren.util.net

import okhttp3.logging.HttpLoggingInterceptor
import java.lang.StringBuilder

class LogInterceptor {

    fun logInterceptor():HttpLoggingInterceptor =
        HttpLoggingInterceptor(object :HttpLoggingInterceptor.Logger{
            private val mMessage = StringBuilder()

            override fun log(message: String) {
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY)

}