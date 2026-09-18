package org.characterroom.lab.ui.characters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.characterroom.lab.domain.model.CharacterRuntimeState

@Composable
fun CharactersScreen(
    modifier: Modifier = Modifier,
    characters: List<CharacterRuntimeState>,
    onCharacterSelected: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text(
                text = "Characters",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "同じCharacter ProfileをChatと一覧の両方から開けます。",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        items(characters, key = { it.profile.id }) { character ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCharacterSelected(character.profile.id) },
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        text = character.profile.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(character.profile.description)
                    Text(
                        text = "${character.lifeState} / Room ${character.roomState}",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = "Mood: ${character.mood.label} · social ${character.mood.socialEnergy}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
