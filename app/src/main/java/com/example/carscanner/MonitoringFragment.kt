package com.example.carscanner

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.lifecycle.repeatOnLifecycle
//import com.example.carscanner.localDB.ObdData
//import com.example.carscanner.viewmodel.ViewModelMain

class MonitoringFragment : Fragment() {

    private lateinit var graphView: CustomGraphView
    private val handler = Handler(Looper.getMainLooper())
    private var index = 0
    private lateinit var rpmTextView: TextView
    private lateinit var speedTextView: TextView
    private lateinit var ai: View
    private lateinit var aiPulse1: View
    private lateinit var aiPulse2: View
    private lateinit var aiText: TextView
    private var colorOrange: Int = 0
    private var colorBlue: Int = 0
    private var colorGreen: Int = 0
    private var colorPurple: Int = 0


    // 예시 급발진 데이터 (속도, 브레이크, 엑셀, RPM)
    private val speedList = listOf(
        0f, 0f, 0f, 5f, 20f, 45f, 60f, 70f, 75f, 78f,
        80f, 80f, 75f, 60f, 30f, 5f, 0f
    )
    private val rpmList = listOf(
        800f, 800f, 800f, 2000f, 1500f, 2000f, 5000f, 6000f, 7000f, 600f,
        400f, 300f, 200f, 150f, 1000f, 900f, 800f
    )
    private val accelList = listOf(
        0f, 0f, 0f, 2f, 1f, 0f, 30f, 50f, 70f, 80f,
        75f, 0f, 0f,0f, 5f, 0f, 0f
    )
    private val brakeList = listOf(
        50f, 50f, 50f, 2f, 1f, 0f, 30f, 0f, 0f, 0f,
        0f, 0f, 0f, 0f, 0f, 0f, 0f
    )

    private val highlightXs = mutableListOf<Float>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_monitoring, container, false)
        val aiSpeechPulse1View: View = root.findViewById(R.id.aiSpeechPulse1)
        val aiSpeechPulse2View: View = root.findViewById(R.id.aiSpeechPulse2)
        val pulse2 = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse2)
        val pulse3 = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse3)
        val showTextOrGraph:Switch = root.findViewById(R.id.showTextOrGraph)
        val currentInfo = root.findViewById<LinearLayout>(R.id.currentInfo)

        aiSpeechPulse1View.startAnimation(pulse2)
        aiSpeechPulse2View.startAnimation(pulse3)

        val graphView : View = root.findViewById(R.id.graphView)
        graphView.visibility = View.GONE

        showTextOrGraph.setOnCheckedChangeListener {_, isChecked ->
            if(isChecked){
                graphView.visibility = View.VISIBLE
                currentInfo.visibility = View.GONE
            }
            else {
                graphView.visibility = View.GONE
                currentInfo.visibility = View.VISIBLE
            }

        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rpmTextView = view.findViewById(R.id.rpmValueTextView)
        speedTextView = view.findViewById(R.id.speedValueTextView)
        graphView = requireView().findViewById(R.id.graphView)
        ai = view.findViewById(R.id.aiSpeech)
        aiPulse1 = view.findViewById(R.id.aiSpeechPulse1)
        aiPulse2 = view.findViewById(R.id.aiSpeechPulse2)
        aiText = view.findViewById(R.id.aiSpeechText)
        colorBlue = ContextCompat.getColor(requireContext(), R.color.blue)
        colorOrange = ContextCompat.getColor(requireContext(), R.color.orange)
        colorGreen = ContextCompat.getColor(requireContext(), R.color.green)
        colorPurple = ContextCompat.getColor(requireContext(), R.color.purple)

        simulateRealTimeUpdate()
    }

    private fun simulateRealTimeUpdate() {
        if (index >= speedList.size) return

        val x = index.toFloat()
        val speed = speedList[index]
        val brake = brakeList[index]
        val accel = accelList[index]
        val rpm = rpmList[index]
        rpmTextView.text = rpm.toInt().toString()
        speedTextView.text = speed.toInt().toString()
        val isSuddenAccel = rpm > 2000f
        graphView.updateData(
            xValue = x,
            speed = speed,
            brake = brake,
            accel = accel,
            rpm = rpm,
            highlight = isSuddenAccel
        )
        if (accel > 0f){
            if (brake > 0f){
                ai.backgroundTintList = ColorStateList.valueOf(colorPurple)
                aiPulse1.backgroundTintList = ColorStateList.valueOf(colorPurple)
                aiPulse2.backgroundTintList = ColorStateList.valueOf(colorPurple)
                aiText.setText("Both\nPressed")
            }
            else{
                ai.backgroundTintList = ColorStateList.valueOf(colorOrange)
                aiPulse1.backgroundTintList = ColorStateList.valueOf(colorOrange)
                aiPulse2.backgroundTintList = ColorStateList.valueOf(colorOrange)
                aiText.setText("Accel")
            }
        }
        else if (brake > 0f) {
            ai.backgroundTintList = ColorStateList.valueOf(colorGreen)
            aiPulse1.backgroundTintList = ColorStateList.valueOf(colorGreen)
            aiPulse2.backgroundTintList = ColorStateList.valueOf(colorGreen)
            aiText.setText("Brake")
        }
        else {
            ai.backgroundTintList = ColorStateList.valueOf(colorBlue)
            aiPulse1.backgroundTintList = ColorStateList.valueOf(colorBlue)
            aiPulse2.backgroundTintList = ColorStateList.valueOf(colorBlue)
            aiText.setText("")
        }
        if (isAdded && isSuddenAccel) {
            var suddenWarningActive = LocalStore.getBoolean(requireContext(), "suddenWarningActive", true)
            if (suddenWarningActive)
            {
                val intent = Intent(requireContext(), SuddenActivity::class.java)
                startActivity(intent)
            }
        }
        index++
        handler.postDelayed({ simulateRealTimeUpdate() }, 500)
    }
}
