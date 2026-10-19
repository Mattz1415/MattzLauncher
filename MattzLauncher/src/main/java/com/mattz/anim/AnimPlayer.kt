package com.mattz.anim

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.TimeInterpolator
import android.view.View
import android.view.animation.PathInterpolator
import com.mattz.anim.animations.Animations
import com.mattz.launcher.setting.AllSettings

class AnimPlayer {
    private var mAnimatorSet: AnimatorSet = AnimatorSet()
    private var mAnimators: MutableList<Animator> = ArrayList()
    private var mOnStartCallback: AnimCallback? = null
    private var mOnEndCallback: AnimCallback? = null
    private var mDuration: Long? = null
    private var mDelay: Long? = null
    private var mInterpolator: TimeInterpolator = PathInterpolator(0.16f, 1f, 0.3f, 1f)

    fun clearEntries() {
        mAnimators.clear()
    }

    fun apply(entry: Entry): AnimPlayer {
        mAnimators.addAll(entry.animations.animator.getAnimators(entry.target))
        return this
    }

    fun interpolator(interpolator: TimeInterpolator): AnimPlayer {
        mInterpolator = interpolator
        return this
    }

    fun duration(long: Long): AnimPlayer {
        mDuration = long
        return this
    }

    fun delay(long: Long): AnimPlayer {
        mDelay = long
        return this
    }

    fun setOnStart(callback: AnimCallback): AnimPlayer {
        mOnStartCallback = callback
        return this
    }

    fun setOnEnd(callback: AnimCallback): AnimPlayer {
        mOnEndCallback = callback
        return this
    }

    fun start() {
        if (mAnimatorSet.isStarted || mAnimatorSet.isRunning) {
            stop()
        }

        mAnimatorSet.apply {
            duration = mDuration ?: AllSettings.animationSpeed.getValue().toLong()
            startDelay = mDelay ?: 0
            interpolator = mInterpolator

            removeAllListeners()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    mOnStartCallback?.call()
                }

                override fun onAnimationEnd(animation: Animator) {
                    mOnEndCallback?.call()
                    clearState()
                }

                override fun onAnimationCancel(animation: Animator) {
                    clearState()
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
            playTogether(mAnimators)
            start()
        }
    }

    fun stop() {
        if (mAnimatorSet.isRunning) {
            mAnimatorSet.cancel()
            clearState()
        }
    }

    private fun clearState() {
        mAnimatorSet = AnimatorSet()
    }

    companion object {
        fun play(): AnimPlayer {
            return AnimPlayer()
        }
    }

    data class Entry(val target: View, val animations: Animations)
}
