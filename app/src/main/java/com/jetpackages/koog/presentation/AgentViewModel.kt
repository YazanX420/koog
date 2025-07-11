package com.jetpackages.koog.presentation

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.structure.executeStructured
import ai.koog.prompt.structure.json.JsonSchemaGenerator
import ai.koog.prompt.structure.json.JsonStructuredData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackages.koog.data.Keys.GEMINI_API_KEY
import com.jetpackages.koog.domain.DrawingInstructions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * This VM will take the user query, and use a Koog AI Agent to convert it
 * into a structured list of coordinates for drawing on the UI (Compose Canvas).
 **/
class AgentViewModel : ViewModel() {

    private val _query = MutableStateFlow(
        """
            A vibrant green field covering the bottom third of the canvas, with a bright blue sky above it. A large, yellow sun shines from the top-right corner. 
            Three fluffy, white clouds drift near the center of the sky. In the foreground on the left, add a small patch of three little red roses."
        """.trimIndent()
    )
    val query = _query.asStateFlow()

    private val _drawingInstructions = MutableStateFlow<DrawingInstructions?>(null)
    val drawingInstructions = _drawingInstructions.asStateFlow()

    // --- 1. Create a Prompt Executor (as you already had) ---
    private val executor = simpleGoogleAIExecutor(GEMINI_API_KEY)

    // --- 2. Generate the JSON Schema from our Data Class ---
    // This creates the `structure` object required by the `executeStructured` function.
    private val drawingInstructionsStructure = JsonStructuredData.createJsonStructure<DrawingInstructions>(
        schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
        schemaType = JsonStructuredData.JsonSchemaType.SIMPLE
    )

    /**
     * This function is called when the user clicks the "Draw!" button.
     * It launches a coroutine to run our Koog agent.
     */
    fun draw() {
        if (query.value.isBlank()) return

        viewModelScope.launch {
            // --- 3. Call executeStructured ---
            // This is the correct function from the documentation for a single, structured call.
            val result = executor.executeStructured(
                // The prompt contains the system instructions and the user's specific query
                prompt = prompt("drawing-prompt") {
                    system(
                        """
                        You are an expert drawing assistant. Your task is to convert a user's textual
                        description into a structured list of shapes to be drawn on a 100x100 canvas.
                        The origin (0,0) is the top-left corner.
                        The point (100,100) is the bottom-right corner.
                        Analyze the user's request and provide the corresponding shape instructions.
                        """
                    )
                    user(query.value)
                },
                // Pass the schema we generated
                structure = drawingInstructionsStructure,
                // Define the model to use for the main request and for fixing any errors
                mainModel = GoogleModels.Gemini1_5FlashLatest,
                fixingModel = GoogleModels.Gemini1_5FlashLatest
            )

            // Update the state with the AI's response to trigger a UI redraw.
            _drawingInstructions.value = result.getOrNull()?.structure
        }
    }

    fun updateQuery(query: String) {
        _query.value = query
    }
}