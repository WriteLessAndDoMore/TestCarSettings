package com.xiangyang.testcarsettings.util

import android.util.Log

object LogUtils {
    private const val GLOBAL_TAG_PREFIX = "CarSettings_"
    private var isLogEnabled = true
    private const val MAX_LOG_LENGTH = 3000
    fun v(msg: String, tag: String? = null) {
        if (!isLogEnabled) return
        printLog(Log.VERBOSE, getFinalTag(tag), msg)
    }

    fun d(msg: String, tag: String? = null) {
        if (!isLogEnabled) return
        printLog(Log.DEBUG, getFinalTag(tag), msg)
    }

    fun i(msg: String, tag: String? = null) {
        if (!isLogEnabled) return
        printLog(Log.INFO, getFinalTag(tag), msg)
    }

    fun w(msg: String, tag: String? = null, tr: Throwable? = null) {
        if (!isLogEnabled) return
        printLog(
            Log.WARN,
            getFinalTag(tag),
            "$msg\n${tr?.let { Log.getStackTraceString(it) } ?: ""}")
    }

    fun e(msg: String, tag: String? = null, tr: Throwable? = null) {
        if (!isLogEnabled) return
        printLog(
            Log.ERROR,
            getFinalTag(tag),
            "$msg\n${tr?.let { Log.getStackTraceString(it) } ?: ""}")
    }

    private fun printLog(level: Int, finalTag: String, msg: String) {
        val stackTrace = Thread.currentThread().stackTrace
        val targetElement = stackTrace.getOrNull(5)
        val metaInfo = if (targetElement != null) {
            "${targetElement.fileName}:${targetElement.lineNumber}:${targetElement.methodName}"
        } else {
            ""
        }
        val fullMessage = "$metaInfo$msg"
        if (fullMessage.length > MAX_LOG_LENGTH) {
            var i = 0
            while (i < fullMessage.length) {
                val end = minOf(i + MAX_LOG_LENGTH, fullMessage.length)
                Log.println(level, finalTag, fullMessage.substring(i, end))
                i += MAX_LOG_LENGTH
            }
        } else {
            Log.println(level, finalTag, fullMessage)
        }
    }

    private fun getFinalTag(tag: String?): String {
        if (!tag.isNullOrBlank()) {
            return "$GLOBAL_TAG_PREFIX$tag"
        }
        val stackTrace = Thread.currentThread().stackTrace
        val targetElement = stackTrace.getOrNull(5)
        val className = targetElement?.fileName?.substringBefore(".") ?: "Unknown"
        return "$GLOBAL_TAG_PREFIX$className"
    }
}