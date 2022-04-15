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
import kotlinx.android.synthetic.main.activity_counting.*

class CountingActivity : AppCompatActivity(), SensorEventListener {
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

        loadData()
        resetSteps()

        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager


        btnBack.setOnClickListener(){
            Log.d(tvStepsTaken.text.toString(),"Total Steps")
//            val steps = totalSteps.toInt()
            val steps = tvStepsTaken.text
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("StepsTaken",steps)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)

                previousTotalSteps = totalSteps

                // When the user will click long tap on the screen,
                // the steps will be reset to 0
                tvStepsTaken.text = 0.toString()

                // This will save the data
                saveData()

            finish()
        }
        btnMaps.setOnClickListener(){
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
        }

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

