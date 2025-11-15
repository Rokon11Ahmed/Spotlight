package com.banglalogic.spotlight

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.core.view.setPadding

fun createOverlay(context: Context): OverlayViews {
    val root = FrameLayout(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        id = View.generateViewId()
        isClickable = true
        isFocusable = true
    }

    // Highlight view covering whole screen
    val highlightView = SpotlightHighlightView(context).apply {
        id = View.generateViewId()
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }
    root.addView(highlightView)

    // Container for infoCard + buttons (VERTICAL)
    val cardContainer = LinearLayout(context).apply {
        id = View.generateViewId()
        orientation = LinearLayout.VERTICAL
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.TOP // Spotlight.showStep() will adjust
        }
        elevation = 12f
    }

    // Info card (title + description)
    val infoCard = LinearLayout(context).apply {
        id = View.generateViewId()
        orientation = LinearLayout.VERTICAL
        setPadding(16.dp(context))
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
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

    cardContainer.addView(infoCard)

    // Button row BELOW infoCard
    val buttonRow = LinearLayout(context).apply {
        id = View.generateViewId()
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.END
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = 8.dp(context)
            marginEnd = 12.dp(context)
            bottomMargin = 12.dp(context)
        }
    }

    val skipButton = Button(context).apply {
        id = View.generateViewId()
        text = "Skip"
        visibility = View.GONE
        isAllCaps = false
    }

    val nextButton = Button(context).apply {
        id = View.generateViewId()
        text = "Next"
        visibility = View.GONE
        isAllCaps = false
    }

    buttonRow.addView(skipButton)
    buttonRow.addView(Space(context).apply {
        layoutParams = LinearLayout.LayoutParams(8.dp(context), 0)
    })
    buttonRow.addView(nextButton)

    cardContainer.addView(buttonRow)

    root.addView(cardContainer)

    return OverlayViews(
        root = root,
        highlightView = highlightView,
        cardContainer = cardContainer,
        infoCard = infoCard,
        titleText = titleText,
        descText = descText,
        nextButton = nextButton,
        skipButton = skipButton
    )
}

data class OverlayViews(
    val root: FrameLayout,
    val highlightView: SpotlightHighlightView,
    val cardContainer: LinearLayout, // now vertical block
    val infoCard: LinearLayout,
    val titleText: TextView,
    val descText: TextView,
    val nextButton: Button,
    val skipButton: Button
)

fun Int.dp(context: Context): Int =
    (this * context.resources.displayMetrics.density).toInt()

