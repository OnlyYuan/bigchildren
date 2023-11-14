package com.cds.bigchildren.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.cds.bigchildren.R
import com.cds.bigchildren.base.BaseActivity
import com.cds.bigchildren.databinding.ActivityMySplashBinding

/**
 * @param cpf
 */
class MySplashActivity : BaseActivity() {

    private lateinit var  mBinding:ActivityMySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_splash)
        initVideo()
        initLisenter()
    }

    private fun initLisenter() {
        mBinding.goLogin.setOnClickListener {
            goFun()
        }
    }

    private fun initVideo() {
        mBinding.videoView.setVideoURI (Uri.parse("android.resource://" + packageName + "/"+ R.raw.splash))
        mBinding.videoView.start()
        mBinding.videoView.setOnCompletionListener{
            goFun()
        }
    }

    private fun goFun() {
        startActivity(Intent(this@MySplashActivity,MainActivity::class.java))
        this@MySplashActivity.finish()
    }
}