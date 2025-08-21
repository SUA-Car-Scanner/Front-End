package com.example.carscanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class SuddenActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    // UI
    private lateinit var countDownText: TextView
    private var countDownTimer: CountDownTimer? = null

    // LocalStore 값
    private var warningCount: Int = 3                    // TTS 반복 횟수
    private var suddenCallingNum: Array<String> = emptyArray()
    private var emergencyNumber: String = "112"

    // TTS
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var speakPending = false
    private var spokenCount = 0

    // 중복 통화 방지
    @Volatile private var hasTriggeredCall = false

    companion object {
        private const val REQUEST_CALL_PERMISSION = 1001
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sudden)
        supportActionBar?.hide()

        // LocalStore 로드
        warningCount = LocalStore.getInt(this, "warningCount", 3)
        suddenCallingNum = LocalStore.getStringArray(this, "suddenCallingNum").toTypedArray()
        emergencyNumber = suddenCallingNum.firstOrNull()?.takeIf { it.isNotBlank() } ?: "112"

        // UI 바인딩
        countDownText = findViewById(R.id.timerText)

        // 종료 버튼: 카운트다운/tts 중단 후 액티비티 종료
        findViewById<View>(R.id.endSudden).setOnClickListener {
            stopEverythingAndFinish()
        }

        // 즉시 전화 버튼: 카운트다운/tts 건너뛰고 바로 전화
        findViewById<View>(R.id.callButton).setOnClickListener {
            stopTimersAndTts()
            triggerCallOnce()
        }

        // 애니메이션 (있으면 사용)
        runCatching {
            val pulse2 = AnimationUtils.loadAnimation(this, R.anim.pulse2)
            val pulse3 = AnimationUtils.loadAnimation(this, R.anim.pulse3)
            findViewById<View>(R.id.pulseTimer1)?.startAnimation(pulse2)
            findViewById<View>(R.id.pulseTimer2)?.startAnimation(pulse3)

            Anim.blink(
                findViewById(R.id.blinkBorder),
                ContextCompat.getColor(this, R.color.red),
                ContextCompat.getColor(this, R.color.black),
                1000L
            )
        }

        // TTS 준비
        tts = TextToSpeech(this, this).apply {
            setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onError(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    if (utteranceId?.startsWith("sudden_msg_") == true) {
                        synchronized(this@SuddenActivity) { spokenCount++ }
                        if (spokenCount >= warningCount) {
                            runOnUiThread { triggerCallOnce() }  // TTS 끝나면 전화
                        }
                    }
                }
            })
        }

        // 카운트다운 시작 (끝나면 TTS)
        startCountDown(10)
    }

    private fun startCountDown(seconds: Int) {
        countDownTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                countDownText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                triggerCallOnce()
                playInstructionTts()
            }
        }.start()
    }

    // TTS 초기화 콜백
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.KOREAN)
            ttsReady = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
            if (speakPending && ttsReady) {
                speakPending = false
                actuallySpeak()
            }
        }
    }

    private fun playInstructionTts() {
        if (warningCount <= 0) {
            return
        }
        if (ttsReady) actuallySpeak() else speakPending = true
    }

    private fun actuallySpeak() {
        val message = """
            1번. 브레이크를 한 번에 강하게 밟으십시오.
            2번. 기어를 중립, N으로 전환하십시오.
            3번. 사이드 브레이크를 사용해 속도를 감소시키십시오.
            모든 상황이 끝났다면 시동을 꺼주시기 바랍니다.
        """.trimIndent()

        spokenCount = 0
        for (i in 1..warningCount) {
            val mode = if (i == 1) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(message, mode, null, "sudden_msg_$i")
        }
    }

    /** 중복 방지 래퍼로 전화 시작 */
    private fun triggerCallOnce() {
        if (hasTriggeredCall) return
        hasTriggeredCall = true
        startEmergencyCall()
    }

    /** 권한 있으면 CALL, 없으면 요청. 거부 시 DIAL */
    private fun startEmergencyCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$emergencyNumber")
            }
            startActivity(callIntent)
            finish()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL_PERMISSION
            )
        }
    }

    // 즉시 종료용(전화 없이)
    private fun stopEverythingAndFinish() {
        stopTimersAndTts()
        finish()
    }

    // 카운트다운/tts 정지
    private fun stopTimersAndTts() {
        countDownTimer?.cancel()
        countDownTimer = null
        tts?.stop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startEmergencyCall()
            } else {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$emergencyNumber")
                }
                startActivity(dialIntent)
                Toast.makeText(this, "전화 권한이 없어 다이얼 화면으로 전환됩니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        tts?.stop()
        tts?.shutdown()
    }
}
