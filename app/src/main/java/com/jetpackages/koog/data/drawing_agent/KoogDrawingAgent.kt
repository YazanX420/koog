package com.jetpackages.koog.data.drawing_agent

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.structure.executeStructured
import ai.koog.prompt.structure.json.JsonSchemaGenerator
import ai.koog.prompt.structure.json.JsonStructuredData
import com.jetpackages.koog.domain.model.DrawingInstructions
import com.jetpackages.koog.domain.structured_ai_agent.StructuredAiAgent

/**
 * A concrete implementation of the StructuredAiAgent that uses the Koog framework
 * to generate drawing instructions from a text prompt.
 *
 * @param executor The Koog PromptExecutor used to communicate with the LLM.
 */
class KoogDrawingAgent(
    private val executor: PromptExecutor
) : StructuredAiAgent<String, DrawingInstructions> {

    // The schema generation is part of the agent's setup, so we create it once.
    private val drawingInstructionsStructure = JsonStructuredData.createJsonStructure<DrawingInstructions>(
        schemaFormat = JsonSchemaGenerator.SchemaFormat.JsonSchema,
        schemaType = JsonStructuredData.JsonSchemaType.SIMPLE
    )

    override suspend fun generateResponse(
        input: String,
        systemPrompt: String
    ): DrawingInstructions? {
        // This is the same logic you had before, now nicely encapsulated.
        val result = executor.executeStructured(
            prompt = prompt("drawing-prompt") {
                system(systemPrompt)
                user(input)
            },
            structure = drawingInstructionsStructure,
            mainModel = GoogleModels.Gemini1_5FlashLatest,
            fixingModel = GoogleModels.Gemini1_5FlashLatest
        )

        return result.getOrNull()?.structure
    }
}