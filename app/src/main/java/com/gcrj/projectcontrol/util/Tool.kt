package com.gcrj.projectcontrol.util

import java.io.UnsupportedEncodingException
import java.math.BigDecimal
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by zhangxin on 2018/6/5.
 */
object Tool {

    fun getMD5Str(input: String): String? {
        try {
            val md = MessageDigest.getInstance("MD5")
            val bDigests = md.digest(input.toByteArray(charset("UTF-8")))
            return byte2hex(bDigests)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return null
    }

    fun getSHA256Str(input: String): String? {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            val bDigests = md.digest(input.toByteArray(charset("UTF-8")))
            return byte2hex(bDigests)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return null
    }

    private fun byte2hex(b: ByteArray): String {
        val hs = StringBuilder()
        var tmp: String
        for (aB in b) {
            tmp = Integer.toHexString(aB.toInt() and 0XFF)
            if (tmp.length == 1)
                hs.append("0").append(tmp)
            else
                hs.append(tmp)
        }

        return hs.toString().toLowerCase()
    }


    fun isChinese(c: Char): Boolean {
        val ub = Character.UnicodeBlock.of(c)
        return (ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
//                || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
//                || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
//                || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION
                )
    }

    //中文算2
    fun getCustomLength(charSequence: CharSequence): Int {
        var length = 0
        charSequence.forEach {
            if (isChinese(it)) {
                length += 2
            } else {
                length += 1
            }
        }

        return length
    }

    fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return "0KB"
        }

        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB"
        }

        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB"
        }

        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }

}