package com.banglalogic.daymate.spotlight

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.banglalogic.daymate.spotlight.model.SpotlightStep
import com.banglalogic.daymate.spotlight.model.SpotlightTarget
import kotlin.math.max

class Spotlight private constructor(
    private val context: Context,
    private val steps: List<SpotlightStep>,
    private val overlayColor: Int,
    private val cardBackground: Int
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
        val card = overlayViews.infoCard

        // theme + rounded + shadow
        card.background = GradientDrawable().apply {
            cornerRadius = 24f
            setColor(cardBackground)
        }
        card.elevation = 12f

        val step = steps[index]

        val showForView: (View) -> Unit = { targetView ->
            highlightView.setOverlayColor(overlayColor)
            highlightView.setTarget(targetView, step.shape)

            titleText.text = step.title
            descText.text = step.description

            // Auto-position card relative to target
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

                val lp = card.layoutParams as FrameLayout.LayoutParams
                lp.marginStart = margin
                lp.marginEnd = margin

                if (targetRect.bottom + card.height + margin < screenHeight) {
                    // below
                    lp.topMargin = targetRect.bottom + margin
                } else {
                    // above
                    lp.topMargin = max(0, targetRect.top - card.height - margin)
                }
                card.layoutParams = lp
            }

            // Animate overlay + card
            val root = overlayViews.root
            root.alpha = 0f
            if (animateIn) {
                root.animate().alpha(1f).setDuration(400).start()
                card.translationY = 50f
                card.alpha = 0f
                card.animate().translationY(0f).alpha(1f).setDuration(400).start()
            } else {
                root.alpha = 1f
            }

            root.setOnClickListener {
                // fade out current step
                root.animate().alpha(0f).setDuration(250).withEndAction {
                    (activity.window.decorView as ViewGroup).removeView(root)
                    if (index + 1 < steps.size) {
                        showStep(index + 1, animateIn = true)
                    } else dismiss()
                }.start()
            }

            (activity.window.decorView as ViewGroup).addView(root)
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

    // Builder
    class Builder(context: Context) {
        private val activity: Activity = context as? Activity
            ?: (if (context is ContextWrapper && context.baseContext is Activity) {
                context.baseContext as Activity
            } else {
                throw IllegalArgumentException("Spotlight requires an Activity context")
            })

        private val steps = mutableListOf<SpotlightStep>()
        private var overlayColor: Int = Color.parseColor("#B3000000")
        private var cardBackground: Int = Color.parseColor("#CC333333")

        fun addStep(step: SpotlightStep) = apply { steps.add(step) }
        fun overlayColor(color: Int) = apply { overlayColor = color }
        fun cardBackground(color: Int) = apply { cardBackground = color }

        fun build(): Spotlight {
            return Spotlight(activity, steps, overlayColor, cardBackground)
        }
    }
}