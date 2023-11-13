package com.cds.bigchildren.common.custom

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.cds.bigchildren.R
import com.cds.bigchildren.databinding.DialogQrcodeBinding
import com.cds.bigchildren.util.createQRCode
import com.cds.bigchildren.util.dp


/**
 * 原因弹窗
 * @auth cpf
 * @date 2022/7/26
 */
class QrcodeDialog(
    private var ipString: String
):DialogFragment() {

    private lateinit var mBinding:DialogQrcodeBinding
    var onReasonListener:OnReasonListener?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.dialog_qrcode,container,false)
        initListener()
        initView()
        return mBinding.root
    }

    private fun initView() {
        mBinding.qrCode.setImageBitmap(createQRCode(ipString,200.dp, 200.dp))
    }

    override fun onResume() {
        super.onResume()
        onReasonListener?.onResume()
    }

    override fun onStart() {
        super.onStart()
        val win = dialog?.window
        win?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dm = resources.displayMetrics
        val params = win?.attributes
        params?.gravity = Gravity.CENTER
        params?.width = 350.dp
        params?.height = 300.dp
        win?.attributes= params
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setOnKeyListener(object : DialogInterface.OnKeyListener{
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    return true
                }
                return false
            }
        })
    }


    private fun initListener() {

    }

    interface OnReasonListener{
        fun  onCancel()

        fun  onConfirm()

        fun  onResume()
    }

}