package com.mact.GetStepGo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_gsgshopnew.*

class GSGShopActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gsgshopnew)
        ibtnToDashboard.setOnClickListener {
            Intent(this,DashboardActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                finish()
            }
        }
    }
}