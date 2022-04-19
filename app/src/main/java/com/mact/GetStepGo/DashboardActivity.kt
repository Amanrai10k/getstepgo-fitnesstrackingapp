package com.mact.GetStepGo



import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import java.util.Calendar

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashboardActivity : AppCompatActivity() {
    private var SECOND_ACTIVITY_REQUEST_CODE = 0
    private lateinit var user : FirebaseAuth
    private lateinit var userDataDatabase : DatabaseReference
    override fun onBackPressed() {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        user = FirebaseAuth.getInstance()
        val url = getString(R.string.firebase_db_location)

        val today = "Today : ${getDate(1)}"  //Using type 1 : for formatted date with '/'
        tvToday.text = today
        val currentDate = getDate(0)
        val currentDay = currentDate.take(2).toInt()
        val currentMonth = currentDate.slice(2..3).toInt()
        val currentYear = currentDate.takeLast(4).toInt()
        userDataDatabase = FirebaseDatabase.getInstance(url).getReference("userData")

//        val steps = intent.getStringExtra("StepsTaken")
//
//        val stepsCount = steps?.toInt()
//
//        if (stepsCount != null) {
//            if(stepsCount>0) {
//                tvStepCount.text = steps
//            }
//        }
//        val stepTaken = tvStepCount.text.toString().toInt()
//        val calories = stepTaken * (0.00072) * 60 * 1.036
//        val distance = (stepTaken/100.00)  * 0.072
//        tvCalorieCount.text = calories.toString()
//        tvDistanceCount.text= distance.toString()

        if(user.currentUser!==null){
            user.currentUser?.let {
                val email = it.email.toString()
                val userName = emailToUserName(email)
                Log.d("currentUserAtDashboard","Welcome "+it.email.toString())

                userDataDatabase.child(userName).get().addOnSuccessListener { info ->
                    if(info.exists()){
                        val email = info.child("email").value
                        var currentSteps = info.child("currentSteps").value
                        var currentCalories = info.child("currentCalories").value
                        var currentDistance = info.child("currentDistance").value
//                        val totalSteps = info.child("totalSteps").value
//                        val totalCalories = info.child("totalCalories").value
//                        val totalDistance = info.child("totalDistance").value
                        val currentGSGCoins = info.child("currentGSGCoins").value.toString().toInt()

                        val dataDate = info.child("date").value.toString()
                        val dataDay = dataDate.take(2).toInt()
                        val dataMonth = dataDate.slice(2..3).toInt()
                        val dataYear = dataDate.takeLast(4).toInt()
                        when {
                            dataYear < currentYear -> {
                                currentSteps = 0
                                currentCalories = 0F
                                currentDistance = 0F
                            }
                            dataMonth < currentMonth -> {
                                currentSteps = 0
                                currentCalories = 0F
                                currentDistance = 0F
                            }
                            dataDay < currentDay -> {
                                currentSteps = 0
                                currentCalories = 0F
                                currentDistance = 0F
                            }
                        }
                        tvTotalSteps.text = currentSteps.toString()
                        tvTotalCalories.text = currentCalories.toString()
                        tvTotalDistance.text= currentDistance.toString()
                        tvgsgcoin.text = currentGSGCoins.toString()
                    }else{
                        Toast.makeText(applicationContext, "User Doesn't Exist", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
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



        btnStopCounting.setOnClickListener {
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
    private fun getDate(type:Int) : String{
        val c =Calendar.getInstance()
        var day = c.get(Calendar.DAY_OF_MONTH).toString()
        var month = (c.get(Calendar.MONTH) + 1).toString()

        if(day.length<2){
            day = "0$day"
        }
        if(month.length<2){
            month = "0$month"
        }
        val year = c.get(Calendar.YEAR).toString()
        var date = "$day$month$year"
        when(type){
            1 -> {date = "$day/$month/$year"}
        }

        return date
    }
    private fun emailToUserName(email : String ): String{
        var userName= email
        val regex = Regex("[^A-Za-z0-9]")
        userName = regex.replace(userName, "")
        return userName
    }

}