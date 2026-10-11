package com.example.broto

import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    // Estado do jogo
    private var dia: Int = 1
    private var acoesRestantes: Int = 3
    private var agua: Int = 50
    private var sol: Int = 50
    private var crescimento: Int = 0
    private var vidas: Int = 3
    private var atingiuCrescimentoPositivo: Boolean = false

    // Componentes da Interface
    private lateinit var tvGrowthLabel: TextView
    private lateinit var pbGrowth: ProgressBar
    private lateinit var tvSunValue: TextView
    private lateinit var tvHeartsValue: TextView
    private lateinit var tvWaterValue: TextView
    private lateinit var ivEventIcon: ImageView
    private lateinit var tvEventDescription: TextView
    private lateinit var ivPlant: ImageView
    private lateinit var tvDayStatus: TextView
    private lateinit var tvActionsStatus: TextView
    private lateinit var btnSol: Button
    private lateinit var btnRegar: Button
    private lateinit var btnAdubar: Button
    private lateinit var btnEncerrarDia: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bindViews()
        setupListeners()
        updateUI()
    }

    private fun bindViews() {
        tvGrowthLabel = findViewById(R.id.tvGrowthLabel)
        pbGrowth = findViewById(R.id.pbGrowth)
        tvSunValue = findViewById(R.id.tvSunValue)
        tvHeartsValue = findViewById(R.id.tvHeartsValue)
        tvWaterValue = findViewById(R.id.tvWaterValue)
        ivEventIcon = findViewById(R.id.ivEventIcon)
        tvEventDescription = findViewById(R.id.tvEventDescription)
        ivPlant = findViewById(R.id.ivPlant)
        tvDayStatus = findViewById(R.id.tvDayStatus)
        tvActionsStatus = findViewById(R.id.tvActionsStatus)
        btnSol = findViewById(R.id.btnSol)
        btnRegar = findViewById(R.id.btnRegar)
        btnAdubar = findViewById(R.id.btnAdubar)
        btnEncerrarDia = findViewById(R.id.btnEncerrarDia)
    }

    private fun setupListeners() {
        // Evento 1: onClick Botão Dar Sol
        btnSol.setOnClickListener {
            if (acoesRestantes > 0) {
                sol = (sol + 30).coerceAtMost(100)
                acoesRestantes--
                updateUI()
            }
        }

        // Evento 2: onClick Botão Regar
        btnRegar.setOnClickListener {
            if (acoesRestantes > 0) {
                agua = (agua + 30).coerceAtMost(100)
                acoesRestantes--
                updateUI()
            }
        }

        // Evento 3: onClick Botão Adubar
        btnAdubar.setOnClickListener {
            if (acoesRestantes > 0) {
                crescimento = (crescimento + 5).coerceAtMost(100)
                if (crescimento > 0) {
                    atingiuCrescimentoPositivo = true
                }
                acoesRestantes--
                if (crescimento >= 100) {
                    finalizarJogo(venceu = true)
                    return@setOnClickListener
                }
                updateUI()
            }
        }

        // Evento 4: onClick Botão Encerrar Dia
        btnEncerrarDia.setOnClickListener {
            processarEncerramentoDoDia()
        }

        // Evento 5: onLongClick Imagem da Planta (Dica de cuidado)
        ivPlant.setOnLongClickListener {
            val statusAgua = when {
                agua in 40..80 -> "Água ideal! (${agua}%)"
                agua < 40 -> "Precisa de água! (${agua}%)"
                else -> "Excesso de água! (${agua}%)"
            }
            val statusSol = when {
                sol in 40..80 -> "Sol ideal! (${sol}%)"
                sol < 40 -> "Precisa de mais sol! (${sol}%)"
                else -> "Excesso de sol! (${sol}%)"
            }
            Toast.makeText(this, "Dica da plantinha:\n$statusAgua | $statusSol\nFaixa ideal: 40% a 80%", Toast.LENGTH_LONG).show()
            true
        }
    }

    private fun processarEncerramentoDoDia() {
        // 1. Água e sol diminuem 25 cada (mínimo 0)
        agua = (agua - 25).coerceAtLeast(0)
        sol = (sol - 25).coerceAtLeast(0)

        // 2. Crescimento e corações calculados
        val aguaNaFaixa = agua in 40..80
        val solNaFaixa = sol in 40..80

        when {
            aguaNaFaixa && solNaFaixa -> {
                crescimento = (crescimento + 25).coerceAtMost(100)
            }
            aguaNaFaixa || solNaFaixa -> {
                crescimento = (crescimento + 10).coerceAtMost(100)
            }
            else -> {
                // Nenhum na faixa ideal: crescimento +0 e perde 1 coração
                vidas--
            }
        }

        if (crescimento > 0) {
            atingiuCrescimentoPositivo = true
        }

        // Verifica derrota por vidas
        if (vidas <= 0) {
            finalizarJogo(venceu = false)
            return
        }

        // Verifica vitória por crescimento
        if (crescimento >= 100) {
            finalizarJogo(venceu = true)
            return
        }

        // 3. Sorteio de evento aleatório:
        // 0: Chuva (chuva.png) -> água +20%
        // 1: Nuvens (nuvem.png) -> sol -20%
        // 2: Joaninha amiga (joaninha.png) -> crescimento +5%
        // 3: Lagarta invasora (lagarta.png) -> crescimento -5%
        when (Random.nextInt(4)) {
            0 -> {
                agua = (agua + 20).coerceAtMost(100)
                ivEventIcon.setImageResource(R.drawable.chuva)
                tvEventDescription.text = "CHUVA PASSAGEIRA: ÁGUA +20%"
            }
            1 -> {
                sol = (sol - 20).coerceAtLeast(0)
                ivEventIcon.setImageResource(R.drawable.nuvem)
                tvEventDescription.text = "NUVENS NO CÉU: SOL -20%"
            }
            2 -> {
                crescimento = (crescimento + 5).coerceAtMost(100)
                if (crescimento > 0) {
                    atingiuCrescimentoPositivo = true
                }
                ivEventIcon.setImageResource(R.drawable.joaninha)
                tvEventDescription.text = "JOANINHA AMIGA TE VISITOU: CRESCIMENTO +5%"
            }
            3 -> {
                crescimento = (crescimento - 5).coerceAtLeast(0)
                ivEventIcon.setImageResource(R.drawable.lagarta)
                tvEventDescription.text = "LAGARTA INVASORA APARECEU: CRESCIMENTO -5%"
            }
        }

        // Condição de derrota: crescimento afetado ao ponto de voltar ao 0%
        if (atingiuCrescimentoPositivo && crescimento <= 0) {
            finalizarJogo(venceu = false)
            return
        }

        // Checagem de vitória após evento
        if (crescimento >= 100) {
            finalizarJogo(venceu = true)
            return
        }

        // 4. Se já completou o dia 7 e não atingiu 100% de crescimento: Derrota
        if (dia >= 7) {
            finalizarJogo(venceu = false)
            return
        }

        // 5. Avança o dia e restaura ações para 3
        dia++
        acoesRestantes = 3
        updateUI()
    }

    private fun updateUI() {
        // Atualiza textos e barras
        tvGrowthLabel.text = "Crescimento: $crescimento%"
        pbGrowth.progress = crescimento

        tvSunValue.text = "$sol%"
        tvHeartsValue.text = "$vidas"
        tvWaterValue.text = "$agua%"

        tvDayStatus.text = "DIA $dia / 7"
        tvActionsStatus.text = "AÇÕES RESTANTES: $acoesRestantes"

        // Habilita/desabilita botões de ação se acabou as ações
        val temAcoes = acoesRestantes > 0
        btnSol.isEnabled = temAcoes
        btnRegar.isEnabled = temAcoes
        btnAdubar.isEnabled = temAcoes

        btnSol.alpha = if (temAcoes) 1.0f else 0.45f
        btnRegar.alpha = if (temAcoes) 1.0f else 0.45f
        btnAdubar.alpha = if (temAcoes) 1.0f else 0.45f

        // Atualiza imagem do broto conforme crescimento
        when {
            crescimento >= 75 -> ivPlant.setImageResource(R.drawable.broto_4)
            crescimento >= 45 -> ivPlant.setImageResource(R.drawable.broto_3)
            crescimento >= 20 -> ivPlant.setImageResource(R.drawable.broto_2)
            else -> ivPlant.setImageResource(R.drawable.broto_1)
        }

        // Quando níveis de sol e água estão abaixo do desejado (< 40%),
        // diminui a saturação em 25% (0.75f) simulando estar secando
        val solAbaixo = sol < 40
        val aguaAbaixo = agua < 40

        if (solAbaixo && aguaAbaixo) {
            val matrix = ColorMatrix()
            matrix.setSaturation(0.75f) // Redução de 25% na saturação
            ivPlant.colorFilter = ColorMatrixColorFilter(matrix)
        } else {
            ivPlant.colorFilter = null // Normal
        }
    }

    private fun finalizarJogo(venceu: Boolean) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("EXTRA_WON", venceu)
            putExtra("EXTRA_GROWTH", crescimento)
            putExtra("EXTRA_DAYS", dia)
        }
        startActivity(intent)
        finish()
    }
}
