package com.banglalogic.daymate.spotlight

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import com.banglalogic.daymate.spotlight.model.SpotlightShape
import kotlin.math.max

class SpotlightHighlightView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // overlay paint (color may change at runtime)
    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#99000000") // default semi-transparent overlay
    }

    // paint used to "erase" overlay (PorterDuff CLEAR)
    private val clearPaint = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private var targetRect: RectF? = null
    private var shape: SpotlightShape = SpotlightShape.RECTANGLE
    private var pulseScale = 1f

    private var pulseAnimator: ValueAnimator? = null
    private var fadeAnimator: ValueAnimator? = null

    init {
        // Required so PorterDuff CLEAR works correctly
        setLayerType(LAYER_TYPE_HARDWARE, null)
        alpha = 0f // start invisible, let parent animate in if desired
    }

    /**
     * Change overlay (dimming) color at runtime.
     * Call before or after setTarget — view will invalidate.
     */
    fun setOverlayColor(@ColorInt color: Int) {
        overlayPaint.color = color
        invalidate()
    }

    /**
     * Set the target view to highlight. Assumes the target view is already measured and positioned
     * (i.e. you call this after your awaitMeasured / doOnPreDraw logic in Spotlight).
     */
    fun setTarget(view: View, shape: SpotlightShape) {
        // stop any previous pulse so we don't leak or run two animators
        stopPulseAnimation()

        val location = IntArray(2)
        view.getLocationInWindow(location)

        targetRect = RectF(
            location[0].toFloat(),
            location[1].toFloat(),
            (location[0] + view.width).toFloat(),
            (location[1] + view.height).toFloat()
        )

        this.shape = shape
        // start pulse after rect is set
        startPulseAnimation()
        // fade in highlight view if hidden
        fadeIn()
        invalidate()
    }

    private fun startPulseAnimation() {
        pulseAnimator = ValueAnimator.ofFloat(1f, 1.08f, 1f).apply {
            duration = 900L
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                pulseScale = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun stopPulseAnimation() {
        pulseAnimator?.cancel()
        pulseAnimator = null
        pulseScale = 1f
    }

    private fun fadeIn() {
        fadeAnimator?.cancel()
        fadeAnimator = ValueAnimator.ofFloat(alpha, 1f).apply {
            duration = 300L
            addUpdateListener { valueAnimator ->
                alpha = valueAnimator.animatedValue as Float
            }
            start()
        }
    }

    fun crossfadeOut(onEnd: (() -> Unit)? = null) {
        fadeAnimator?.cancel()
        fadeAnimator = ValueAnimator.ofFloat(alpha, 0f).apply {
            duration = 220L
            addUpdateListener { valueAnimator ->
                alpha = valueAnimator.animatedValue as Float
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd?.invoke()
                }
            })
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopPulseAnimation()
        fadeAnimator?.cancel()
        fadeAnimator = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw dim overlay
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)

        // Cut out highlight according to shape
        targetRect?.let { rect ->
            when (shape) {
                SpotlightShape.CIRCLE -> {
                    val radius = (maxOf(rect.width(), rect.height()) / 2f) * pulseScale
                    canvas.drawCircle(rect.centerX(), rect.centerY(), radius, clearPaint)
                }
                SpotlightShape.RECTANGLE -> {
                    val scaled = RectF(
                        rect.left - (rect.width() * (pulseScale - 1f) / 2f),
                        rect.top - (rect.height() * (pulseScale - 1f) / 2f),
                        rect.right + (rect.width() * (pulseScale - 1f) / 2f),
                        rect.bottom + (rect.height() * (pulseScale - 1f) / 2f)
                    )
                    canvas.drawRect(scaled, clearPaint)
                }
                SpotlightShape.ROUNDED_RECT -> {
                    val scaled = RectF(
                        rect.left - (rect.width() * (pulseScale - 1f) / 2f),
                        rect.top - (rect.height() * (pulseScale - 1f) / 2f),
                        rect.right + (rect.width() * (pulseScale - 1f) / 2f),
                        rect.bottom + (rect.height() * (pulseScale - 1f) / 2f)
                    )
                    canvas.drawRoundRect(scaled, 24f, 24f, clearPaint)
                }
            }
        }
    }
}