package com.example.carscanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SuddenActivity : AppCompatActivity() {

    private lateinit var countDownText: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sudden)
        supportActionBar?.hide()


        val endSuddenView: View = findViewById(R.id.endSudden)
        //countDownText = findViewById(R.id.countDownText)*

        /*cancelButton.setOnClickListener {
            val intent = Intent(this, SuddenWarningActivity::class.java)
            startActivity(intent)
            finish()
        }*/
        val aiSpeechTimerView: View = findViewById<View>(R.id.aiSpeechTimer)
        val pulseTimer1View: View = findViewById<View>(R.id.pulseTimer1)
        val pulseTimer2View: View = findViewById<View>(R.id.pulseTimer2)
        val pulse2 = AnimationUtils.loadAnimation(this, R.anim.pulse2)
        val pulse3 = AnimationUtils.loadAnimation(this, R.anim.pulse3)
        pulseTimer1View.startAnimation(pulse2)
        pulseTimer2View.startAnimation(pulse3)
        val blinkBorderView: View = findViewById(R.id.blinkBorder)
        Anim.blink(blinkBorderView, ContextCompat.getColor(this, R.color.red), ContextCompat.getColor(this, R.color.black), 1000L)

        //startCountDown(10) // 카운트다운 시작 (10초)
    }

    private fun startCountDown(seconds: Int) {
        object : CountDownTimer((seconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                countDownText.text = "신${secondsLeft}s)"
            }

            override fun onFinish() {
                // 권한 확인
                if (ActivityCompat.checkSelfPermission(
                        this@SuddenActivity,
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val callIntent = Intent(Intent.ACTION_CALL).apply {
                        data = Uri.parse("tel:112")
                    }
                    startActivity(callIntent)
                } else {
                    // 권한 요청
                    ActivityCompat.requestPermissions(
                        this@SuddenActivity,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        REQUEST_CALL_PERMISSION
                    )
                }
            }
        }.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되었으면 전화 걸기
                val callIntent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:112")
                }
                startActivity(callIntent)
            } else {
                // 권한 거부되었을 경우, ACTION_DIAL로 대체
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:112")
                }
                startActivity(dialIntent)
                Toast.makeText(this, "전화 권한이 없어 다이얼 화면으로 전환됩니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CALL_PERMISSION = 1
    }
}
