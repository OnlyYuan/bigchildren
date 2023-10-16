package com.cds.bigchildren.model.repository

import com.cds.bigchildren.model.bean.ConfigDataBean
import com.cds.bigchildren.model.datasource.GetConfigDatasource


/**
 * 全局数据数据仓库
 */
class DataRepository(
    private val getConfigDatasource: GetConfigDatasource
) {


    /**
     * 获取全局数据
     *
     */
    suspend fun getConfigData(): ConfigDataBean?{
        return  getConfigDatasource.load()
    }

}