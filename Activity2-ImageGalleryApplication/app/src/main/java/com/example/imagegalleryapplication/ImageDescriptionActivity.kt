    package com.example.imagegalleryapplication

    import android.annotation.SuppressLint
    import android.app.DatePickerDialog
    import android.os.Bundle
    import android.view.View
    import android.widget.*
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.content.edit
    import androidx.core.content.ContextCompat
    import android.content.res.ColorStateList
    import java.util.*

    class ImageDescriptionActivity : AppCompatActivity() {

        private var rotation = 0f
        private lateinit var prefs: android.content.SharedPreferences
        private var imageId: Int = -1

        @SuppressLint("SetTextI18n", "MissingInflatedId", "UseSwitchCompatOrMaterialCode")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_image_description)

            prefs = getSharedPreferences("ImagePrefs", MODE_PRIVATE)

            val ivSelectedImage = findViewById<ImageView>(R.id.ivSelectedImage)
            val tvImageDescription = findViewById<TextView>(R.id.tvImageDescription)
            val cbFavorite = findViewById<CheckBox>(R.id.cbFavorite)
            val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
            val btnPickDate = findViewById<Button>(R.id.btnPickDate)
            val tvPickedDate = findViewById<TextView>(R.id.tvPickedDate)
            val btnRotate = findViewById<Button>(R.id.btnRotate)
            val seekBarZoom = findViewById<SeekBar>(R.id.seekBarZoom)
            val btnBack = findViewById<Button>(R.id.btnBack)
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)
            val switchTheme = findViewById<Switch>(R.id.switchTheme)
            findViewById<LinearLayout>(R.id.rootLayout)

            // Get image & set description
            imageId = intent.getIntExtra("imageId", -1)
            if (imageId != -1) {
                progressBar.visibility = View.VISIBLE
                ivSelectedImage.postDelayed({
                    ivSelectedImage.setImageResource(imageId)
                    progressBar.visibility = View.GONE
                }, 600)

                tvImageDescription.text = when (imageId) {
                    R.drawable.img1 -> "Majestic tiger roaming freely in the wild safari, showcasing its strength and beauty."
                    R.drawable.img2 -> "A sequence of three plants illustrating growth, from a small sprout to a healthy, tall plant."
                    R.drawable.img3 -> "Colorful parrot perched on a branch, displaying vibrant feathers and lively personality."
                    R.drawable.img4 -> "A purple flower with delicate petals and a bright yellow center, radiating natural beauty."
                    R.drawable.img5 -> "Close-up of a fox’s face, highlighting its sharp eyes and clever expression."
                    R.drawable.img6 -> "Bright sunflower with golden petals and a rich brown center, turning towards the sun."
                    else -> "Image Description"
                }
            }

            // Restore per-image preferences
            cbFavorite.isChecked = prefs.getBoolean("favorite_$imageId", false)
            val likedState = prefs.getInt("like_state_$imageId", -1)
            if (likedState != -1) radioGroup.check(likedState)

            val savedDate = prefs.getString("date_$imageId", null)
            if (savedDate != null) tvPickedDate.text = savedDate

            rotation = prefs.getFloat("rotation_$imageId", 0f)
            ivSelectedImage.rotation = rotation

            val savedZoom = prefs.getInt("zoom_$imageId", 100)
            seekBarZoom.progress = savedZoom
            val scale = savedZoom / 100f
            ivSelectedImage.scaleX = scale
            ivSelectedImage.scaleY = scale

            // Favorite checkbox
            cbFavorite.setOnCheckedChangeListener { _, isChecked ->
                prefs.edit { putBoolean("favorite_$imageId", isChecked) }
                Toast.makeText(
                    this,
                    if (isChecked) "Added to Favorites" else "Removed from Favorites",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Like/Dislike radio buttons
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                prefs.edit { putInt("like_state_$imageId", checkedId) }
                val msg =
                    if (checkedId == R.id.rbLike) "You liked this image" else "You disliked this image"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }

            // Date picker dialog
            btnPickDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                DatePickerDialog(this, { _, y, m, d ->
                    val picked = "Selected: $d/${m + 1}/$y"
                    tvPickedDate.text = picked
                    prefs.edit { putString("date_$imageId", picked) }
                }, year, month, day).show()
            }

            // Rotate image
            btnRotate.setOnClickListener {
                rotation += 90f
                if (rotation >= 360f) rotation = 0f
                ivSelectedImage.rotation = rotation
                prefs.edit { putFloat("rotation_$imageId", rotation) }
            }

            // Zoom with SeekBar
            seekBarZoom.max = 200
            seekBarZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val scale = progress / 100f
                    ivSelectedImage.scaleX = scale
                    ivSelectedImage.scaleY = scale
                    prefs.edit { putInt("zoom_$imageId", progress) }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    progressBar.visibility = View.VISIBLE
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    progressBar.visibility = View.GONE
                }
            })

            // Back button
            btnBack.setOnClickListener { finish() }

            // Dark mode toggle
            val isDarkMode = prefs.getBoolean("dark_mode", false)
            switchTheme.isChecked = isDarkMode
            applyDarkMode(isDarkMode)

            switchTheme.setOnCheckedChangeListener { _, checked ->
                prefs.edit { putBoolean("dark_mode", checked) }
                applyDarkMode(checked)
            }
        }

        // Function to dynamically apply dark/light mode
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        private fun applyDarkMode(enabled: Boolean) {
            val rootLayout = findViewById<LinearLayout>(R.id.rootLayout)
            val tvImageDescription = findViewById<TextView>(R.id.tvImageDescription)
            val tvPickedDate = findViewById<TextView>(R.id.tvPickedDate)
            val cbFavorite = findViewById<CheckBox>(R.id.cbFavorite)
            val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
            val switchTheme = findViewById<Switch>(R.id.switchTheme)
            val tvZoom = findViewById<TextView>(R.id.tvZoom)

            val textColor = if (enabled)
                ContextCompat.getColor(this, android.R.color.white)
            else
                ContextCompat.getColor(this, android.R.color.black)

            // Background
            val bgColor = if (enabled)
                ContextCompat.getColor(this, android.R.color.background_dark)
            else
                ContextCompat.getColor(this, android.R.color.background_light)

            rootLayout.setBackgroundColor(bgColor)

            // TextViews
            tvImageDescription.setTextColor(textColor)
            tvPickedDate.setTextColor(textColor)
            tvZoom.setTextColor(textColor)

            // CheckBox
            cbFavorite.setTextColor(textColor)
            cbFavorite.buttonTintList = ColorStateList.valueOf(textColor)

            // Switch
            switchTheme.setTextColor(textColor)
            switchTheme.thumbTintList = ColorStateList.valueOf(textColor)
            switchTheme.trackTintList = ColorStateList.valueOf(textColor)

            // RadioButtons inside RadioGroup
            for (i in 0 until radioGroup.childCount) {
                val child = radioGroup.getChildAt(i)
                if (child is RadioButton) {
                    child.setTextColor(textColor)
                    child.buttonTintList = ColorStateList.valueOf(textColor)
                }
            }
        }
    }