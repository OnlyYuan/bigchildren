package com.cds.bigchildren.model.datasource

import com.cds.bigchildren.model.api.ApiService
import com.cds.bigchildren.util.net.RetrofitUtils
import com.cds.bigchildren.model.bean.ConfigDataBean


/**
 * 获取全局数据
 */
class GetConfigDatasource {

    /**
     * 获取全局数据
     */
    suspend fun load(): ConfigDataBean? {

          return  RetrofitUtils
                .getInstance()
                .getService<ApiService>()
                .getConfigData()
                .getDataIfSuccess()
    }

}