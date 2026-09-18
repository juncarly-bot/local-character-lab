package org.characterroom.lab.application

import org.characterroom.lab.domain.model.ChatMessage
import org.characterroom.lab.domain.model.CharacterRuntimeState
import org.characterroom.lab.domain.model.ConversationAct
import org.characterroom.lab.domain.model.Relationship

data class CharacterRuntimeContext(
    val characterId: String,
    val characterName: String,
    val characterDescription: String,
    val moodLabel: String,
    val topic: String,
    val conversationAct: ConversationAct,
    val topicInterest: Int,
    val topicExpertise: Int,
    val relationshipToUser: Relationship?,
    val userText: String,
    val recentTranscript: List<String>,
    val directorInstruction: String,
)

class RuntimeContextBuilder(
    private val recentTranscriptLimit: Int = 8,
) {

    fun build(
        character: CharacterRuntimeState,
        relationshipToUser: Relationship?,
        topic: String,
        act: ConversationAct,
        userText: String,
        recentMessages: List<ChatMessage>,
    ): CharacterRuntimeContext {
        val profile = character.profile
        val interest = profile.interests[topic]
            ?: profile.interests["general"]
            ?: 50
        val expertise = profile.expertise[topic]
            ?: profile.expertise["general"]
            ?: 20

        val transcript = recentMessages
            .takeLast(recentTranscriptLimit)
            .map { message -> "${message.speakerName}: ${message.text}" }

        val instruction = when (act) {
            ConversationAct.KNOWLEDGE -> "Answer with useful knowledge, but do not exceed the character's expertise."
            ConversationAct.QUESTION -> "Ask one natural question that moves the conversation forward."
            ConversationAct.AGREEMENT -> "Respond with agreement without repeating the previous message."
            ConversationAct.COUNTERPOINT -> "Offer a respectful counterpoint."
            ConversationAct.BACKCHANNEL -> "Give a short natural backchannel response."
            ConversationAct.PERSONAL_REACTION -> "Give the character's personal reaction."
            ConversationAct.TOPIC_SHIFT -> "Shift gently to a related topic."
            ConversationAct.SILENCE -> "Do not produce an utterance."
        }

        return CharacterRuntimeContext(
            characterId = profile.id,
            characterName = profile.name,
            characterDescription = profile.description,
            moodLabel = character.mood.label,
            topic = topic,
            conversationAct = act,
            topicInterest = interest,
            topicExpertise = expertise,
            relationshipToUser = relationshipToUser,
            userText = userText,
            recentTranscript = transcript,
            directorInstruction = instruction,
        )
    }
}
