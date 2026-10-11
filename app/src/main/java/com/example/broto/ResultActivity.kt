package com.example.broto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val won = intent.getBooleanExtra("EXTRA_WON", false)
        val finalGrowth = intent.getIntExtra("EXTRA_GROWTH", 0)
        val daysUsed = intent.getIntExtra("EXTRA_DAYS", 1)

        val tvResultTitle = findViewById<TextView>(R.id.tvResultTitle)
        val ivResultPlant = findViewById<ImageView>(R.id.ivResultPlant)
        val tvFinalGrowth = findViewById<TextView>(R.id.tvFinalGrowth)
        val tvFinalDays = findViewById<TextView>(R.id.tvFinalDays)
        val btnPlantarDeNovo = findViewById<Button>(R.id.btnPlantarDeNovo)

        if (won) {
            tvResultTitle.text = "Sua planta\nfloresceu!"
            ivResultPlant.setImageResource(R.drawable.broto_4)
        } else {
            tvResultTitle.text = "A plantinha\nnão resistiu"
            ivResultPlant.setImageResource(R.drawable.broto_morto)
        }

        tvFinalGrowth.text = "Crescimento final: $finalGrowth%"
        tvFinalDays.text = "Dias usados: $daysUsed"

        btnPlantarDeNovo.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            // Clear current task and start a fresh game
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
