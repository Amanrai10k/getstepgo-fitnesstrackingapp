package com.mact.GetStepGo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(){
    private lateinit var userDataDatabase : DatabaseReference
    private lateinit var database : DatabaseReference
    private lateinit var user : FirebaseAuth

    override fun onBackPressed() {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val url = getString(R.string.firebase_db_location)
        user = FirebaseAuth.getInstance()
        userDataDatabase = FirebaseDatabase.getInstance(url).getReference("userData")
        database = FirebaseDatabase.getInstance(url).getReference("users")
        if(user.currentUser!=null){
            user.currentUser?.let {
                Log.d("userCurrent",it.email.toString())
                val email = it.email.toString()
                val userName = emailToUserName(email)
                database.child(userName).get().addOnSuccessListener {info ->
                    if(info.exists()){
                        val userFName = info.child("fname").value.toString()
                        tvUsername.text = userFName
                    }
                    else{
                        Toast.makeText(applicationContext, "UserName Doesn't Exist", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
                userDataDatabase.child(userName).get().addOnSuccessListener { info ->
                    if(info.exists()){
                        val userEmail = info.child("email").value

                        val totalSteps = info.child("totalSteps").value
                        val totalCalories = info.child("totalCalories").value
                        val totalDistance = info.child("totalDistance").value
                        val totalGSGCoins = info.child("totalGSGCoins").value

                        tvTotalCalories.text = totalCalories.toString()
                        tvTotalDistance.text = totalDistance.toString()
                        tvTotalSteps.text = totalSteps.toString()
                        tvTotalGSGCoins.text = totalGSGCoins.toString()
                    }else{
                        Toast.makeText(applicationContext, "User Doesn't Exist", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        btnToDashboard.setOnClickListener{
            finish()
            overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
        }
        btnLogout.setOnClickListener{
            Log.d("userCurrentAtProfile", user.currentUser?.email.toString()+" has Logged Out")
            user.signOut()
            Intent(this,LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                finish()
            }
        }

    }
    private fun emailToUserName(email : String ): String{
        var userName= email
        val regex = Regex("[^A-Za-z0-9]")
        userName = regex.replace(userName, "")
        return userName
    }
}