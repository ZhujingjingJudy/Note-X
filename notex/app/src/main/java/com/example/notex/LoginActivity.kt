package com.example.notex

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.reflect.full.declaredMemberProperties

object UserInfoStore {
    private val _chatts = arrayListOf<UserInfo>()
    val chatts: List<UserInfo> = _chatts
    private val nFields = UserInfo::class.declaredMemberProperties.size

    private lateinit var queue: RequestQueue
    private const val serverUrl = "http://47.120.9.223:8000/"
    fun postLoginInfo(context: Context, chatt: UserInfo) {
        val jsonObj = mapOf(
            "username" to chatt.username,
            "password" to chatt.password
        )
        // Log.d("postChatt", "chatt posted!")
        // chatt.username?.let { Log.d("postChatt", it, ) }
        val postRequest = JsonObjectRequest(Request.Method.POST,
            serverUrl+"api/v1/token/", JSONObject(jsonObj),
            { response -> Log.d("successfulPostChatt", "chatt posted!${response.toString()}") },
            { error -> Log.e("errorPostChatt", error.toString())}//error.localizedMessage ?: "JsonObjectRequest error") }
        )

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(postRequest)
        Log.d("request", postRequest.toString())
    }

    fun postSignUp(context: Context, chatt: UserInfo, completion: () -> Unit) {
        val jsonObj = mapOf(
            "username" to chatt.username,
            "password" to chatt.password
        )
        // Log.d("postChatt", "chatt posted!")
        // chatt.username?.let { Log.d("postChatt", it, ) }
        val postRequest = JsonObjectRequest(Request.Method.POST,
            serverUrl+"api/v1/users/", JSONObject(jsonObj),
            { response -> Log.d("successfulPostChatt", "chatt posted!${response.toString()}") },
            { error -> Log.e("errorPostChatt", error.toString())}//error.localizedMessage ?: "JsonObjectRequest error") }
        )

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(postRequest)
        Log.d("request", postRequest.toString())
    }
}