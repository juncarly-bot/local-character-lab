package org.characterroom.lab.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
        )

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text("Model Library", style = MaterialTheme.typography.titleMedium)
                Text("Main: 8B / 4B / 2B")
                Text("Helper: 0.6B-class candidate")
                Text("MVP skeletonではまだPreview Runtimeを使用")
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text("Latency target", style = MaterialTheme.typography.titleMedium)
                Text("Normal foreground turn: <= 10 sec")
                Text("Main LLM call: 原則1回を第一目標")
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text("Developer Mode", style = MaterialTheme.typography.titleMedium)
                Text("Speaker score / Context / latency表示を次段階で実装")
            }
        }
    }
}
