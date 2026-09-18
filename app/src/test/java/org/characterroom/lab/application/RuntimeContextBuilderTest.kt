package org.characterroom.lab.application

import org.characterroom.lab.domain.model.ChatMessage
import org.characterroom.lab.domain.model.ConversationAct
import org.characterroom.lab.sample.SampleWorld
import org.junit.Assert.assertEquals
import org.junit.Test

class RuntimeContextBuilderTest {

    @Test
    fun limitsRecentTranscriptAndKeepsSelectedAct() {
        val character = SampleWorld.characters.first { it.profile.id == "sayo" }
        val messages = (1L..12L).map { id ->
            ChatMessage(
                id = id,
                speakerId = "user",
                speakerName = "USER",
                text = "message $id",
                isUser = true,
            )
        }

        val context = RuntimeContextBuilder(recentTranscriptLimit = 8).build(
            character = character,
            relationshipToUser = SampleWorld.relationshipsToUser["sayo"],
            topic = "general",
            act = ConversationAct.QUESTION,
            userText = "続きは？",
            recentMessages = messages,
        )

        assertEquals(8, context.recentTranscript.size)
        assertEquals("USER: message 5", context.recentTranscript.first())
        assertEquals(ConversationAct.QUESTION, context.conversationAct)
    }
}
