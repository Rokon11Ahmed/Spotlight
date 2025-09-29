package com.banglalogic.sample

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.banglalogic.spotlight.Spotlight
import com.banglalogic.spotlight.model.CardPosition
import com.banglalogic.spotlight.model.ShapeAnimation
import com.banglalogic.spotlight.model.SpotlightShape
import com.banglalogic.spotlight.model.SpotlightStep
import com.banglalogic.spotlight.model.SpotlightTarget
import com.banglalogic.spotlight.SpotlightListener
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
            // Steps
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

            // 🔹 Overlay & card visuals
            .overlayColor(Color.parseColor("#99000000"))   // dark overlay
            .cardBackground(Color.parseColor("#CCffffff")) // info card background

            // 🔹 Custom text styles
            .titleStyle(20f, Color.WHITE, Typeface.DEFAULT_BOLD)
            .descStyle(14f, Color.WHITE, Typeface.SANS_SERIF)

            // 🔹 Highlight config
            .highlightPadding(12) // px padding around highlight
            .shapeAnimation(ShapeAnimation.BREATHING) // NONE, PULSE, BREATHING, BOUNCE
            .useBlurOverlay(false) // blur background instead of flat dim

            // 🔹 Card positioning (AUTO, ABOVE, BELOW, LEFT, RIGHT, CENTER)
            .cardPosition(CardPosition.AUTO)

            // 🔹 Buttons
            .showButtons(true) // show Next/Skip buttons

            // 🔹 Callbacks
            .listener(object : SpotlightListener {
                override fun onStepShown(index: Int, step: SpotlightStep) {
                    Log.d("Spotlight", "Step $index shown: ${step.title}")
                }

                override fun onStepDismissed(index: Int, step: SpotlightStep) {
                    Log.d("Spotlight", "Step $index dismissed: ${step.title}")
                }

                override fun onStepNext(index: Int, step: SpotlightStep) {
                    Log.d("Spotlight", "Step $index next clicked")
                }

                override fun onStepSkipped(index: Int, step: SpotlightStep) {
                    Log.d("Spotlight", "Step $index skipped")
                }

                override fun onFinished() {
                    Log.d("Spotlight", "Tutorial finished")
                }
            })

            // finally build
            .build()

// start the sequence
        spotlight.start()
    }
}