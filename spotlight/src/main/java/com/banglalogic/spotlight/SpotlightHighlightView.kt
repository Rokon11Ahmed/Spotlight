package com.banglalogic.daymate.spotlight

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import com.banglalogic.spotlight.model.ShapeAnimation
import com.banglalogic.spotlight.model.SpotlightShape

class SpotlightHighlightView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // overlay paint (dimming)
    private val overlayPaint = Paint().apply {
        color = 0x99000000.toInt() // default semi-transparent overlay
    }

    // paint used to "erase" overlay (PorterDuff CLEAR)
    private val clearPaint = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private var targetRect: RectF? = null
    private var shape: SpotlightShape = SpotlightShape.RECTANGLE
    private var paddingPx: Int = 0

    // animation state
    private var pulseScale = 1f
    private var pulseAnimator: ValueAnimator? = null
    private var fadeAnimator: ValueAnimator? = null

    // runtime preferences (defaults)
    private var shapeAnimation: ShapeAnimation = ShapeAnimation.PULSE
    private var useBlur: Boolean = false

    init {
        // Required so PorterDuff CLEAR works correctly
        setLayerType(LAYER_TYPE_HARDWARE, null)
        alpha = 0f // start invisible; parent may animate in
    }

    /**
     * Change overlay (dimming) color at runtime.
     */
    fun setOverlayColor(color: Int) {
        overlayPaint.color = color
        invalidate()
    }

    /**
     * Simple setTarget overload used by showStep().
     * Keeps previously-set animation/blur preferences intact.
     */
    fun setTarget(view: View, shape: SpotlightShape, padding: Int) {
        setTarget(view, shape, padding, this.shapeAnimation, this.useBlur)
    }

    /**
     * Full setTarget with explicit animation & blur choices.
     */
    fun setTarget(view: View, shape: SpotlightShape, padding: Int, shapeAnimation: ShapeAnimation, useBlur: Boolean) {
        stopPulseAnimation()

        val location = IntArray(2)
        // location in window so overlay aligns properly
        view.getLocationInWindow(location)

        targetRect = RectF(
            location[0].toFloat() - padding,
            location[1].toFloat() - padding,
            (location[0] + view.width + padding).toFloat(),
            (location[1] + view.height + padding).toFloat()
        )

        this.shape = shape
        this.paddingPx = padding
        this.shapeAnimation = shapeAnimation
        this.useBlur = useBlur

        // apply blur if requested (API 31+)
        setBlurEnabled(useBlur)

        // start shape animation (depending on chosen mode)
        when (shapeAnimation) {
            ShapeAnimation.NONE -> { clearAnimation() }
            ShapeAnimation.PULSE -> startPulseAnimation()
            ShapeAnimation.BREATHING -> startBreathingAnimation()
            ShapeAnimation.BOUNCE -> startBounceAnimation()
        }

        // fade in
        fadeIn()
        invalidate()
    }

    /**
     * Enable / disable blur (applies RenderEffect on this view when available).
     * Note: RenderEffect blurs the view's *content*; blurring underlying activity content reliably
     * would require different approach (snapshot/blur) — this simple approach is provided as requested.
     */
    fun setBlurEnabled(enabled: Boolean) {
        useBlur = enabled
        if (useBlur && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // moderate blur radius - adjust if you want stronger/weaker blur
            setRenderEffect(RenderEffect.createBlurEffect(18f, 18f, Shader.TileMode.CLAMP))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setRenderEffect(null)
        }
        // For older versions we do nothing here (no RenderScript fallback implemented)
        invalidate()
    }

    /**
     * Start a subtle pulse animation.
     */
    fun startPulseAnimation() {
        stopPulseAnimation()
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

    /**
     * Start a slower breathing-style animation.
     */
    fun startBreathingAnimation() {
        stopPulseAnimation()
        pulseAnimator = ValueAnimator.ofFloat(1f, 1.15f, 0.95f, 1f).apply {
            duration = 2000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener {
                pulseScale = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    /**
     * Start a bounce-like animation.
     */
    fun startBounceAnimation() {
        stopPulseAnimation()
        pulseAnimator = ValueAnimator.ofFloat(1f, 1.2f, 1f).apply {
            duration = 700L
            repeatCount = ValueAnimator.INFINITE
            interpolator = BounceInterpolator()
            addUpdateListener {
                pulseScale = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    /**
     * Stop any shape/pulse animation and reset scale.
     */
    private fun stopPulseAnimation() {
        pulseAnimator?.cancel()
        pulseAnimator = null
        pulseScale = 1f
    }

    /**
     * Clear all animations (pulse + fade) and reset state.
     */
    override fun clearAnimation() {
        stopPulseAnimation()
        fadeAnimator?.cancel()
        fadeAnimator = null
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

    /**
     * Crossfade out then call optional onEnd
     */
    fun crossfadeOut(onEnd: (() -> Unit)? = null) {
        fadeAnimator?.cancel()
        fadeAnimator = ValueAnimator.ofFloat(alpha, 0f).apply {
            duration = 220L
            addUpdateListener { valueAnimator -> alpha = valueAnimator.animatedValue as Float }
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
                    val scaled = scaleRect(rect)
                    canvas.drawRect(scaled, clearPaint)
                }
                SpotlightShape.ROUNDED_RECT -> {
                    val scaled = scaleRect(rect)
                    canvas.drawRoundRect(scaled, 24f, 24f, clearPaint)
                }
                SpotlightShape.OVAL -> {
                    val scaled = scaleRect(rect)
                    canvas.drawOval(scaled, clearPaint)
                }
            }
        }
    }

    private fun scaleRect(rect: RectF): RectF {
        return RectF(
            rect.left - (rect.width() * (pulseScale - 1f) / 2f),
            rect.top - (rect.height() * (pulseScale - 1f) / 2f),
            rect.right + (rect.width() * (pulseScale - 1f) / 2f),
            rect.bottom + (rect.height() * (pulseScale - 1f) / 2f)
        )
    }
}