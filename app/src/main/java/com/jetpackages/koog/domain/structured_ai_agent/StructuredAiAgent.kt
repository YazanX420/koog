package com.jetpackages.koog.domain.structured_ai_agent

/**
 * A generic interface for an AI agent that takes a specific input type
 * and generates a structured output of a specific type.
 *
 * @param I The data type of the input (e.g., String for a user query).
 * @param O The data type of the structured output (e.g., a data class).
 */
interface StructuredAiAgent<in I, out O> {

    /**
     * Generates a structured response from the AI model.
     *
     * @param input The user's input for this specific request.
     * @param systemPrompt The core instructions or "personality" for the agent.
     * @return An instance of the output data class `O`, or null if the generation failed.
     */
    suspend fun generateResponse(input: I, systemPrompt: String): O?
}