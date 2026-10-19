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
import android.view.animation.DecelerateInterpolator
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class FuturisticCircularGaugeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var progress: Float = 0f
        private set

    var title: String = ""
        set(value) {
            field = value
            invalidate()
        }

    var valueText: String = ""
        set(value) {
            field = value
            invalidate()
        }

    var accentColor: Int = Color.parseColor("#00F2FE")
        set(value) {
            field = value
            updateShader()
            invalidate()
        }

    var secondaryColor: Int = Color.parseColor("#4FACFE")
        set(value) {
            field = value
            updateShader()
            invalidate()
        }

    private var currentProgress: Float = 0f
    private var progressAnimator: ValueAnimator? = null

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        color = Color.parseColor("#1FFFFFFF")
    }

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val glowDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val valueTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val titleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9CA3AF")
        textAlign = Paint.Align.CENTER
    }

    private val arcBounds = RectF()
    private val matrixGradient = Matrix()
    private var strokeWidthPx: Float = 10f

    init {
        val density = resources.displayMetrics.density
        strokeWidthPx = 4.5f * density
        trackPaint.strokeWidth = strokeWidthPx
        arcPaint.strokeWidth = strokeWidthPx
        valueTextPaint.textSize = 11f * resources.displayMetrics.scaledDensity
        titleTextPaint.textSize = 8.5f * resources.displayMetrics.scaledDensity
    }

    fun setProgress(target: Float, animated: Boolean = true) {
        val clamped = target.coerceIn(0f, 100f)
        progress = clamped
        progressAnimator?.cancel()
        if (animated && isAttachedToWindow) {
            progressAnimator = ValueAnimator.ofFloat(currentProgress, clamped).apply {
                duration = 650
                interpolator = DecelerateInterpolator(1.8f)
                addUpdateListener {
                    currentProgress = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } else {
            currentProgress = clamped
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = min(w, h).toFloat()
        val halfStroke = strokeWidthPx / 2f + 4f
        val left = (w - size) / 2f + halfStroke
        val top = (h - size) / 2f + halfStroke
        val right = (w + size) / 2f - halfStroke
        val bottom = (h + size) / 2f - halfStroke
        arcBounds.set(left, top, right, bottom)
        updateShader()
    }

    private fun updateShader() {
        if (arcBounds.width() <= 0) return
        val cx = arcBounds.centerX()
        val cy = arcBounds.centerY()
        val gradient = SweepGradient(
            cx, cy,
            intArrayOf(secondaryColor, accentColor, secondaryColor),
            floatArrayOf(0f, 0.5f, 1f)
        )
        matrixGradient.reset()
        matrixGradient.setRotate(-90f, cx, cy)
        gradient.setLocalMatrix(matrixGradient)
        arcPaint.shader = gradient
        glowDotPaint.color = accentColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (arcBounds.width() <= 0) return

        // 1. Draw Background Track Ring (full 360)
        canvas.drawOval(arcBounds, trackPaint)

        // 2. Draw Progress Ring Arc (starts from -90 top, clockwise)
        val sweepAngle = (currentProgress / 100f) * 360f
        if (sweepAngle > 0.5f) {
            canvas.drawArc(arcBounds, -90f, sweepAngle, false, arcPaint)

            // 3. Glowing Indicator Dot at the head of the arc
            val cx = arcBounds.centerX()
            val cy = arcBounds.centerY()
            val radius = arcBounds.width() / 2f
            val rad = Math.toRadians((-90.0 + sweepAngle).toDouble())
            val dotX = (cx + radius * cos(rad)).toFloat()
            val dotY = (cy + radius * sin(rad)).toFloat()

            glowDotPaint.alpha = 255
            canvas.drawCircle(dotX, dotY, strokeWidthPx * 0.7f, glowDotPaint)
        }

        // 4. Center Texts (Value and Title)
        val cx = arcBounds.centerX()
        val cy = arcBounds.centerY()

        val textToDraw = if (valueText.isNotEmpty()) valueText else "${currentProgress.toInt()}%"
        val valueY = cy + (valueTextPaint.textSize / 3.2f) - (if (title.isNotEmpty()) 4f else 0f)
        canvas.drawText(textToDraw, cx, valueY, valueTextPaint)

        if (title.isNotEmpty()) {
            val titleY = valueY + titleTextPaint.textSize + 3f
            canvas.drawText(title.uppercase(), cx, titleY, titleTextPaint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        progressAnimator?.cancel()
    }
}
