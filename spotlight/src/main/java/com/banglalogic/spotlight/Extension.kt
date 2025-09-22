package com.banglalogic.daymate.spotlight

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

fun View.awaitMeasured(onReady: (View) -> Unit) {
    if (isLaidOut && width > 0 && height > 0) {
        onReady(this)
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isLaidOut && width > 0 && height > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    onReady(this@awaitMeasured)
                }
            }
        })
    }
}

fun RecyclerView.awaitItem(position: Int, onReady: (View) -> Unit) {
    scrollToPosition(position)
    post {
        val holder = findViewHolderForAdapterPosition(position)
        if (holder?.itemView != null) {
            holder.itemView.awaitMeasured(onReady)
        } else {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    val vh = findViewHolderForAdapterPosition(position)
                    if (vh?.itemView != null) {
                        removeOnScrollListener(this)
                        vh.itemView.awaitMeasured(onReady)
                    }
                }
            })
        }
    }
}

fun TabLayout.awaitTabAt(position: Int, onReady: (View) -> Unit) {
    val tabStrip = getChildAt(0) as? ViewGroup ?: return
    val tabView = tabStrip.getChildAt(position)
    tabView?.awaitMeasured(onReady)
}