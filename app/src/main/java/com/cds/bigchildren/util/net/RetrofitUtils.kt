package com.cds.bigchildren.util.net

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.lang.UnsupportedOperationException
import java.util.concurrent.TimeUnit

class RetrofitUtils private constructor(){

    var mRetrofit: Retrofit? = null

    companion object{
        fun getInstance() = Holder.holder
    }

    private object  Holder{
        val holder = RetrofitUtils()
    }


    // 用于下载的工具
    private val mDownloadRetrofit: DownloadRetrofit by lazy { DownloadRetrofit() }

   fun init(httpConfig: HttpConfig, gson: Gson = Gson()){
       val okHttpClient = OkHttpClient.Builder()
           .connectTimeout(httpConfig.connectTimeOut,TimeUnit.SECONDS)
           .readTimeout(httpConfig.readTimeout,TimeUnit.SECONDS)
           .writeTimeout(httpConfig.writeTimeout,TimeUnit.SECONDS)
           .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
           .build()

       mRetrofit = Retrofit.Builder()
           .client(okHttpClient)
           .baseUrl(httpConfig.baseUrl)
           .addConverterFactory(ScalarsConverterFactory.create())
           .addConverterFactory(GsonConverterFactory.create(gson))
           .build()

       //初始化下载
       mDownloadRetrofit.init(httpConfig,gson)
   }

    /**
     * 获取自定义的api服务类实例
     */
    inline fun <reified T> getService():T{
        val retrofit = mRetrofit?:throw UnsupportedOperationException("must call init() method first")
        return retrofit.create(T::class.java)
    }

    /**
     * 下载文件
     *
     * @param url               下载地址。可以是完整路径或者子路径(如果在RequestConfig配置过)
     * @param downloadFile      下载的文件缓存
     * @param threadCount       分成几个子文件进行下载。默认为1，表示不分割。
     * @param deleteCache       下载之前是否删除已经下载的文件缓存，默认为false
     * @param callbackInterval  数据的发送频率限制，防止下载时发送数据过快，默认200毫秒
     */
    fun downloadFile(
        url: String,
        downloadFile: File,
        threadCount: Int = 1,
        deleteCache: Boolean = false,
        callbackInterval: Long = 200L
    ): Flow<DownloadInfo> = mDownloadRetrofit.downloadFile(
        url, downloadFile, threadCount, deleteCache, callbackInterval
    )

}