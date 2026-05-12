package com.example.research02

import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class TrainingActivity : AppCompatActivity() {

    private var counter = 0
    private var lastTouchTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        val touchArea = findViewById<FrameLayout>(R.id.touchArea)
        val txtCounter = findViewById<TextView>(R.id.txtCounter)
        val btnFinish = findViewById<Button>(R.id.btnFinish)

        val mediaPlayer = MediaPlayer.create(this, R.raw.nosetap2)

        touchArea.setOnClickListener {
            val currentTime = SystemClock.elapsedRealtime()

            if (currentTime - lastTouchTime > 1000) {
                counter++
                txtCounter.text = counter.toString()

                mediaPlayer.start()

                lastTouchTime = currentTime
            }
        }

        btnFinish.setOnClickListener {

            val prefs = getSharedPreferences("training_data", MODE_PRIVATE)
            prefs.edit {

                val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date())

                val currentValue = prefs.getInt(today, 0)
                val newValue = currentValue + counter

                putInt(today, newValue)
            }

            finish()
        }
    }
}