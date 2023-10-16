package com.cds.bigchildren

import android.app.Application
import com.cds.bigchildren.util.di.appModule
import com.cds.bigchildren.util.net.HttpConfig
import com.cds.bigchildren.util.net.RetrofitUtils
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class  MyApplication :Application() {

    companion object{
       //private const val BASE_URL = "http://117.50.160.104:11001/"
        //private const val BASE_URL = "https://vrbt.service.aidcstore.net"
        private const val BASE_URL = "http://114.255.82.226:9312"
    }

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger(Level.ERROR)
            androidContext(this@MyApplication)
            modules(appModule)
        }

        RetrofitUtils.getInstance().init(HttpConfig(baseUrl = BASE_URL))

    }
}