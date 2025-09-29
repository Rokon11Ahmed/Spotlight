package com.banglalogic.spotlight.model


data class SpotlightStep(
    val target: SpotlightTarget,
    val title: String,
    val description: String,
    val shape: SpotlightShape
)

enum class SpotlightShape { CIRCLE, RECTANGLE, ROUNDED_RECT, OVAL }

enum class CardPosition { AUTO, ABOVE, BELOW, LEFT, RIGHT, CENTER }

enum class ShapeAnimation { NONE, PULSE, BREATHING, BOUNCE }
