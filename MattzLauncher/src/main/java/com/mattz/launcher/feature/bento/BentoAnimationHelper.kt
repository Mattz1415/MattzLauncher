package com.mattz.launcher.feature.bento

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.OvershootInterpolator
import android.view.animation.PathInterpolator

object BentoAnimationHelper {
    private val springInterpolator = PathInterpolator(0.16f, 1f, 0.3f, 1f)
    private val bounceInterpolator = OvershootInterpolator(1.2f)

    fun animateBentoStagger(vararg views: View) {
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 40f

            val delay = index * 45L
            val alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
                duration = 320
                startDelay = delay
                interpolator = springInterpolator
            }
            val transAnim = ObjectAnimator.ofFloat(view, "translationY", 40f, 0f).apply {
                duration = 380
                startDelay = delay
                interpolator = bounceInterpolator
            }

            AnimatorSet().apply {
                playTogether(alphaAnim, transAnim)
                start()
            }
        }
    }

    fun animatePillClick(target: View) {
        val scaleX = ObjectAnimator.ofFloat(target, "scaleX", 0.92f, 1.04f, 1f).apply { duration = 240 }
        val scaleY = ObjectAnimator.ofFloat(target, "scaleY", 0.92f, 1.04f, 1f).apply { duration = 240 }
        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            start()
        }
    }
}
