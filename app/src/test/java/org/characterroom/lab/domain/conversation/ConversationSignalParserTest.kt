package org.characterroom.lab.domain.conversation

import org.characterroom.lab.sample.SampleWorld
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConversationSignalParserTest {

    private val parser = ConversationSignalParser()

    @Test
    fun detectsDirectAddressAndQuestion() {
        val signals = parser.parse(
            text = "TTはどう思う？",
            characters = SampleWorld.characters,
        )

        assertEquals("tt", signals.directlyAddressedCharacterId)
        assertTrue(signals.isQuestion)
    }

    @Test
    fun neutralStatementHasNoForcedTarget() {
        val signals = parser.parse(
            text = "今日は少し眠い",
            characters = SampleWorld.characters,
        )

        assertEquals(null, signals.directlyAddressedCharacterId)
        assertFalse(signals.isQuestion)
    }
}
