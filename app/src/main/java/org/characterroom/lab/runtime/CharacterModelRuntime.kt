package org.characterroom.lab.runtime

import kotlinx.coroutines.delay
import org.characterroom.lab.application.CharacterRuntimeContext

data class CharacterGenerationRequest(
    val context: CharacterRuntimeContext,
)

data class CharacterGenerationResult(
    val text: String,
    val runtimeName: String,
    val generationLatencyMs: Long,
)

interface CharacterModelRuntime {
    suspend fun generate(request: CharacterGenerationRequest): CharacterGenerationResult
}

/**
 * Development-only stand-in until the local Main LLM runtime is connected.
 * Character state stays outside this runtime so replacing it will not erase
 * Profile / Relationship / Memory data.
 */
class PreviewCharacterModelRuntime : CharacterModelRuntime {
    override suspend fun generate(request: CharacterGenerationRequest): CharacterGenerationResult {
        val startedAt = System.nanoTime()
        delay(180)

        val text = when (request.context.characterId) {
            "sayo" -> "うん、聞いてる。もう少しその話を聞かせて。"
            "tt" -> "承知しました。いまの話を少し整理しながら考えます。"
            "itako" -> "それは気になりますわね。もう少し聞かせてくださる？"
            else -> "その話、もう少し聞いてみたい。"
        }

        return CharacterGenerationResult(
            text = text,
            runtimeName = "preview-runtime",
            generationLatencyMs = (System.nanoTime() - startedAt) / 1_000_000L,
        )
    }
}
