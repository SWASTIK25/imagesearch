package com.assignment.presentation.helpers

import android.content.Context
import android.net.Uri

import com.assignment.common.Constants

import org.apache.commons.io.IOUtils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

import javax.inject.Inject

class AppFileUtils @Inject
internal constructor() {
    fun createFileAt(context: Context, fileName: String, extension: String): File {
        val externalFilesDir = context.getExternalFilesDir(null)
        val directoryPath = (externalFilesDir
                ?: context.cacheDir).toString() + File.separator + Constants.AppConstants.FOLDER_CACHE_APP_CAPTURED
        val dir = File(directoryPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(directoryPath + File.separator + "cached_" + System.currentTimeMillis() + "." + extension)
    }


    fun getFile(context: Context, uri: Uri, des: File): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            IOUtils.copy(inputStream, FileOutputStream(des))
            IOUtils.closeQuietly(inputStream)
            return des
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }

    }

    fun getFile(url: String, params: Map<String, String>, des: File): File? {
        try {
            val inputStream = getInputStream(url, params, null)
            IOUtils.copy(inputStream, FileOutputStream(des))
            IOUtils.closeQuietly(inputStream)
            return des
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }

    }

    @Throws(IOException::class)
    private fun getInputStream(url: String, params: Map<String, String>, auth: String?): InputStream {
        val urlObject = URL(buildURL(url, params))
        val connection = urlObject.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        if (auth != null) connection.setRequestProperty("Authorization", auth)
        val responseCode = connection.responseCode
        if (responseCode != 200) {
            throw RuntimeException(connection.responseMessage)
        }

        return connection.inputStream
    }

    private fun buildURL(base: String, params: Map<String, String>): String {
        val sb = StringBuilder()
        sb.append(base)
        val sep = if (base.contains("?")) "&" else "?"
        toPostData(params, sb, sep)
        return sb.toString()
    }

    private fun toPostData(params: Map<String, String>): String {
        val sb = StringBuilder()
        toPostData(params, sb, "")
        return sb.toString()
    }

    private fun toPostData(params: Map<String, String>?, sb: StringBuilder, sep: String) {
        var sep = sep
        if (params == null) return
        for (name in params.keys) {
            sb.append(sep)
            sep = "&"
            sb.append(name)
            sb.append("=")
            try {
                sb.append(URLEncoder.encode(params[name], "UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

        }
    }



}
