package com.banglalogic.sample

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.banglalogic.daymate.spotlight.Spotlight
import com.banglalogic.daymate.spotlight.model.SpotlightShape
import com.banglalogic.daymate.spotlight.model.SpotlightStep
import com.banglalogic.daymate.spotlight.model.SpotlightTarget
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    lateinit var fabIcon : FloatingActionButton
    lateinit var tabLayout : TabLayout
    lateinit var submitButton : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {
        fabIcon = findViewById(R.id.fab)
        tabLayout = findViewById(R.id.tab_layout)
        submitButton = findViewById(R.id.submit_button)

        val spotlight = Spotlight.Builder(this)
            .addStep(
                SpotlightStep(
                    SpotlightTarget.ViewTarget(fabIcon),
                    "Quick Action",
                    "Tap here to add a new item",
                    SpotlightShape.CIRCLE
                )
            )
            .addStep(
                SpotlightStep(
                    SpotlightTarget.TabTarget(tabLayout, 0),
                    "First Tab",
                    "Tap here to see first tab item",
                    SpotlightShape.RECTANGLE
                )
            )
            .addStep(
                SpotlightStep(
                    SpotlightTarget.TabTarget(tabLayout, 1),
                    "Second Tab",
                    "Tap here to see second tab item",
                    SpotlightShape.RECTANGLE
                )
            )
            .addStep(
                SpotlightStep(
                    SpotlightTarget.ViewTarget(submitButton),
                    "Submit",
                    "Tap here to submit data",
                    SpotlightShape.ROUNDED_RECT
                )
            )
            .overlayColor(Color.parseColor("#99000000"))
            .cardBackground(Color.parseColor("#CC333333"))
            .build()
        spotlight.start()
    }
}