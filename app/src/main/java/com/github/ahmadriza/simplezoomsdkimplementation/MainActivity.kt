package com.github.ahmadriza.simplezoomsdkimplementation

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : ZoomWebinarActivity() {

    private val meetID = "83088613473"
    private val meetPass = "gNAZ10"

    private val tvStatus by lazy { findViewById<TextView>(R.id.tv_status) }
    private val btnJoin by lazy { findViewById<Button>(R.id.btn_join) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        btnJoin.setOnClickListener { joinWebinar(
                meetID, meetPass
        ) }

    }

    override fun provideUserName(): String = "Papa Joni"

    override fun provideUserID(): String  = "jonipintar@mail.com"

    override fun onSDKReady() {
        tvStatus.visibility = View.GONE
        btnJoin.visibility = View.VISIBLE
    }

    override fun onSDKInit() {
        tvStatus.visibility = View.VISIBLE
        btnJoin.visibility = View.GONE
    }
}