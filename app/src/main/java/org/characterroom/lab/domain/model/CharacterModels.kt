package org.characterroom.lab.domain.model

enum class LifeState {
    SLEEP,
    FREE,
}

enum class RoomState {
    IN,
    OUT,
}

enum class ConversationAct {
    KNOWLEDGE,
    QUESTION,
    AGREEMENT,
    COUNTERPOINT,
    BACKCHANNEL,
    PERSONAL_REACTION,
    TOPIC_SHIFT,
    SILENCE,
}

data class Personality(
    val sociability: Int,
    val initiative: Int,
    val curiosity: Int,
    val emotionality: Int,
    val humor: Int,
    val assertiveness: Int,
)

data class ConversationBehavior(
    val talkativeness: Int,
    val interruption: Int,
    val topicChange: Int,
    val silenceTolerance: Int,
)

data class CharacterProfile(
    val id: String,
    val name: String,
    val description: String,
    val personality: Personality,
    val behavior: ConversationBehavior,
    val interests: Map<String, Int>,
    val expertise: Map<String, Int>,
    val values: List<String>,
)

data class MoodState(
    val label: String,
    val valence: Int,
    val energy: Int,
    val socialEnergy: Int,
    val sleepiness: Int,
)

data class CharacterRuntimeState(
    val profile: CharacterProfile,
    val lifeState: LifeState,
    val roomState: RoomState,
    val mood: MoodState,
)

data class Relationship(
    val fromId: String,
    val toId: String,
    val familiarity: Int,
    val affection: Int,
    val trust: Int,
    val comfort: Int,
    val tension: Int,
)

data class ChatMessage(
    val id: Long,
    val speakerId: String,
    val speakerName: String,
    val text: String,
    val isUser: Boolean = false,
)
