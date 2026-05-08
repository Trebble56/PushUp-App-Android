package com.example.research02

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var calendarGrid: GridLayout
    private lateinit var headerGrid: GridLayout
    private lateinit var txtMonthYear: TextView

    private lateinit var txtYearTotal: TextView

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        txtYearTotal = findViewById(R.id.txtYearTotal)

        calendarGrid = findViewById(R.id.calendarGrid)
        headerGrid = findViewById(R.id.headerGrid)
        txtMonthYear = findViewById(R.id.txtMonthYear)

        val btnPrev = findViewById<Button>(R.id.btnPrev)
        val btnNext = findViewById<Button>(R.id.btnNext)

        btnPrev.text = "<"
        btnNext.text = ">"

        btnPrev.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            renderCalendar()
        }

        btnNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            renderCalendar()
        }

        renderCalendar()
    }

    private fun renderCalendar() {
        calendarGrid.removeAllViews()
        headerGrid.removeAllViews()

        val prefs = getSharedPreferences("training_data", MODE_PRIVATE)
        val tempCal = calendar.clone() as Calendar
        tempCal.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        txtMonthYear.text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(tempCal.time)

        // Header
        val daysHeader = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
        for (dayName in daysHeader) {
            headerGrid.addView(createHeaderCell(dayName))
        }

        calendarGrid.setBackgroundColor(getColor(R.color.grid_line))
        headerGrid.setBackgroundColor(getColor(R.color.grid_line))

        val offset = firstDayOfWeek - Calendar.MONDAY
        val startOffset = if (offset < 0) offset + 7 else offset

        repeat(startOffset) {
            calendarGrid.addView(createEmptyCell())
        }

        val today = Calendar.getInstance()

        val displayMetrics = resources.displayMetrics
        val cellWidth = displayMetrics.widthPixels / 7
        val cellHeight = cellWidth * 2
        val topPadding = (cellHeight * 0.15f).toInt()

        for (day in 1..daysInMonth) {
            tempCal.set(Calendar.DAY_OF_MONTH, day)
            val dateKey = dateFormat.format(tempCal.time)
            val value = prefs.getInt(dateKey, 0)

            // 🔲 Container
            val container = FrameLayout(this)
            container.layoutParams = createStandardLayoutParams()
            container.setBackgroundColor(getColor(R.color.dark_bg))

            val timestamp = tempCal.timeInMillis
            val currentDateKey = dateKey
            val currentValue = value

            container.setOnClickListener {

                val intent = Intent(this, DayDetailActivity::class.java)

                intent.putExtra("timestamp", timestamp)
                intent.putExtra("dateKey", currentDateKey)
                intent.putExtra("value", currentValue)

                startActivity(intent)
            }

            // 🟠 Tag (oben)
            val tvDay = TextView(this)
            tvDay.text = "$day."
            tvDay.setTextColor(getColor(R.color.orange_main))
            tvDay.gravity = Gravity.CENTER_HORIZONTAL
            tvDay.setPadding(0, topPadding, 0, 0)

            val dayParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            dayParams.gravity = Gravity.TOP
            tvDay.layoutParams = dayParams

            // 🔵 Wert (zentriert)
            val tvValue = TextView(this)
            tvValue.text = if (value > 0) value.toString() else ""
            tvValue.setTextColor(getColor(R.color.orange_main))
            tvValue.gravity = Gravity.CENTER

            if (value > 0) {
                tvValue.setTypeface(null, android.graphics.Typeface.BOLD)
                tvValue.setShadowLayer(15f, 0f, 0f, getColor(R.color.orange_main))
            }

            val valueParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            valueParams.gravity = Gravity.CENTER
            tvValue.layoutParams = valueParams

            // Heute markieren
            val isToday = tempCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    tempCal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    tempCal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)

            if (isToday) {
                container.background = ContextCompat.getDrawable(this, R.drawable.today_border)
            }


            // Zusammensetzen
            container.addView(tvDay)
            container.addView(tvValue)

            calendarGrid.addView(container)
        }
        val yearTotal = getYearTotal()
        txtYearTotal.text = "Jahr gesamt: $yearTotal"
    }

    private fun getYearTotal(): Int {
        val prefs = getSharedPreferences("training_data", MODE_PRIVATE)
        val allEntries = prefs.all

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        var total = 0

        for ((key, value) in allEntries) {
            // Prüfen ob Key ein Datum ist (yyyy-MM-dd)
            if (key.length == 10 && key[4] == '-' && key[7] == '-') {
                val year = key.substring(0, 4).toIntOrNull()

                if (year == currentYear) {
                    total += value as? Int ?: 0
                }
            }
        }

        return total
    }

    private fun createHeaderCell(text: String): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.setTextColor(getColor(R.color.orange_main))
        tv.setTypeface(null, android.graphics.Typeface.BOLD)
        tv.setBackgroundColor(getColor(R.color.dark_bg))

        val displayMetrics = resources.displayMetrics
        val cellWidth = displayMetrics.widthPixels / 7
        val headerHeight = (cellWidth * 0.7).toInt()

        val params = GridLayout.LayoutParams()
        params.width = 0
        params.height = headerHeight
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED)
        params.setMargins(3, 3, 3, 3)

        tv.layoutParams = params
        return tv
    }

    private fun createEmptyCell(): TextView {
        val tv = TextView(this)
        tv.setBackgroundColor(getColor(R.color.dark_bg))
        tv.text = ""
        tv.layoutParams = createStandardLayoutParams()
        return tv
    }

    private fun createStandardLayoutParams(): GridLayout.LayoutParams {
        val rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        val colSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        val params = GridLayout.LayoutParams(rowSpec, colSpec)

        params.width = 0

        val displayMetrics = resources.displayMetrics
        val cellWidth = displayMetrics.widthPixels / 7
        params.height = cellWidth * 2

        params.setGravity(Gravity.FILL)
        params.setMargins(3, 3, 3, 3)

        return params
    }
    override fun onResume() {
        super.onResume()
        renderCalendar()
    }
}