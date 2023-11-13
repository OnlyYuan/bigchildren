package com.cds.bigchildren.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.text.TextUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.net.Socket
import java.util.Hashtable


//连入的客户端list
@Volatile
var socketList = ArrayList<Socket>()

/**
 * 跟读总分
 */
var totalReadScore = 0

/**
 * 游戏总分
 */
var totalGameScore = 0

/**
 * 问答总分
 */
var totalAnswerScore = 0

/**
 * 电话号码和随机数据组成的
 */
var totalSessionId = "000"

//当前关卡进度
var totalCurrentLevel = 0
/**
 * 分割图片的地址 eg  img:/profile/images/v2/cat.png
 */
fun getPicUrl(path:String):String{
    val paths = path.split(":")
    return  if (paths.size>1){
        paths[1]
    }else{
        ""
    }
}

/**
 * @param  codeString：要生成二维码的字符串
 * @param  width：二维码图片的宽度
 * @param  height：二维码图片的高度
 */
fun createQRCode(codeString: String?, width: Int, height: Int): Bitmap? {
    try {
        //首先判断参数的合法性，要求字符串内容不能为空或图片长宽必须大于0
        if (TextUtils.isEmpty(codeString) || width <= 0 || height <= 0) {
            return null
        }
        //设置二维码的相关参数，生成BitMatrix（位矩阵）对象
        val hashtable = Hashtable<EncodeHintType, String?>()
        hashtable[EncodeHintType.CHARACTER_SET] = "utf-8" //设置字符转码格式
        hashtable[EncodeHintType.ERROR_CORRECTION] = "H" //设置容错级别
        hashtable[EncodeHintType.MARGIN] = "2" //设置空白边距
        //encode需要抛出和处理异常
        val bitMatrix =
            QRCodeWriter().encode(codeString, BarcodeFormat.QR_CODE, width, height, hashtable)
        //再创建像素数组，并根据位矩阵为数组元素赋颜色值
        val pixel = IntArray(width * width)
        for (h in 0 until height) {
            for (w in 0 until width) {
                if (bitMatrix[w, h]) {
                    pixel[h * width + w] = Color.BLACK //设置黑色色块
                } else {
                    pixel[h * width + w] = Color.WHITE //设置白色色块
                }
            }
        }
        //创建bitmap对象
        //根据像素数组设置Bitmap每个像素点的颜色值，之后返回Bitmap对象
        val qrcodemap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        qrcodemap.setPixels(pixel, 0, width, 0, 0, width, height)
        return qrcodemap
    } catch (e: WriterException) {
        return null
    }
}

/**
 * 正常编码中一般只会用到 [dp]/[sp] ;
 * 其中[dp]/[sp] 会根据系统分辨率将输入的dp/sp值转换为对应的px
 */
val Float.dp: Float                 // [xxhdpi](360 -> 1080)
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

val Int.dp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()


val Float.sp: Float                 // [xxhdpi](360 -> 1080)
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)


val Int.sp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()

