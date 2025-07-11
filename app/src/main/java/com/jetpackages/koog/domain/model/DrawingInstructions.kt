package com.jetpackages.koog.domain.model

import ai.koog.agents.core.tools.annotations.LLMDescription
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The root object for our drawing instructions.
 * The AI will populate an instance of this class.
 */
@Serializable
@SerialName("DrawingInstructions") // As shown in the docs for good practice
@LLMDescription("A set of instructions to draw shapes on a canvas based on a user's request.")
data class DrawingInstructions(
    // Use @property:LLMDescription as shown in the docs
    @property:LLMDescription("A list of shapes that should be drawn on the canvas to fulfill the user's request.")
    val shapes: List<Shape>
)

/**
 * Represents a single shape to be drawn.
 */
@Serializable
@SerialName("Shape")
@LLMDescription("A single geometric shape with a specific type, color, and position.")
data class Shape(
    @property:LLMDescription("The type of shape to draw. Can be RECTANGLE or OVAL.")
    val type: ShapeType,
    @property:LLMDescription("The color of the shape as a common color name, e.g., 'red', 'blue', 'green'.")
    val color: String,
    @property:LLMDescription("The X coordinate of the shape's top-left corner. Must be between 0.0 and 100.0.")
    val x: Float,
    @property:LLMDescription("The Y coordinate of the shape's top-left corner. Must be between 0.0 and 100.0.")
    val y: Float,
    @property:LLMDescription("The width of the shape. Must be between 0.0 and 100.0.")
    val width: Float,
    @property:LLMDescription("The height of the shape. Must be between 0.0 and 100.0.")
    val height: Float
)

/**
 * An enumeration of the supported shape types.
 */
@Serializable
@SerialName("ShapeType")
enum class ShapeType {
    RECTANGLE, OVAL
}

/**
 * A handy extension function to convert the AI's color string (e.g., "red")
 * into a Color object that Jetpack Compose can use.
 */
fun String.toComposeColor(): Color {
    return when (this.lowercase()) {
        "red" -> Color.Red
        "green" -> Color.Green
        "blue" -> Color.Blue
        "yellow" -> Color.Yellow
        "cyan" -> Color.Cyan
        "magenta" -> Color.Magenta
        "black" -> Color.Black
        "white" -> Color.White
        "gray" -> Color.Gray
        else -> Color.Black // Default color if the name is not recognized
    }
}