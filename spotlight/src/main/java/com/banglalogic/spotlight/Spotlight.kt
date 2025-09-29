package com.banglalogic.spotlight

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.banglalogic.spotlight.model.CardPosition
import com.banglalogic.spotlight.model.ShapeAnimation
import com.banglalogic.spotlight.model.SpotlightStep
import com.banglalogic.spotlight.model.SpotlightTarget
import kotlin.math.max
import androidx.core.graphics.toColorInt

class Spotlight private constructor(
    private val context: Context,
    private val steps: List<SpotlightStep>,
    private val overlayColor: Int,
    private val cardBackground: Int,
    private val titleStyle: TextStyleConfig,
    private val descStyle: TextStyleConfig,
    private val highlightPadding: Int,
    private val showButtons: Boolean,
    private val cardPosition: CardPosition,
    private val shapeAnimation: ShapeAnimation,
    private val useBlur: Boolean,
    private val listener: SpotlightListener?
) {

    private val activity: Activity = when {
        context is Activity -> context
        context is ContextWrapper && context.baseContext is Activity -> context.baseContext as Activity
        else -> throw IllegalArgumentException("Spotlight requires an Activity context")
    }

    private var overlay: FrameLayout? = null
    private var currentIndex = -1

    fun start() {
        if (steps.isEmpty()) return
        showStep(0, animateIn = true)
    }

    private fun showStep(index: Int, animateIn: Boolean) {
        currentIndex = index

        val overlayViews = createOverlay(activity)
        overlay = overlayViews.root

        val highlightView = overlayViews.highlightView
        val titleText = overlayViews.titleText
        val descText = overlayViews.descText
        val cardContainer = overlayViews.cardContainer   // new: container (card + buttons)
        val card = overlayViews.infoCard                 // actual info card content
        val nextButton = overlayViews.nextButton
        val skipButton = overlayViews.skipButton

        // Style card
        card.background = GradientDrawable().apply {
            cornerRadius = 24f
            setColor(cardBackground)
        }
        card.elevation = 12f

        val step = steps[index]

        val showForView: (View) -> Unit = { targetView ->
            // highlight with padding + shape
            highlightView.setOverlayColor(overlayColor)
            highlightView.setTarget(targetView, step.shape, highlightPadding)

            // Blur option
            highlightView.setBlurEnabled(useBlur)

            // Title + Desc styles
            titleText.text = step.title
            titleText.setTextColor(titleStyle.color)
            titleText.textSize = titleStyle.sizeSp
            titleText.typeface = titleStyle.typeface

            descText.text = step.description
            descText.setTextColor(descStyle.color)
            descText.textSize = descStyle.sizeSp
            descText.typeface = descStyle.typeface

            // Auto-position or explicit position
            targetView.post {
                val location = IntArray(2)
                targetView.getLocationOnScreen(location)
                val targetRect = Rect(
                    location[0],
                    location[1],
                    location[0] + targetView.width,
                    location[1] + targetView.height
                )

                val screenHeight = overlayViews.root.height
                val margin = 32.dp(activity)

                // Position the whole container, not just card
                val lp = cardContainer.layoutParams as FrameLayout.LayoutParams
                lp.marginStart = margin
                lp.marginEnd = margin

                when (cardPosition) {
                    CardPosition.ABOVE -> {
                        lp.topMargin = max(0, targetRect.top - cardContainer.height - margin)
                    }
                    CardPosition.BELOW -> {
                        lp.topMargin = targetRect.bottom + margin
                    }
                    CardPosition.LEFT -> {
                        lp.topMargin = max(0, targetRect.centerY() - cardContainer.height / 2)
                        lp.marginStart = max(0, targetRect.left - cardContainer.width - margin)
                    }
                    CardPosition.RIGHT -> {
                        lp.topMargin = max(0, targetRect.centerY() - cardContainer.height / 2)
                        lp.marginEnd = max(0, overlayViews.root.width - targetRect.right - margin)
                    }
                    CardPosition.CENTER -> {
                        lp.gravity = Gravity.CENTER
                    }
                    CardPosition.AUTO -> {
                        if (targetRect.bottom + cardContainer.height + margin < screenHeight) {
                            lp.topMargin = targetRect.bottom + margin
                        } else {
                            lp.topMargin = max(0, targetRect.top - cardContainer.height - margin)
                        }
                    }
                }

                cardContainer.layoutParams = lp
            }

            // Show/Hide Next + Skip buttons
            if (showButtons) {
                nextButton.visibility = View.VISIBLE
                skipButton.visibility = View.VISIBLE
            } else {
                nextButton.visibility = View.GONE
                skipButton.visibility = View.GONE
            }

            nextButton.setOnClickListener {
                listener?.onStepNext(index, step)
                if (index + 1 < steps.size) {
                    dismissStep(index) {
                        showStep(index + 1, animateIn = true)
                    }
                } else {
                    finish()
                }
            }

            skipButton.setOnClickListener {
                listener?.onStepSkipped(index, step)
                finish()
            }

            // Animate overlay + container
            val root = overlayViews.root
            root.alpha = 0f
            if (animateIn) {
                root.animate().alpha(1f).setDuration(400).start()
                cardContainer.translationY = 50f
                cardContainer.alpha = 0f
                cardContainer.animate().translationY(0f).alpha(1f).setDuration(400).start()
            } else {
                root.alpha = 1f
            }

            // Shape animation (pulse, breathing, bounce)
            when (shapeAnimation) {
                ShapeAnimation.NONE -> highlightView.clearAnimation()
                ShapeAnimation.PULSE -> highlightView.startPulseAnimation()
                ShapeAnimation.BREATHING -> highlightView.startBreathingAnimation()
                ShapeAnimation.BOUNCE -> highlightView.startBounceAnimation()
            }

            // Root click -> next (if no buttons)
            if (!showButtons) {
                root.setOnClickListener {
                    dismissStep(index) {
                        if (index + 1 < steps.size) {
                            showStep(index + 1, animateIn = true)
                        } else {
                            finish()
                        }
                    }
                }
            }

            (activity.window.decorView as ViewGroup).addView(root)

            // Notify listener
            listener?.onStepShown(index, step)
        }

        // Handle different target types
        when (val target = step.target) {
            is SpotlightTarget.ViewTarget -> target.view.awaitMeasured { showForView(it) }
            is SpotlightTarget.RecyclerItemTarget -> target.recyclerView.awaitItem(target.position) { showForView(it) }
            is SpotlightTarget.TabTarget -> target.tabLayout.awaitTabAt(target.position) { showForView(it) }
        }
    }

    fun dismiss() {
        overlay?.animate()?.alpha(0f)?.setDuration(250)?.withEndAction {
            (activity.window.decorView as ViewGroup).removeView(overlay)
            overlay = null
        }?.start()
    }

    private fun dismissStep(index: Int, onEnd: () -> Unit) {
        overlay?.animate()?.alpha(0f)?.setDuration(250)?.withEndAction {
            (activity.window.decorView as ViewGroup).removeView(overlay)
            overlay = null
            listener?.onStepDismissed(index, steps[index])
            onEnd()
        }?.start()
    }

    private fun finish() {
        dismiss()
        listener?.onFinished()
    }

    // Builder
    class Builder(context: Context) {
        private val activity: Activity = context as? Activity
            ?: (if (context is ContextWrapper && context.baseContext is Activity) {
                context.baseContext as Activity
            } else {
                throw IllegalArgumentException("Spotlight requires an Activity context")
            })

        private val steps = mutableListOf<SpotlightStep>()
        private var overlayColor: Int = "#B3000000".toColorInt()
        private var cardBackground: Int = "#CC333333".toColorInt()
        private var titleStyle = TextStyleConfig(18f, Color.WHITE, Typeface.DEFAULT_BOLD)
        private var descStyle = TextStyleConfig(14f, Color.WHITE, Typeface.DEFAULT)
        private var highlightPadding = 0
        private var showButtons = false
        private var cardPosition = CardPosition.AUTO
        private var shapeAnimation = ShapeAnimation.PULSE
        private var useBlur = false
        private var listener: SpotlightListener? = null

        fun addStep(step: SpotlightStep) = apply { steps.add(step) }
        fun overlayColor(color: Int) = apply { overlayColor = color }
        fun cardBackground(color: Int) = apply { cardBackground = color }
        fun titleStyle(sizeSp: Float, color: Int, typeface: Typeface) = apply {
            titleStyle = TextStyleConfig(sizeSp, color, typeface)
        }
        fun descStyle(sizeSp: Float, color: Int, typeface: Typeface) = apply {
            descStyle = TextStyleConfig(sizeSp, color, typeface)
        }
        fun highlightPadding(padding: Int) = apply { highlightPadding = padding }
        fun showButtons(enabled: Boolean) = apply { showButtons = enabled }
        fun cardPosition(position: CardPosition) = apply { cardPosition = position }
        fun shapeAnimation(animation: ShapeAnimation) = apply { shapeAnimation = animation }
        fun useBlurOverlay(enabled: Boolean) = apply { useBlur = enabled }
        fun listener(listener: SpotlightListener) = apply { this.listener = listener }

        fun build() = Spotlight(activity, steps, overlayColor, cardBackground, titleStyle,
            descStyle, highlightPadding, showButtons, cardPosition, shapeAnimation, useBlur, listener)
    }

    data class TextStyleConfig(val sizeSp: Float, val color: Int, val typeface: Typeface)
}