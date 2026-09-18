package org.characterroom.lab.domain.conversation

import org.characterroom.lab.domain.model.ConversationAct
import org.characterroom.lab.domain.model.RoomState
import org.characterroom.lab.sample.SampleWorld
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ConversationDirectorTest {

    private val director = ConversationDirector()

    @Test
    fun directlyAddressedCharacterWins() {
        val decision = director.chooseSpeaker(
            candidates = SampleWorld.characters,
            relationshipsToUser = SampleWorld.relationshipsToUser,
            context = SpeakerDecisionContext(
                directlyAddressedCharacterId = "tt",
            ),
        )

        assertEquals("tt", decision.selectedCharacterId)
    }

    @Test
    fun roomOutCharacterIsNotCandidate() {
        val decision = director.chooseSpeaker(
            candidates = SampleWorld.characters,
            relationshipsToUser = SampleWorld.relationshipsToUser,
            context = SpeakerDecisionContext(
                directlyAddressedCharacterId = "guest",
            ),
        )

        assertFalse(
            decision.scores.any { it.characterId == "guest" },
        )
        assertEquals(
            RoomState.OUT,
            SampleWorld.characters.first { it.profile.id == "guest" }.roomState,
        )
    }

    @Test
    fun recentSpeakerPenaltyIsVisibleInDeveloperBreakdown() {
        val decision = director.chooseSpeaker(
            candidates = SampleWorld.characters,
            relationshipsToUser = SampleWorld.relationshipsToUser,
            context = SpeakerDecisionContext(
                recentSpeakerId = "itako",
            ),
        )

        val score = decision.scores.first { it.characterId == "itako" }
        assertEquals(-24.0, score.contributions.getValue("recent_speaker"), 0.001)
    }

    @Test
    fun knowledgeCooldownPrefersQuestionForCuriousCharacter() {
        val sayo = SampleWorld.characters.first { it.profile.id == "sayo" }

        val act = director.chooseAct(
            character = sayo,
            topic = "general",
            signals = ConversationSignals(
                directlyAddressedCharacterId = null,
                isQuestion = false,
            ),
            recentAct = ConversationAct.KNOWLEDGE,
        )

        assertEquals(ConversationAct.QUESTION, act)
    }

    @Test
    fun questionUsesKnowledgeWhenExpertiseIsHigh() {
        val tt = SampleWorld.characters.first { it.profile.id == "tt" }
        val expertTt = tt.copy(
            profile = tt.profile.copy(
                expertise = tt.profile.expertise + ("general" to 90),
            ),
        )

        val act = director.chooseAct(
            character = expertTt,
            topic = "general",
            signals = ConversationSignals(
                directlyAddressedCharacterId = "tt",
                isQuestion = true,
            ),
            recentAct = null,
        )

        assertEquals(ConversationAct.KNOWLEDGE, act)
    }
}
