package com.cds.bigchildren.common.custom

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.cds.bigchildren.R
import com.cds.bigchildren.databinding.ActivityTransitionBinding
import com.cds.bigchildren.model.bean.ConfigDataBean
import com.gyf.immersionbar.ktx.immersionBar

/**
 * 过渡页
 */
class TransitionDialog : DialogFragment() {

    private var curProcess = 0 //当前进度
    private lateinit var mBinding:ActivityTransitionBinding
    private var curContent: ConfigDataBean.ContentData?=null //配置数据
    private var toActivity = ""//要到的activity
    private var goNextActivity:(()->Unit)?=null
    private var isTrue = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.FullScreenDialog)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //StatusBarUtil.hideFakeStatusBarView(this)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_transition,container,false)
        immersionBar {
            transparentStatusBar()
            transparentNavigationBar()
            statusBarDarkFont(true)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        initData()
    }

    private fun initData() {

        mBinding.img.viewTreeObserver.addOnWindowFocusChangeListener {
            startProgressMove()
        }
    }

    override fun onStart() {
        super.onStart()

        if (dialog != null) {
            dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
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



    private  fun startProgressMove(){
        translateAnimation()
    }

    private fun translateAnimation(){
        val toX = mBinding.progress.width*1.0f -mBinding.img.width/2
        val objectAnimator = ObjectAnimator.ofFloat(mBinding.img,"translationX",toX)
        objectAnimator.duration = 2000
        objectAnimator.repeatCount = 0
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.addUpdateListener {
            curProcess = (it.currentPlayTime).toInt()
            mBinding.progress.progress = curProcess
        }
        objectAnimator.addListener(object:AnimatorListener{
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {

                if (isTrue){
                    isTrue = false
                    Log.i("11","------->qweqweqwe")
                    goNextActivity?.invoke()
                }


            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }

        })
        objectAnimator.start()
    }

    /**
     * 过渡画面结束
     */
    fun setGoNextFun(goNext:()->Unit ){
        goNextActivity = goNext
    }

    override fun onStop() {
        super.onStop()
        dismissAllowingStateLoss()
    }
}