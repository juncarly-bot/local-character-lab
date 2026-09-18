package org.characterroom.lab.domain.conversation

import org.characterroom.lab.domain.model.CharacterRuntimeState

data class ConversationSignals(
    val directlyAddressedCharacterId: String?,
    val isQuestion: Boolean,
)

class ConversationSignalParser {

    fun parse(
        text: String,
        characters: List<CharacterRuntimeState>,
    ): ConversationSignals {
        val normalized = text.trim()
        val addressed = characters
            .firstOrNull { character ->
                val name = character.profile.name
                val id = character.profile.id
                looksAddressed(normalized, name) || looksAddressed(normalized, id)
            }
            ?.profile
            ?.id

        return ConversationSignals(
            directlyAddressedCharacterId = addressed,
            isQuestion = looksLikeQuestion(normalized),
        )
    }

    private fun looksAddressed(
        text: String,
        token: String,
    ): Boolean {
        if (token.isBlank()) return false

        val escaped = Regex.escape(token)
        val pattern = Regex(
            pattern = "(^|[\\s、,。.!！?？@])$escaped(さん|ちゃん|くん|様|さま|は|って|、|,|。|\\s|\\z|[!！?？])",
            option = RegexOption.IGNORE_CASE,
        )
        return pattern.containsMatchIn(text)
    }

    private fun looksLikeQuestion(text: String): Boolean {
        if (text.contains("?") || text.contains("？")) return true

        val endings = listOf(
            "ですか",
            "ますか",
            "かな",
            "かしら",
            "どう思う",
            "どう思います",
            "教えて",
            "知ってる",
            "知っています",
        )

        return endings.any { hint -> text.contains(hint) }
    }
}
