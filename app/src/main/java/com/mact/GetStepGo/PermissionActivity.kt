package com.mact.GetStepGo


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_permission.*

class PermissionActivity : AppCompatActivity() {
    companion object {
        private const val Physical_PERMISSION_CODE = 100
        private const val STORAGE_PERMISSION_CODE = 101
        private const val LOCATION_PERMISSION_CODE = 102
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        var PhysicalPer = checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION)
        var StoragePer = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var LocationPer = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (PhysicalPer == 0 ){
            switchPhysicalPer.isChecked = true;
        }
        if (LocationPer == 0 ){
            switchLocationPer.isChecked = true;
        }
        if (StoragePer == 0 ){
            switchStoragePer.isChecked = true;
        }
        btnContinue.setOnClickListener {

            PhysicalPer = checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION)
            StoragePer = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            LocationPer = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (PhysicalPer == 0 && LocationPer == 0 && StoragePer == 0) {
                if(switchPhysicalPer.isChecked && switchLocationPer.isChecked &&switchStoragePer.isChecked ){
                    Intent(this, SplashActivity::class.java).also {
                        startActivity(it)
                        overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                        finish()
                    }
                } else{
                    val message ="Enable Permissions"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }else {
                val message ="Enable All Permissions"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            }
        }

        switchPhysicalPer?.setOnCheckedChangeListener { _, isChecked ->
            PhysicalPer = checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION)
             if (isChecked) {
                 checkPermission(
                     Manifest.permission.ACTIVITY_RECOGNITION,
                     Physical_PERMISSION_CODE)
            }

        }
        switchLocationPer?.setOnCheckedChangeListener { _, isChecked ->
             if (isChecked) {
                 checkPermission(
                     Manifest.permission.ACCESS_COARSE_LOCATION,LOCATION_PERMISSION_CODE)
            }

        }
        switchStoragePer?.setOnCheckedChangeListener { _, isChecked ->
             if (isChecked) {
                 checkPermission(
                     Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
            }
            else {
//                 val  message ="Storage Permission Is required\nCannot be disabled"
//                  switchStoragePer.isChecked= true
//                 Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }


        }


        tvManualPermission.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }

    }

    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            var grantedPerm = ""
            when (permission) {
                "android.permission.ACTIVITY_RECOGNITION" -> grantedPerm = "Physical Activity"
                "android.permission.ACCESS_COARSE_LOCATION" -> grantedPerm = "Location"
                "android.permission.WRITE_EXTERNAL_STORAGE" -> grantedPerm = "Storage"
            }
            Toast.makeText(this, "$grantedPerm Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }
    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Physical_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Physical Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Physical Permission Denied", Toast.LENGTH_SHORT).show()
                switchPhysicalPer.toggle()
                tvManualPermission.visibility = TextView.VISIBLE
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
                switchStoragePer.toggle()
                tvManualPermission.visibility = TextView.VISIBLE
            }
        }
        else if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show()
                switchLocationPer.toggle()
                tvManualPermission.visibility = TextView.VISIBLE
            }
        }
    }
}
