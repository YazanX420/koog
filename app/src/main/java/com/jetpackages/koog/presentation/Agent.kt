package com.jetpackages.koog.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jetpackages.koog.R
import com.jetpackages.koog.domain.ShapeType
import com.jetpackages.koog.domain.toComposeColor

/**
 * This Agent will take user inputs about drawing anything, and send it to the VM.
 * It also displays the final drawing on a Canvas.
 **/
@Composable
fun Agent(modifier: Modifier = Modifier) {
    val appName = stringResource(R.string.app_name)

    val viewModel = viewModel<AgentViewModel>()

    val query by viewModel.query.collectAsStateWithLifecycle()
    val drawingInstructions by viewModel.drawingInstructions.collectAsStateWithLifecycle()

    val onClick = { viewModel.draw() }
    val updateQuery = { newQuery: String -> viewModel.updateQuery(newQuery) }

    CenteredColumn(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Hello I'm $appName",
            modifier = Modifier,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif
        )

        Spacer(modifier = Modifier.height(8.dp))

        // This is the Canvas where the AI's instructions will be drawn.
        Canvas(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .border(2.dp, Color.LightGray)
        ) {
            // When the instructions are not null, loop through and draw each shape.
            drawingInstructions?.shapes?.forEach { shape ->
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Convert the AI's 100x100 grid coordinates to the actual canvas pixel coordinates
                val topLeft = Offset(
                    x = (shape.x / 100f) * canvasWidth,
                    y = (shape.y / 100f) * canvasHeight
                )
                val drawSize = Size(
                    width = (shape.width / 100f) * canvasWidth,
                    height = (shape.height / 100f) * canvasHeight
                )

                when (shape.type) {
                    ShapeType.RECTANGLE -> {
                        drawRect(
                            color = shape.color.toComposeColor(),
                            topLeft = topLeft,
                            size = drawSize
                        )
                    }
                    ShapeType.OVAL -> {
                        drawOval(
                            color = shape.color.toComposeColor(),
                            topLeft = topLeft,
                            size = drawSize
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "How can I help you today?",
            modifier = Modifier,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = query,
            onValueChange = updateQuery,
            placeholder = { Text("e.g., a green field and a blue sky") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onClick,
            modifier = Modifier,
            content = { Text(text = "Draw!") },
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
        )
    }
}