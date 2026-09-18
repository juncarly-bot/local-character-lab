package org.characterroom.lab.domain.conversation

import org.characterroom.lab.domain.model.CharacterRuntimeState
import org.characterroom.lab.domain.model.LifeState
import org.characterroom.lab.domain.model.Relationship
import org.characterroom.lab.domain.model.RoomState

data class SpeakerDecisionContext(
    val topic: String = "general",
    val directlyAddressedCharacterId: String? = null,
    val recentSpeakerId: String? = null,
)

data class SpeakerScore(
    val characterId: String,
    val score: Double,
    val contributions: Map<String, Double>,
)

data class SpeakerDecision(
    val selectedCharacterId: String?,
    val scores: List<SpeakerScore>,
)

class ConversationDirector {

    fun chooseSpeaker(
        candidates: List<CharacterRuntimeState>,
        relationshipsToUser: Map<String, Relationship>,
        context: SpeakerDecisionContext,
    ): SpeakerDecision {
        val scores = candidates
            .filter { it.lifeState == LifeState.FREE && it.roomState == RoomState.IN }
            .map { character ->
                val profile = character.profile
                val relationship = relationshipsToUser[profile.id]
                val interest = profile.interests[context.topic]
                    ?: profile.interests["general"]
                    ?: 50

                val directAddress = if (context.directlyAddressedCharacterId == profile.id) 100.0 else 0.0
                val topicInterest = interest * 0.20
                val initiative = profile.personality.initiative * 0.18
                val talkativeness = profile.behavior.talkativeness * 0.18
                val sociability = profile.personality.sociability * 0.12
                val curiosity = profile.personality.curiosity * 0.07
                val relationshipComfort = (relationship?.comfort ?: 50) * 0.10
                val moodEnergy = character.mood.energy * 0.05
                val socialEnergy = character.mood.socialEnergy * 0.05
                val recentSpeakerPenalty = if (context.recentSpeakerId == profile.id) -24.0 else 0.0

                val contributions = linkedMapOf(
                    "direct_address" to directAddress,
                    "topic_interest" to topicInterest,
                    "initiative" to initiative,
                    "talkativeness" to talkativeness,
                    "sociability" to sociability,
                    "curiosity" to curiosity,
                    "relationship_comfort" to relationshipComfort,
                    "mood_energy" to moodEnergy,
                    "social_energy" to socialEnergy,
                    "recent_speaker" to recentSpeakerPenalty,
                )

                SpeakerScore(
                    characterId = profile.id,
                    score = contributions.values.sum(),
                    contributions = contributions,
                )
            }
            .sortedByDescending { it.score }

        return SpeakerDecision(
            selectedCharacterId = scores.firstOrNull()?.characterId,
            scores = scores,
        )
    }
}
