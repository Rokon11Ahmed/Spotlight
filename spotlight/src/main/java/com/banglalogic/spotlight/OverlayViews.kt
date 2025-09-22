package com.banglalogic.daymate.spotlight

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

fun createOverlay(context: Context): OverlayViews {
    val root = FrameLayout(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        id = View.generateViewId()
    }

    val highlightView = SpotlightHighlightView(context).apply {
        id = View.generateViewId()
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }
    root.addView(highlightView)

    val infoCard = LinearLayout(context).apply {
        id = View.generateViewId()
        orientation = LinearLayout.VERTICAL
        setPadding(16.dp(context), 16.dp(context), 16.dp(context), 16.dp(context))
        elevation = 12f
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }
    }

    val titleText = TextView(context).apply {
        id = View.generateViewId()
        textSize = 18f
        setTypeface(typeface, Typeface.BOLD)
        setTextColor(Color.WHITE)
    }
    infoCard.addView(titleText)

    val descText = TextView(context).apply {
        id = View.generateViewId()
        textSize = 14f
        setTextColor(Color.WHITE)
        setPadding(0, 8.dp(context), 0, 0)
    }
    infoCard.addView(descText)

    root.addView(infoCard)

    return OverlayViews(root, highlightView, infoCard, titleText, descText)
}

data class OverlayViews(
    val root: FrameLayout,
    val highlightView: SpotlightHighlightView,
    val infoCard: LinearLayout,
    val titleText: TextView,
    val descText: TextView
)

fun createRootContainer(context: Context): FrameLayout {
    return FrameLayout(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(Color.TRANSPARENT) // overlay handled in SpotlightView
        isClickable = true
        isFocusable = true
    }
}

fun Int.dp(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()
