package com.mact.GetStepGo

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activty_startcountdown.*

class StartCountdownActivity : AppCompatActivity() {
    override fun onBackPressed() {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_startcountdown)
        // time count down for 3 seconds,
        // with 1 second as countDown interval
        object : CountDownTimer(4000, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                var counter =(millisUntilFinished / 1000)

                when (counter) {
                    3.toLong() -> {tvcountdown.text = "ON YOUR MARKS"}
                    2.toLong() -> {
                        tvcountdown.text = "GET"
                        tvcountdown.textSize = 100F
                    }
                    1.toLong() -> {tvcountdown.text = "STEP"}
                    0.toLong() -> {tvcountdown.text = "GO!"}
                }

            }

            // Callback function, fired
            // when the time is up
            override fun onFinish() {
                val it = Intent(applicationContext,  CountingActivity::class.java)
                startActivity(it)
                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                finish()
            }
        }.start()
    }
}



