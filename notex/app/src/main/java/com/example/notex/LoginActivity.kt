package com.example.notex

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
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
    fun postLoginInfo(context: Context, chatt: UserInfo, onSuccess: () -> Unit, onFail: () -> Unit) {
        val jsonObj = mapOf(
            "username" to chatt.username,
            "password" to chatt.password
        )

        val postRequest = JsonObjectRequest(Request.Method.POST,
            serverUrl+"api/v1/token/", JSONObject(jsonObj),
            { response ->
                Log.d("successfulSignIn", "chatt posted!${response.toString()}")
                onSuccess()
            },
            { error ->
                Log.e("errorSignIn", error.toString())
                onFail()
            }
        )

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(postRequest)
        Log.d("request", postRequest.toString())
    }

    fun postSignUp(context: Context, chatt: UserInfo, onSuccess: () -> Unit, onFail: () -> Unit) {
        val jsonObj = mapOf(
            "username" to chatt.username,
            "password" to chatt.password,
            "email" to chatt.email
        )

        val postRequest = JsonObjectRequest(Request.Method.POST,
            serverUrl+"api/v1/users/", JSONObject(jsonObj),
            { response ->
                Log.d("successfulSignUp", "Sign_up${response.toString()}")
                onSuccess()
            },
            { error ->
                Log.e("errorSignUp", error.toString())
                onFail()
            }
        )

        if (!this::queue.isInitialized) {
            queue = newRequestQueue(context)
        }
        queue.add(postRequest)
        Log.d("request", postRequest.toString())
    }
}