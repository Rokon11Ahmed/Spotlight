package com.banglalogic.spotlight

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
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

fun RecyclerView.awaitItem(
    position: Int,
    timeoutMs: Long = 2000L,
    onReady: (View) -> Unit
) {
    post {
        // Best scroll approach for accuracy
        (layoutManager as? LinearLayoutManager)?.let { lm ->
            try {
                lm.scrollToPositionWithOffset(position, 0)
            } catch (_: Exception) {
                scrollToPosition(position)
            }
        } ?: scrollToPosition(position)

        val mainHandler = handler ?: android.os.Handler(context.mainLooper)
        var cleaned = false

        // --- Declare listeners & runnable BEFORE usage ---

        lateinit var attachListener: RecyclerView.OnChildAttachStateChangeListener
        lateinit var layoutChangeListener: View.OnLayoutChangeListener
        lateinit var retryRunnable: Runnable

        fun cleanup() {
            if (cleaned) return
            cleaned = true
            try { removeOnChildAttachStateChangeListener(attachListener) } catch (_: Exception) {}
            try { removeOnLayoutChangeListener(layoutChangeListener) } catch (_: Exception) {}
            try { mainHandler.removeCallbacks(retryRunnable) } catch (_: Exception) {}
        }

        fun checkAndDeliver(): Boolean {
            val vh = findViewHolderForAdapterPosition(position)
            val item = vh?.itemView
            if (item != null && item.isLaidOut && item.width > 0 && item.height > 0) {
                item.doOnLayout {
                    if (!cleaned) {
                        cleanup()
                        onReady(item)
                    }
                }
                return true
            }
            return false
        }

        attachListener = object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                val holder = getChildViewHolder(view)
                if (holder != null && holder.adapterPosition == position) {
                    view.doOnLayout {
                        if (!cleaned) {
                            cleanup()
                            onReady(view)
                        }
                    }
                }
            }
            override fun onChildViewDetachedFromWindow(view: View) {}
        }

        layoutChangeListener = View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            checkAndDeliver()
        }

        val startTime = System.currentTimeMillis()
        retryRunnable = object : Runnable {
            override fun run() {
                if (cleaned) return
                if (checkAndDeliver()) return

                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed >= timeoutMs) {
                    cleanup()
                    return
                }
                mainHandler.postDelayed(this, 40)
            }
        }

        // --- Attach listeners ---
        addOnChildAttachStateChangeListener(attachListener)
        addOnLayoutChangeListener(layoutChangeListener)

        // Fast check
        if (checkAndDeliver()) return@post

        // Start retry loop
        mainHandler.postDelayed(retryRunnable, 40)
    }
}

fun TabLayout.awaitTabAt(position: Int, onReady: (View) -> Unit) {
    val tabStrip = getChildAt(0) as? ViewGroup ?: return
    val tabView = tabStrip.getChildAt(position)
    tabView?.awaitMeasured(onReady)
}