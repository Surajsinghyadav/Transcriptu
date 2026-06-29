package com.example.transcriptu.presentation.reusablecomponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.transcriptu.ui.theme.TranscriptuTheme

@Composable
fun PlainTranscriptCard(
    transcript: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(CutCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp)),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = transcript,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = Int.MAX_VALUE,
                overflow = androidx.compose.ui.text.style.TextOverflow.Visible
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlainTranscriptCardPreview() {
    TranscriptuTheme {
        PlainTranscriptCard(
            transcript = "This is a sample transcript that demonstrates the layout of the PlainTranscriptCard. It should wrap text correctly and have proper padding inside the surface.",
            modifier = Modifier.padding(16.dp)
        )
    }
}
