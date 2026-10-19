package com.mattz.launcher.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.hypot

class AnimatedGridBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bgPaint = Paint().apply {
        color = Color.parseColor("#030407")
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#15FFFFFF")
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var dotSpacing = 28f
    private var dotRadius = 1.2f
    private var waveProgress = 0f
    private var animator: ValueAnimator? = null

    init {
        val density = resources.displayMetrics.density
        dotSpacing = 28f * density
        dotRadius = 1.2f * density
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    private fun startAnimation() {
        if (animator?.isRunning == true) return
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 9000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                waveProgress = it.animatedValue as Float
                updateGlowShader()
                invalidate()
            }
            start()
        }
    }

    private fun stopAnimation() {
        animator?.cancel()
        animator = null
    }

    private fun updateGlowShader() {
        if (width <= 0 || height <= 0) return
        // Ambient radial light wave that breathes gently
        val cx = width * (0.35f + 0.3f * kotlin.math.sin(waveProgress * 2 * Math.PI).toFloat())
        val cy = height * (0.3f + 0.2f * kotlin.math.cos(waveProgress * 2 * Math.PI).toFloat())
        val radius = hypot(width.toFloat(), height.toFloat()) * 0.45f

        val radialGrad = RadialGradient(
            cx, cy, radius,
            intArrayOf(
                Color.parseColor("#1200E5FF"), // 7% subtle cyan glow
                Color.parseColor("#087C4DFF"), // 3% subtle deep purple glow
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.55f, 1f),
            Shader.TileMode.CLAMP
        )
        glowPaint.shader = radialGrad
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateGlowShader()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 1. Solid Dark OLED Canvas
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Ambient Pulse Glow in Background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), glowPaint)

        // 3. Matrix Dot Grid
        var x = dotSpacing / 2f
        val w = width.toFloat()
        val h = height.toFloat()

        while (x < w) {
            var y = dotSpacing / 2f
            while (y < h) {
                canvas.drawCircle(x, y, dotRadius, dotPaint)
                y += dotSpacing
            }
            x += dotSpacing
        }
    }
}
