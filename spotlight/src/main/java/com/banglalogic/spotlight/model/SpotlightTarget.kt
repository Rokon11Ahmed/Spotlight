package com.banglalogic.spotlight.model

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

sealed class SpotlightTarget {
    data class ViewTarget(val view: View) : SpotlightTarget()
    data class RecyclerItemTarget(val recyclerView: RecyclerView, val position: Int) : SpotlightTarget()
    data class TabTarget(val tabLayout: TabLayout, val position: Int) : SpotlightTarget()
}