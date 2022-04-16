package com.mact.GetStepGo

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.tvbtnToSignUp


class LoginActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    private lateinit var user : FirebaseAuth
    private var count = 0
    override fun onBackPressed() {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slidedown_animation)
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slideup_animation)
        tvHead.startAnimation(slideDownAnimation)
        ivHeadBack.startAnimation(slideDownAnimation)
        tvHaveAcc.startAnimation(slideUpAnimation)
        tvbtnToSignUp.startAnimation(slideUpAnimation)
        user = FirebaseAuth.getInstance()
        if(user.currentUser!= null){
            user.currentUser?.let {
                if(user.currentUser?.isEmailVerified == true){
                    Log.d("currentUserAtLogin",it.email.toString()+" has logged in")
                    Intent(this,DashboardActivity::class.java).also {newIt->
                        startActivity(newIt)
                        overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                        finish()
                    }
                }else{

                }
            }
        }

        tvbtnToSignUp.setOnClickListener{
            Intent(this,SingUpActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                finish()
            }
        }
        ibtnLogin.setOnClickListener {
            if(validateEmail()&&validatePassword()){
                loginUser()
            }
        }

    }
    private fun showDialog(activity: Activity?, msg: String?) {
        val dialog = Dialog(activity!!, R.style.AppTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.loadingscreen)
        dialog.show()
    }
    private fun loginUser () {
        val email = etEmailAddress.text.toString()
        val password = etpassword.text.toString()

        user.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){

                    beginLogIn()
                } else{
                    Toast.makeText(
                        this,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

    }

    private fun beginLogIn() {
        Handler().postDelayed({
            if(user.currentUser?.isEmailVerified == true){
                showDialog(this,"Login Successful")
                tvAlert.text = null
                Log.d("currentUserAtLogin", user.currentUser?.email.toString()+" has logged in")
                val url = getString(R.string.firebase_db_location)
                val email = etEmailAddress.text.toString()
                val password = etpassword.text.toString()
                val userName = emailToUserName(email)
                database = FirebaseDatabase.getInstance(url).getReference("users")
                database.child(userName).get().addOnSuccessListener {
                    if(it.exists()){
                        val userEmail = it.child("email").value
                        val userPass = it.child("password").value
                        val userFName = it.child("fName").value.toString()
                        Log.d("user",user.currentUser.toString())
                        if(userEmail==email&&userPass==password){
                            Intent(this,DashboardActivity::class.java).also {newIt->
                                startActivity(newIt)
                                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                                finish()
                            }
                        }else{
                            Toast.makeText(applicationContext, "Wrong Credentials", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(applicationContext, "User Doesn't Exist", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                val alert = "Email Verification Pending!"
                tvAlert.text = alert
                Log.d("currentUserAtLogin", user.currentUser?.email.toString()+" has not verified his email address")
            }
        }, 1500) // 1500 is the delayed time in milliseconds.
    }

    private fun validateEmail() : Boolean{
        val email = etEmailAddress.text.toString().trim()
        val emailRegex : Regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$".toRegex()
        if (email.isEmpty()){
            etEmailAddress.error = "Enter Your Email"
            return false
        }
        else if(!email.matches(emailRegex)){
            etEmailAddress.error = "Invalid Email Address"
            return false
        }
        else
        {
            etEmailAddress.error=null
            return true
        }
    }
    private fun validatePassword() : Boolean{
        val password = etpassword.text.toString().trim()

        if (password.isEmpty()){
            etpassword.error = "Enter Your Password"
            return false
        }
        else{
            etpassword.error = null
            return true
        }
    }
    private fun emailToUserName(email : String ): String{
        var userName= email
        val regex = Regex("[^A-Za-z0-9]")
        userName = regex.replace(userName, "")
        return userName
    }

}