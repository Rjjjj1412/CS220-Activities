package com.example.imagegalleryapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.etUsername)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val cbRemember = findViewById<CheckBox>(R.id.cbRemember)

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        //Restore saved credentials if "remember me" was checked
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)
        if (isRemembered) {
            val savedUser = sharedPreferences.getString("username", "")
            val savedPass = sharedPreferences.getString("password", "")
            username.setText(savedUser)
            password.setText(savedPass)
            cbRemember.isChecked = true
        }

        btnLogin.setOnClickListener {
            val inputUser = username.text.toString()
            val inputPass = password.text.toString()

            if (inputUser == "admin123" && inputPass == "admin123") {
                if (cbRemember.isChecked) {
                    // Save user, pass and rememberMe flag
                    sharedPreferences.edit {
                        putBoolean("rememberMe", true)
                        putString("username", inputUser)
                        putString("password", inputPass)
                    }
                } else {
                    // Clear all saved values
                    sharedPreferences.edit {
                        clear()
                    }
                }

                startActivity(Intent(this, GalleryActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid login", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
