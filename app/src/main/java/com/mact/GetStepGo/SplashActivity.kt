package com.mact.GetStepGo


import android.Manifest
import android.annotation.SuppressLint

import android.content.Intent
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splashscreen.*



@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val TAG = "PermissionDemo"
    private val RECORD_REQUEST_CODE = 101
    @RequiresApi(M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scaleup_animation)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.slidedown_animation)
        ivsplashlogo.startAnimation(scaleAnimation)
        tvsplash.startAnimation(slideAnimation)

        val physicalPer = checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION)
        val storagePer = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val locationPer = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        if(physicalPer==0 && storagePer==0 && locationPer==0){
            Handler().postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                finish()
            }, 1500) // 1500 is the delayed time in milliseconds.
        }
        else{
            val intent = Intent(this, PermissionActivity::class.java)
            startActivity(intent)
        }


    }



}
