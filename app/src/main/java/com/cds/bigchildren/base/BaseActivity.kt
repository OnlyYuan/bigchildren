package com.cds.bigchildren.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import org.greenrobot.eventbus.EventBus

/**
 * @author cpf
 * @date 2021/12/31
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        //StatusBarUtil.hideFakeStatusBarView(this)
          immersionBar {
              transparentStatusBar()
              statusBarDarkFont(true)
          }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}