package com.cds.bigchildren.ui

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.cds.bigchildren.R
import com.cds.bigchildren.base.BaseActivity
import com.cds.bigchildren.common.custom.QrcodeDialog
import com.cds.bigchildren.common.route.mainToucSummaryBtn
import com.cds.bigchildren.common.route.mainTouchAnswerBtn
import com.cds.bigchildren.common.route.mainTouchReadBtn
import com.cds.bigchildren.common.route.mainTouchStoryBtn
import com.cds.bigchildren.databinding.ActivityMainBinding
import com.cds.bigchildren.model.bean.ConfigDataBean
import com.cds.bigchildren.model.bean.SocketRoute
import com.cds.bigchildren.service.SocketService
import com.cds.bigchildren.util.MyServerSocket
import com.cds.bigchildren.util.SharedPreferencesUtils
import com.cds.bigchildren.util.net.DataHandler
import com.cds.bigchildren.util.totalSessionId
import com.cds.bigchildren.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.InetAddress

class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBinding:ActivityMainBinding
    private var qrcodeDialog:QrcodeDialog?=null
    private val mViewModel: MainViewModel by viewModel()
    private val contentDataList = ArrayList<ConfigDataBean.ContentData>()//关卡list
    private var totalCount  = 0 //总关卡
    private var curPosition = 0//关卡位置

    private val summaryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (
            it.data?.getBooleanExtra("next",false) == true
        ){
            changeTagFun(1)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        getLocalIPAddress()
        startSocket()
        getConfigDataFun()
        initView()
        initListener()
    }

    private fun initListener() {
        mBinding.storyBtn.setOnClickListener(this)
        mBinding.redBtn.setOnClickListener(this)
        mBinding.answerBtn.setOnClickListener(this)
    }

    private fun initView() {
        mBinding.nickName.text = (SharedPreferencesUtils.getValue(this,
            SharedPreferencesUtils.UserInfo,
            SharedPreferencesUtils.NICK_NAME,
            ""))?:""

    }

    override fun onClick(v: View?) {
        when(v){
            mBinding.storyBtn->{
                val intent = Intent(this@MainActivity,StoryActivity::class.java)
                intent.putExtra("curContent",contentDataList[curPosition])
                startActivity(intent)
            }
            mBinding.redBtn->{
                val intent = Intent(this@MainActivity,ReadActivity::class.java)
                intent.putExtra("curContent",contentDataList[curPosition])
                startActivity(intent)

            }

            mBinding.answerBtn->{
                val intent = Intent(this@MainActivity,QuestionActivity::class.java)
                intent.putExtra("curContent",contentDataList[curPosition])
                startActivity(intent)
            }
        }
    }

    private fun startSocket(){

        startService(Intent(this,SocketService::class.java))
    }



    private fun getLocalIPAddress(){
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager!=null){
            val wifiInfo = wifiManager.connectionInfo
            val intaddr = wifiInfo.ipAddress
            val byteaddr = byteArrayOf(
                (intaddr and 0xff).toByte(),
                (intaddr shr 8 and 0xff).toByte(), (intaddr shr 16 and 0xff).toByte(),
                (intaddr shr 24 and 0xff).toByte()
            )
            var addr: InetAddress? = null
            try {
                addr = InetAddress.getByAddress(byteaddr)
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
            val mobileIp: String = addr?.hostAddress ?: ""
            if (mobileIp.isNotEmpty()){
                generateQrcode(mobileIp)
            }

            Toast.makeText(this,"ip:$mobileIp",Toast.LENGTH_SHORT).show()
            Log.i("11","ip: ${mobileIp}")
        }

    }


    /**
     * 生成二维码
     */
    private fun generateQrcode(ipString: String){
        qrcodeDialog = QrcodeDialog(ipString)
        qrcodeDialog?.show(supportFragmentManager,"dialog")

    }


    /**
     * 获取数据信息
     */
    private fun getConfigDataFun(){

        lifecycleScope.launch {
            DataHandler.performCollect(
                this@MainActivity,
                block = {
                    mViewModel.getConfigData()
                },
                onError = {
                    Log.i("11","--->失败")

                },
                onSuccess = {
                    it?.contentList?.let { it1->
                        contentDataList.addAll(it1)
                        totalCount = it1.size
                        showTagUi()
                    }
                }
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMsg(msg: String){
        var msgArray = msg.split("?")
        when(msgArray[0]){
            "connect"-> {//链接
                if (qrcodeDialog!=null){
                    qrcodeDialog?.dismiss()
                }
                if (msgArray.size>1){
                    mBinding.nickName.text = msgArray[1].toString()
                }

                if (msgArray.size>2){
                    totalSessionId = msgArray[2].toString()
                }
            }

            mainTouchStoryBtn->{//故事
                val intent  = Intent(this@MainActivity,StoryActivity::class.java)
                intent.putExtra("curContent",contentDataList[curPosition])
                startActivity(intent)
            }

            mainTouchReadBtn->{//读故事
                val  intent = Intent(this@MainActivity,ReadActivity::class.java)
                intent.putExtra("curContent",contentDataList[curPosition])
                startActivity(intent)
            }

            mainTouchAnswerBtn->{//问答
                val intent = Intent(this@MainActivity,QuestionActivity::class.java)
                intent.putExtra("curContent",contentDataList[curPosition])
                startActivity(intent)
            }

            mainToucSummaryBtn->{//总结页面
                val intent = Intent(this@MainActivity,SummaryActivity::class.java)
                intent.putExtra("curContent",contentDataList[curPosition])
                summaryLauncher.launch(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 切换标签
     * -1 上一个   1 下一个
     */
    private fun changeTagFun(position:Int){
        if (position+curPosition<0||position+curPosition>=totalCount){
            Toast.makeText(this@MainActivity,"没有更多了！",Toast.LENGTH_SHORT).show()
        }else{
            curPosition += position
            showTagUi()
        }

    }

    /**
     * 展示标签ui
     */
    private fun showTagUi(){
        mBinding.contentName.text = contentDataList[curPosition].name ?: ""
    }

}
