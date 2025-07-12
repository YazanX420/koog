package com.jetpackages.koog.presentation

import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackages.koog.data.Keys.CHATGPT_API_KEY
import com.jetpackages.koog.data.Keys.GEMINI_API_KEY
import com.jetpackages.koog.data.drawing_agent.KoogDrawingAgent
import com.jetpackages.koog.domain.model.DrawingInstructions
import com.jetpackages.koog.domain.structured_ai_agent.StructuredAiAgent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * This VM coordinates the UI state and delegates the AI logic to a StructuredAiAgent.
 * It is now completely decoupled from the specific Koog implementation.
 **/
class AgentViewModel : ViewModel() {

    private val _query = MutableStateFlow(
        """
            A vibrant green field covering the bottom third of the canvas, with a bright blue sky above it. A large, yellow sun shines from the top-right corner. 
            Three fluffy, white clouds drift near the center of the sky. In the foreground on the left, add a small patch of three little red roses.
        """.trimIndent()
    )
    val query = _query.asStateFlow()

    private val _drawingInstructions = MutableStateFlow<DrawingInstructions?>(null)
    val drawingInstructions = _drawingInstructions.asStateFlow()

    // --- 1. Define the Agent's Core "Personality" ---
    private val drawingSystemPrompt = """
        You are an expert drawing assistant. Your task is to convert a user's textual
        description into a structured list of shapes to be drawn on a 100x100 canvas.
        The origin (0,0) is the top-left corner.
        The point (100,100) is the bottom-right corner.
        Analyze the user's request and provide the corresponding shape instructions.
    """.trimIndent()

    // --- 2. Create an instance of our agent using the interface ---
    // We can easily swap KoogDrawingAgent with another implementation in the future.
    private val geminiDrawingAgent: StructuredAiAgent<String, DrawingInstructions> =
        KoogDrawingAgent(executor = simpleGoogleAIExecutor(GEMINI_API_KEY))

    private val chatGptDrawingAgent: StructuredAiAgent<String, DrawingInstructions> =
        KoogDrawingAgent(executor = simpleOpenAIExecutor(CHATGPT_API_KEY))

    private val agent = chatGptDrawingAgent

    /**
     * This function now simply calls our abstracted agent.
     */
    fun draw() {
        if (query.value.isBlank()) return

        viewModelScope.launch {
            val result = agent.generateResponse(
                input = query.value,
                systemPrompt = drawingSystemPrompt
            )
            _drawingInstructions.value = result
        }
    }

    fun updateQuery(query: String) {
        _query.value = query
    }
}