package com.cds.bigchildren.adapter


import coil.load
import com.cds.bigchildren.R
import com.cds.bigchildren.databinding.AnswerItemBinding
import com.cds.bigchildren.model.bean.StartAnswerBean
import com.cds.bigchildren.util.getPicUrl
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class AnswerAdapter() :BaseQuickAdapter<StartAnswerBean.NodeBean,BaseDataBindingHolder<AnswerItemBinding>>(R.layout.answer_item){
    override fun convert(holder: BaseDataBindingHolder<AnswerItemBinding>, item: StartAnswerBean.NodeBean) {
        holder.dataBinding?.answer?.load(
            "http://114.255.82.226:9313/prod-api${getPicUrl(item.content?:"")}"
        )
    }

}