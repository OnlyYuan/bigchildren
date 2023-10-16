package com.cds.bigchildren.util.net


data class ResultModel<T>(
    val msg: String?,
    val code:Int = -1,
    val data: T
) {

    companion object {
        const val CODE_SUCCESS = 200//成功
        const val CODE_TOKEN_ERROR = 428001//token失效
    }

    fun isSuccess(): Boolean {
        return code == CODE_SUCCESS
    }

    private fun getDataModelSuccess():T?{
        if (!isSuccess()){
            val errorMsg = msg
//            throw RuntimeException( if (errorMsg.isNullOrEmpty()){
//                "unknown error"
//            }else{
//                errorMsg
//            })
            throw ResultException(
                code,
                if (errorMsg.isNullOrEmpty()){
                    "unknown error"
                }else{
                    errorMsg
                }
            )
        }
        return data
    }

    /**
     * 成功返回的分页信息，否则抛异常。
     */
//    fun getPageModelIfSuccess(): PageInfo? {
//        return getDataModelSuccess()?.page_info
//    }

    /**
     * 成功返回的数据，否则抛异常。
     */
    fun getDataIfSuccess():T?{
        return getDataModelSuccess()
    }

    data class DataModel<T>(
        val data: T?
      //  val page_info: PageInfo?
    )

    data class PageInfo(
        val current_page: Int?,
        val total:Int?,
        val page_size:Int?,
    )
}