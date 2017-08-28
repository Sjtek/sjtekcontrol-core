package nl.sjtek.control.core.net

import okhttp3.OkHttpClient

object HttpClient {

    val client = OkHttpClient.Builder().build()
}