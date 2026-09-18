package org.characterroom.lab.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.characterroom.lab.domain.conversation.ConversationDirector
import org.characterroom.lab.domain.conversation.SpeakerDecisionContext
import org.characterroom.lab.domain.model.ChatMessage
import org.characterroom.lab.domain.model.CharacterRuntimeState
import org.characterroom.lab.domain.model.Relationship
import org.characterroom.lab.domain.model.RoomState
import org.characterroom.lab.runtime.CharacterGenerationRequest
import org.characterroom.lab.runtime.CharacterModelRuntime

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    characters: List<CharacterRuntimeState>,
    relationshipsToUser: Map<String, Relationship>,
    director: ConversationDirector,
    runtime: CharacterModelRuntime,
) {
    val roomCharacters = remember(characters) {
        characters.filter { it.roomState == RoomState.IN }
    }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                id = 1L,
                speakerId = "system",
                speakerName = "ROOM",
                text = "Character Room Lab MVP skeleton",
            ),
        )
    }
    var input by remember { mutableStateOf("") }
    var nextId by remember { mutableLongStateOf(2L) }
    var recentSpeakerId by remember { mutableStateOf<String?>(null) }
    var generating by remember { mutableStateOf(false) }
    var lastRuntime by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Character Room",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "Resident 3 + Guest Slot",
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            roomCharacters.forEach { character ->
                AssistChip(
                    onClick = { },
                    label = {
                        Text("${character.profile.name} · ${character.mood.label}")
                    },
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "+ Guest Slot · OUT",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(messages, key = { it.id }) { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(),
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = message.speakerName,
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }

        lastRuntime?.let {
            Text(
                text = "runtime: $it",
                style = MaterialTheme.typography.labelSmall,
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = input,
                onValueChange = { input = it },
                enabled = !generating,
                placeholder = { Text("Roomへ話しかける") },
                singleLine = true,
            )

            Button(
                enabled = input.isNotBlank() && !generating,
                onClick = {
                    val userText = input.trim()
                    input = ""
                    messages += ChatMessage(
                        id = nextId++,
                        speakerId = "user",
                        speakerName = "USER",
                        text = userText,
                        isUser = true,
                    )
                    generating = true

                    val decision = director.chooseSpeaker(
                        candidates = roomCharacters,
                        relationshipsToUser = relationshipsToUser,
                        context = SpeakerDecisionContext(
                            recentSpeakerId = recentSpeakerId,
                        ),
                    )
                    val selected = roomCharacters.firstOrNull {
                        it.profile.id == decision.selectedCharacterId
                    }

                    if (selected == null) {
                        generating = false
                        messages += ChatMessage(
                            id = nextId++,
                            speakerId = "system",
                            speakerName = "ROOM",
                            text = "今は誰も話さなかった。",
                        )
                    } else {
                        scope.launch {
                            val result = runtime.generate(
                                CharacterGenerationRequest(
                                    character = selected.profile,
                                    userText = userText,
                                ),
                            )
                            recentSpeakerId = selected.profile.id
                            lastRuntime = result.runtimeName
                            messages += ChatMessage(
                                id = nextId++,
                                speakerId = selected.profile.id,
                                speakerName = selected.profile.name,
                                text = result.text,
                            )
                            generating = false
                        }
                    }
                },
            ) {
                Text(if (generating) "..." else "送信")
            }
        }
    }
}
