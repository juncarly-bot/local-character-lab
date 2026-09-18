package org.characterroom.lab.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.characterroom.lab.domain.conversation.ConversationDirector
import org.characterroom.lab.runtime.PreviewCharacterModelRuntime
import org.characterroom.lab.sample.SampleWorld
import org.characterroom.lab.ui.characters.CharactersScreen
import org.characterroom.lab.ui.chat.ChatScreen
import org.characterroom.lab.ui.settings.SettingsScreen

@Composable
fun CharacterRoomApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val director = remember { ConversationDirector() }
    val runtime = remember { PreviewCharacterModelRuntime() }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf("Chat", "Characters", "Settings").forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Text(label.take(1)) },
                        label = { Text(label) },
                    )
                }
            }
        },
    ) { padding ->
        when (selectedTab) {
            0 -> ChatScreen(
                modifier = Modifier.padding(padding),
                characters = SampleWorld.characters,
                relationshipsToUser = SampleWorld.relationshipsToUser,
                director = director,
                runtime = runtime,
            )

            1 -> CharactersScreen(
                modifier = Modifier.padding(padding),
                characters = SampleWorld.characters,
            )

            else -> SettingsScreen(
                modifier = Modifier.padding(padding),
            )
        }
    }
}
