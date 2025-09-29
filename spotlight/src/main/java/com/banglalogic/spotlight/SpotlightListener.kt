package com.banglalogic.spotlight

import com.banglalogic.spotlight.model.SpotlightStep

interface SpotlightListener {
    /**
     * Called when a step is first shown on screen.
     */
    fun onStepShown(index: Int, step: SpotlightStep) {}

    /**
     * Called when the user taps overlay (or auto-dismiss) for a step.
     */
    fun onStepDismissed(index: Int, step: SpotlightStep) {}

    /**
     * Called when the "Next" button is pressed.
     */
    fun onStepNext(index: Int, step: SpotlightStep) {}

    /**
     * Called when the "Skip" button is pressed.
     */
    fun onStepSkipped(index: Int, step: SpotlightStep) {}

    /**
     * Called once the spotlight tutorial flow has ended (last step or skipped).
     */
    fun onFinished() {}
}