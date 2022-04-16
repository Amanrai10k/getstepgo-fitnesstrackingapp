package com.mact.GetStepGo

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_counting.*
import java.util.*

class CountingActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var user : FirebaseAuth
    private lateinit var userDataDatabase : DatabaseReference
    private lateinit var database : DatabaseReference
    // Added SensorEventListener the MainActivity class
    // Implement all the members in the class Counting Activity
    // after adding SensorEventListener

    // we have assigned sensorManger to nullable
    private var sensorManager: SensorManager? = null

    // Creating a variable which will give the running status
    // and initially given the boolean value as false
    private var running = false

    // Creating a variable which will counts total steps
    // and it has been given the value of 0 float
    private var totalSteps = 0

    // Creating a variable which counts previous total
    // steps and it has also been given the value of 0 float
    private var previousTotalSteps = 0

    var count =0

    override fun onBackPressed() {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counting)
        user = FirebaseAuth.getInstance()
        val currentDate = getDate()
        val currentDay = currentDate.take(2).toInt()
        val currentMonth = currentDate.slice(2..3).toInt()
        val currentYear = currentDate.takeLast(4).toInt()

        val url = getString(R.string.firebase_db_location)
        userDataDatabase = FirebaseDatabase.getInstance(url).getReference("userData")
        database = FirebaseDatabase.getInstance(url).getReference("users")
        var totalSteps = 0
        var totalCalories = 0F
        var totalDistance = 0F
        var currentSteps  = 0
        var currentCalories: Float
        var currentDistance: Float
        var height : Int
        var weight = 60
        var currentGSGCoins  = 0
        var totalGSGCoins = 0
        var dataDay  = 0
        var dataMonth  = 0
        var dataYear  = 0
        var dataDate : String
        loadData()
        resetSteps()

        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if(user.currentUser!==null){
            user.currentUser?.let {
                val email = it.email.toString()
                val userName = emailToUserName(email)
                database.child(userName).get().addOnSuccessListener { info ->
                    if(info.exists()){
                        weight = info.child("weight").value.toString().toInt()
                        height = info.child("height").value.toString().toInt()
//                      Age = info.child("currentDistance").value.toString().toInt()

                    }else{
                        Toast.makeText(applicationContext, "User Doesn't Exist", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
                userDataDatabase.child(userName).get().addOnSuccessListener { info ->
                    if(info.exists()){
                        currentSteps = info.child("currentSteps").value.toString().toInt()
                        currentCalories = info.child("currentCalories").value.toString().toFloat()
                        currentDistance = info.child("currentDistance").value.toString().toFloat()
                        currentGSGCoins = info.child("currentGSGCoins").value.toString().toInt()
                        totalSteps = info.child("totalSteps").value.toString().toInt()
                        totalCalories = info.child("totalCalories").value.toString().toFloat()
                        totalDistance = info.child("totalDistance").value.toString().toFloat()
                        totalGSGCoins = info.child("totalGSGCoins").value.toString().toInt()
                        totalGSGCoins = info.child("totalGSGCoins").value.toString().toInt()
                        dataDate = info.child("date").value.toString()
                        dataDay = dataDate.take(2).toInt()
                        dataMonth = dataDate.slice(2..3).toInt()
                        dataYear = dataDate.takeLast(4).toInt()
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
                    }else{
                        Toast.makeText(applicationContext, "User Doesn't Exist", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnStopCounting.setOnClickListener(){
            Log.d(tvStepsTaken.text.toString(),"Total Steps")
//            val steps = totalSteps.toInt()
            val steps = tvStepsTaken.text.toString().toInt()
            val calories = (steps * (0.00072) * weight * 1.036).toFloat()
            val distance = ((steps/100.00)  * 0.072).toFloat()
            currentSteps += steps
            currentCalories =(currentSteps * (0.00072) * weight * 1.036).toFloat()
            currentDistance = ((currentSteps/100.00)  * 0.072).toFloat()
            if(currentSteps > 1000){
                currentGSGCoins += currentSteps / 1000
            }
                    totalSteps += steps
                    totalCalories += calories
                    totalDistance += distance
                    totalGSGCoins= currentGSGCoins

                previousTotalSteps = totalSteps

                // When the user will click long tap on the screen,
                // the steps will be reset to 0
                tvStepsTaken.text = 0.toString()

                // This will save the data
                saveData()
            if(user.currentUser!==null) {
                user.currentUser?.let {
                    val email = it.email.toString()
                    val userName = emailToUserName(email)
                    val userData = UserData(email,currentSteps,currentCalories,currentDistance,currentGSGCoins,totalSteps,totalCalories,totalDistance,totalGSGCoins,currentDate)
                    userDataDatabase.child(userName).setValue(userData).addOnSuccessListener {
                        Log.d("Counting", "Successfully Uploaded Data")
                    }.addOnFailureListener {
                        Log.d("Counting", "Failed TO Upload Data")
                    }
                }
            }
            val intent = Intent(this, DashboardActivity::class.java)
//            intent.putExtra("StepsTaken",steps)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
            finish()
        }

        btnMaps.setOnClickListener(){
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
        }

    }
    private fun emailToUserName(email : String ): String{
        var userName= email
        val regex = Regex("[^A-Za-z0-9]")
        userName = regex.replace(userName, "")
        return userName
    }
    private fun getDate() : String{
        val c =Calendar.getInstance()
        var day = c.get(Calendar.DAY_OF_MONTH).toString()
        var month =  (c.get(Calendar.MONTH) + 1).toString()
        if(day.length<2){
            day = "0$day"
        }
        if(month.length<2){
            month = "0$month"
        }
        val year = c.get(Calendar.YEAR).toString()
        val date = "$day$month$year"
        return date
    }

    override fun onResume() {
        super.onResume()
        running = true

        // Returns the number of steps taken by the user since the last reboot while activated
        // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
        // So don't forget to add the following permission in AndroidManifest.xml present in manifest folder of the app.
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView



        if (running) {

            totalSteps = event!!.values[0].toInt()

            if(count==0){
                previousTotalSteps=totalSteps
                count++
            }
            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            Log.d("Pre", "$previousTotalSteps")
            Log.d("Total", "$totalSteps")
            // It will show the current steps to the user
            tvStepsTaken.text = ("$currentSteps")
        }
    }

    fun resetSteps() {

        tvStepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        tvStepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            tvStepsTaken.text = 0.toString()

            // This will save the data
            saveData()

            true
        }
    }

    private fun saveData() {

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putInt("key1", previousTotalSteps)
        Log.d("SaveMainActivity", "$previousTotalSteps")
        Log.d("SaveMainActivity", "$totalSteps")

        editor.apply()
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getInt("key1", 0)

        // Log.d is used for debugging purposes
        Log.d("LoadMainActivity", "$savedNumber")
        Log.d("LoadTotalMainActivity", "$totalSteps")


        previousTotalSteps = savedNumber

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }

}

