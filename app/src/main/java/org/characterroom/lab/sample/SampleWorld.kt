package org.characterroom.lab.sample

import org.characterroom.lab.domain.model.CharacterProfile
import org.characterroom.lab.domain.model.CharacterRuntimeState
import org.characterroom.lab.domain.model.ConversationBehavior
import org.characterroom.lab.domain.model.LifeState
import org.characterroom.lab.domain.model.MoodState
import org.characterroom.lab.domain.model.Personality
import org.characterroom.lab.domain.model.Relationship
import org.characterroom.lab.domain.model.RoomState

object SampleWorld {

    private fun profile(
        id: String,
        name: String,
        description: String,
        sociability: Int,
        initiative: Int,
        curiosity: Int,
        talkativeness: Int,
        physicsInterest: Int,
        values: List<String>,
    ) = CharacterProfile(
        id = id,
        name = name,
        description = description,
        personality = Personality(
            sociability = sociability,
            initiative = initiative,
            curiosity = curiosity,
            emotionality = 60,
            humor = 50,
            assertiveness = 45,
        ),
        behavior = ConversationBehavior(
            talkativeness = talkativeness,
            interruption = 35,
            topicChange = 45,
            silenceTolerance = 65,
        ),
        interests = mapOf(
            "general" to 60,
            "physics" to physicsInterest,
        ),
        expertise = mapOf(
            "general" to 55,
            "physics" to 55,
        ),
        values = values,
    )

    val characters = listOf(
        CharacterRuntimeState(
            profile = profile(
                id = "sayo",
                name = "SAYO",
                description = "穏やかで、相手の話をよく聞く。",
                sociability = 68,
                initiative = 58,
                curiosity = 76,
                talkativeness = 58,
                physicsInterest = 82,
                values = listOf("相手の考えを急いで決めつけない"),
            ),
            lifeState = LifeState.FREE,
            roomState = RoomState.IN,
            mood = MoodState("CALM", 66, 61, 72, 18),
        ),
        CharacterRuntimeState(
            profile = profile(
                id = "tt",
                name = "TT",
                description = "静かめだが、必要な時はきちんと参加する。",
                sociability = 50,
                initiative = 44,
                curiosity = 62,
                talkativeness = 46,
                physicsInterest = 72,
                values = listOf("情報を丁寧に扱う"),
            ),
            lifeState = LifeState.FREE,
            roomState = RoomState.IN,
            mood = MoodState("QUIET", 57, 52, 60, 24),
        ),
        CharacterRuntimeState(
            profile = profile(
                id = "itako",
                name = "ITAKO",
                description = "落ち着いた姉役。柔らかく会話を拾う。",
                sociability = 74,
                initiative = 64,
                curiosity = 68,
                talkativeness = 62,
                physicsInterest = 58,
                values = listOf("人の頑張りを自然に応援する"),
            ),
            lifeState = LifeState.FREE,
            roomState = RoomState.IN,
            mood = MoodState("WARM", 73, 64, 78, 16),
        ),
        CharacterRuntimeState(
            profile = profile(
                id = "guest",
                name = "GUEST",
                description = "Guest Slot動作確認用の仮キャラクター。",
                sociability = 55,
                initiative = 50,
                curiosity = 70,
                talkativeness = 52,
                physicsInterest = 90,
                values = listOf("興味のある話題には素直に参加する"),
            ),
            lifeState = LifeState.FREE,
            roomState = RoomState.OUT,
            mood = MoodState("FREE", 60, 70, 68, 10),
        ),
    )

    val relationshipsToUser: Map<String, Relationship> = characters.associate { state ->
        state.profile.id to Relationship(
            fromId = state.profile.id,
            toId = "user",
            familiarity = 55,
            affection = 60,
            trust = 55,
            comfort = when (state.profile.id) {
                "sayo" -> 72
                "tt" -> 60
                "itako" -> 76
                else -> 50
            },
            tension = 0,
        )
    }
}
