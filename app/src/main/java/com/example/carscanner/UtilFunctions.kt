package com.example.carscanner

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View

object Anim {
    fun blink(view: View, color1:Int, color2:Int, duration: Long){
        ObjectAnimator.ofArgb(view, "backgroundColor", color1, color2).apply {
            this.duration = duration
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            start()

        }
        /*ValueAnimator.ofInt(0,1).apply {
            this.duration = duration
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                val color = if (value == 0) color1 else color2
                view.setBackgroundColor(color)
            }
            start()
        }*/
    }
}