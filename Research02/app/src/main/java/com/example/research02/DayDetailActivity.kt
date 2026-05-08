package com.example.research02

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class DayDetailActivity : AppCompatActivity() {

    private lateinit var txtTitle: TextView
    private lateinit var txtValue: TextView
    private lateinit var btnMinus: Button
    private lateinit var btnPlus: Button

    private var currentValue = 0
    private lateinit var dateKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_detail)

        txtTitle = findViewById(R.id.txtTitle)
        txtValue = findViewById(R.id.txtValue)
        btnMinus = findViewById(R.id.btnMinus)
        btnPlus = findViewById(R.id.btnPlus)

        // 📥 Daten aus Intent
        val timestamp = intent.getLongExtra("timestamp", 0L)
        dateKey = intent.getStringExtra("dateKey") ?: ""
        currentValue = intent.getIntExtra("value", 0)

        // 📅 Titel setzen
        val date = Date(timestamp)
        val format = SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.getDefault())
        txtTitle.text = format.format(date)

        txtValue.text = currentValue.toString()

        val prefs = getSharedPreferences("training_data", MODE_PRIVATE)

        btnMinus.setOnClickListener {
            if (currentValue > 0) {
                currentValue--
                txtValue.text = currentValue.toString()
            }
        }

        btnPlus.setOnClickListener {
            currentValue++
            txtValue.text = currentValue.toString()
        }

        // 💾 Speichern beim Verlassen
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            prefs.edit().putInt(dateKey, currentValue).apply()
            finish()
        }
    }
}