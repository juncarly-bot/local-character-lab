package org.characterroom.lab.runtime

import kotlinx.coroutines.delay
import org.characterroom.lab.domain.model.CharacterProfile

data class CharacterGenerationRequest(
    val character: CharacterProfile,
    val userText: String,
    val topic: String = "general",
)

data class CharacterGenerationResult(
    val text: String,
    val runtimeName: String,
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
        delay(180)

        val text = when (request.character.id) {
            "sayo" -> "うん、聞いてる。もう少しその話を聞かせて。"
            "tt" -> "承知しました。いまの話を少し整理しながら考えます。"
            "itako" -> "それは気になりますわね。もう少し聞かせてくださる？"
            else -> "その話、もう少し聞いてみたい。"
        }

        return CharacterGenerationResult(
            text = text,
            runtimeName = "preview-runtime",
        )
    }
}
