package com.cds.bigchildren.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.cds.bigchildren.model.bean.ConfigDataBean
import com.cds.bigchildren.model.repository.DataRepository

class MainViewModel(

    private val dataRepository: DataRepository
) : ViewModel() {

    /**
     * 获取配置信息用户信息
     */
    suspend fun getConfigData():  ConfigDataBean?{
        return dataRepository.getConfigData()
    }


}