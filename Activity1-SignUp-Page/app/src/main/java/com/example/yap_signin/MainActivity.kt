package com.example.yap_signin

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var passwordsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        // Views
        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val rgGender = findViewById<RadioGroup>(R.id.rgGender)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val cbShowPasswords = findViewById<CheckBox>(R.id.cbShowPasswords)
        val etBirthdate = findViewById<EditText>(R.id.etBirthdate)
        val btnPickDate = findViewById<Button>(R.id.btnPickDate)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        // Date picker (Calendar)
        val openDatePicker = {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    val mm = String.format(Locale.US, "%02d", m + 1)
                    val dd = String.format(Locale.US, "%02d", d)
                    etBirthdate.setText(String.format(Locale.US, "%04d-%s-%s", y, mm, dd))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        etBirthdate.setOnClickListener { openDatePicker() }
        btnPickDate.setOnClickListener { openDatePicker() }

        // Show/Hide passwords (affects both fields)
        cbShowPasswords.setOnCheckedChangeListener { _, isChecked ->
            passwordsVisible = isChecked
            if (isChecked) {
                etPassword.transformationMethod = null
                etConfirmPassword.transformationMethod = null
                etPassword.inputType = InputType.TYPE_CLASS_TEXT
                etConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                etConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                etConfirmPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            // Move cursor to end after toggling
            etPassword.setSelection(etPassword.text?.length ?: 0)
            etConfirmPassword.setSelection(etConfirmPassword.text?.length ?: 0)
        }

        // Sign Up (basic validation for demo)
        btnSignUp.setOnClickListener {
            val fullName = etFullName.textString()
            val email = etEmail.textString()
            val username = etUsername.textString()
            val pass = etPassword.textStringRaw()
            val confirm = etConfirmPassword.textStringRaw()
            val birthdate = etBirthdate.textString()

            val genderId = rgGender.checkedRadioButtonId
            val gender = if (genderId != -1)
                findViewById<RadioButton>(genderId).text.toString()
            else
                ""

            // Validate
            if (fullName.isEmpty()) return@setOnClickListener etFullName.showError("Full name is required")
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
                return@setOnClickListener etEmail.showError("Valid email is required")
            if (gender.isEmpty()) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (username.isEmpty()) return@setOnClickListener etUsername.showError("Username is required")
            if (pass.length < 6) return@setOnClickListener etPassword.showError("Min 6 characters")
            if (confirm != pass) return@setOnClickListener etConfirmPassword.showError("Passwords do not match")
            if (birthdate.isEmpty()) return@setOnClickListener etBirthdate.showError("Pick your birthdate")

            // Demo success
            Toast.makeText(
                this,
                "Signed up:\n$fullName • $email • $gender • $username • $birthdate",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // --- Small helpers ---
    private fun EditText.textString(): String = text.toString().trim()
    private fun EditText.textStringRaw(): String = text.toString()
    private fun EditText.showError(msg: String) {
        error = msg
        requestFocus()
    }
}
