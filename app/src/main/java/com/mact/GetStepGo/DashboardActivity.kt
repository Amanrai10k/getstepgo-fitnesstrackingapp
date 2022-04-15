package com.mact.GetStepGo



import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*



class DashboardActivity : AppCompatActivity() {
    private var SECOND_ACTIVITY_REQUEST_CODE = 0
    private lateinit var user : FirebaseAuth

    override fun onBackPressed() {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        user = FirebaseAuth.getInstance()
        if(user.currentUser!==null){
            user.currentUser?.let {
                Log.d("currentUserAtDashboard","Welcome "+it.email.toString())
                }
            }

//        val startDate = Calendar.getInstance()
//        startDate.add(Calendar.MONTH, -1)
//
//        val endDate = Calendar.getInstance()
//        endDate.add(Calendar.MONTH, 0)

//        val horizontalCalendar = HorizontalCalendar.Builder(this, R.id.cvDateSelecter)
//            .range(startDate, endDate)
//            .datesNumberOnScreen(5)
//            .build()
//
//        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {
//            override fun onDateSelected(date: Calendar?, position: Int) {
//                //do something
//            }
//        }
        val steps = intent.getStringExtra("StepsTaken")

        val stepsCount = steps?.toInt()

        if (stepsCount != null) {
            if(stepsCount>0) {
                tvStepCount.text = steps
            }
        }
        val stepTaken = tvStepCount.text.toString().toInt()
        val calories = stepTaken * (0.00072) * 60 * 1.036
        tvCalorieCount.text = calories.toString()
        val distance = (stepTaken/100.00)  * 0.072
        tvDistanceCount.text= distance.toString()


        btnBack.setOnClickListener {
            val intent = Intent(this, StartCountdownActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
        }

        icGSGCoin.setOnClickListener{
            val intent = Intent(this, GSGShopActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
        }
        btnprofile.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
        }

//        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//            super.onActivityResult(requestCode, resultCode, data)
//            tvStepCount.text = "100"
//            var result = intent.getCharSequenceExtra("result")
//            tvStepCount.text = result
//            if (requestCode == 0) {
//                if (resultCode == RESULT_OK) {0
//                    // Get the result from intent
//
//
//                    // set the result to the text view
//
//
//                }
//            }
//        }

    }

}