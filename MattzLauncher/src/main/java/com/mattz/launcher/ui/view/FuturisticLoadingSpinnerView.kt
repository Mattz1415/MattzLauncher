package com.mattz.launcher.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.min

class FuturisticLoadingSpinnerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var rotationAngle: Float = 0f
    private var pulseAlpha: Int = 255
    private var animator: ValueAnimator? = null

    private val outerArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val innerArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val centerDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#00F2FE")
    }

    private val outerBounds = RectF()
    private val innerBounds = RectF()
    private val outerMatrix = Matrix()
    private val innerMatrix = Matrix()
    private var strokeWidthPx = 8f

    init {
        val density = resources.displayMetrics.density
        strokeWidthPx = 3f * density
        outerArcPaint.strokeWidth = strokeWidthPx
        innerArcPaint.strokeWidth = strokeWidthPx * 0.85f
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            startAnimation()
        } else {
            stopAnimation()
        }
    }

    private fun startAnimation() {
        if (animator?.isRunning == true) return
        animator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 1400
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                rotationAngle = it.animatedValue as Float
                val pulse = (rotationAngle / 360f) * 2f
                val sinVal = kotlin.math.sin(pulse * Math.PI)
                pulseAlpha = (140 + 115 * sinVal).toInt().coerceIn(60, 255)
                invalidate()
            }
            start()
        }
    }

    private fun stopAnimation() {
        animator?.cancel()
        animator = null
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = min(w, h).toFloat()
        val halfStroke = strokeWidthPx / 2f + 2f
        val left = (w - size) / 2f + halfStroke
        val top = (h - size) / 2f + halfStroke
        val right = (w + size) / 2f - halfStroke
        val bottom = (h + size) / 2f - halfStroke
        outerBounds.set(left, top, right, bottom)

        val innerPadding = strokeWidthPx * 2.2f
        innerBounds.set(
            left + innerPadding,
            top + innerPadding,
            right - innerPadding,
            bottom - innerPadding
        )

        updateGradients()
    }

    private fun updateGradients() {
        if (outerBounds.width() <= 0) return
        val cx = outerBounds.centerX()
        val cy = outerBounds.centerY()

        val outerGrad = SweepGradient(
            cx, cy,
            intArrayOf(
                Color.parseColor("#10FFFFFF"),
                Color.parseColor("#4FACFE"),
                Color.parseColor("#00F2FE")
            ),
            floatArrayOf(0f, 0.6f, 1f)
        )
        outerArcPaint.shader = outerGrad

        val innerGrad = SweepGradient(
            cx, cy,
            intArrayOf(
                Color.parseColor("#10FFFFFF"),
                Color.parseColor("#A855F7"),
                Color.parseColor("#EC4899")
            ),
            floatArrayOf(0f, 0.6f, 1f)
        )
        innerArcPaint.shader = innerGrad
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (outerBounds.width() <= 0) return

        val cx = outerBounds.centerX()
        val cy = outerBounds.centerY()

        // 1. Draw Outer Clockwise Ring (280 degrees sweep)
        canvas.save()
        canvas.rotate(rotationAngle, cx, cy)
        canvas.drawArc(outerBounds, 0f, 280f, false, outerArcPaint)
        canvas.restore()

        // 2. Draw Inner Counter-Clockwise Ring (210 degrees sweep)
        canvas.save()
        canvas.rotate(-rotationAngle * 1.5f, cx, cy)
        canvas.drawArc(innerBounds, 0f, 210f, false, innerArcPaint)
        canvas.restore()

        // 3. Center Pulsing Glow Dot
        centerDotPaint.alpha = pulseAlpha
        canvas.drawCircle(cx, cy, strokeWidthPx * 0.9f, centerDotPaint)
    }
}
