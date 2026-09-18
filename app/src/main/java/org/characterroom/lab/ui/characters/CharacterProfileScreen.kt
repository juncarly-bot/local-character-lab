package org.characterroom.lab.ui.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.characterroom.lab.domain.model.CharacterRuntimeState
import org.characterroom.lab.domain.model.Relationship

@Composable
fun CharacterProfileScreen(
    character: CharacterRuntimeState,
    relationshipToUser: Relationship?,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = character.profile.name,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(character.profile.description)
                }
                Button(onClick = onBack) {
                    Text("戻る")
                }
            }
        }

        item {
            ProfileSection("Overview") {
                ProfileLine("Life", character.lifeState.name)
                ProfileLine("Room", character.roomState.name)
                ProfileLine("Mood", character.mood.label)
                ProfileLine("Valence", character.mood.valence.toString())
                ProfileLine("Energy", character.mood.energy.toString())
                ProfileLine("Social energy", character.mood.socialEnergy.toString())
                ProfileLine("Sleepiness", character.mood.sleepiness.toString())
            }
        }

        item {
            ProfileSection("Relationship to USER") {
                if (relationshipToUser == null) {
                    Text("まだRelationshipがありません。")
                } else {
                    ProfileLine("Familiarity", relationshipToUser.familiarity.toString())
                    ProfileLine("Affection", relationshipToUser.affection.toString())
                    ProfileLine("Trust", relationshipToUser.trust.toString())
                    ProfileLine("Comfort", relationshipToUser.comfort.toString())
                    ProfileLine("Tension", relationshipToUser.tension.toString())
                }
            }
        }

        item {
            ProfileSection("Personality") {
                val p = character.profile.personality
                ProfileLine("Sociability", p.sociability.toString())
                ProfileLine("Initiative", p.initiative.toString())
                ProfileLine("Curiosity", p.curiosity.toString())
                ProfileLine("Emotionality", p.emotionality.toString())
                ProfileLine("Humor", p.humor.toString())
                ProfileLine("Assertiveness", p.assertiveness.toString())
            }
        }

        item {
            ProfileSection("Conversation behavior") {
                val b = character.profile.behavior
                ProfileLine("Talkativeness", b.talkativeness.toString())
                ProfileLine("Interruption", b.interruption.toString())
                ProfileLine("Topic change", b.topicChange.toString())
                ProfileLine("Silence tolerance", b.silenceTolerance.toString())
            }
        }

        item {
            Text("Interests & Expertise", style = MaterialTheme.typography.titleMedium)
        }

        items(
            items = character.profile.interests.entries.toList(),
            key = { "interest-${it.key}" },
        ) { entry ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(entry.key, style = MaterialTheme.typography.titleSmall)
                    ProfileLine("Interest", entry.value.toString())
                    ProfileLine(
                        "Expertise",
                        (character.profile.expertise[entry.key] ?: 0).toString(),
                    )
                }
            }
        }

        item {
            ProfileSection("Values") {
                character.profile.values.forEach { value ->
                    Text("• $value")
                }
            }
        }

        item {
            HorizontalDivider()
            Text(
                text = "MVP: この画面は現在read-only。次段階で編集と永続化を接続します。",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun ProfileLine(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label)
        Text(value, style = MaterialTheme.typography.labelLarge)
    }
}
