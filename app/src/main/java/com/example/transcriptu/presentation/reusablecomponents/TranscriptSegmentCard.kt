package com.transcriptapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.transcriptu.data.modal.Transcript

data class TranscriptSegment(
    val id: String,
    val timestampSeconds: Long,
    val text: String,
)

fun Long.toTimestampString(): String {
    val minutes = this / 60
    val seconds = this % 60
    return "%02d:%02d".format(minutes, seconds)
}

/**
 * A single transcript segment row with timestamp pill and expandable full text.
 * Tapping the row toggles expansion.
 */
@Composable
fun TranscriptSegmentCard(
    segment: Transcript,
    isHighlighted: Boolean = false,
    showTimestamp: Boolean = true,
    modifier: Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val backgroundColor = if (isHighlighted)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    else
        MaterialTheme.colorScheme.surface

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { expanded = !expanded },
        color = backgroundColor,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = if (isHighlighted) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Timestamp pill
            if (showTimestamp) {
                TimestampPill(
                    timestamp = segment.offset,
                    onClick = {}
                )
                Spacer(Modifier.width(12.dp))

            // Text — collapses to 2 lines by default
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = segment.text!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                AnimatedVisibility(
                    visible = segment.text.length > 120 && !expanded,
                    enter = expandVertically(tween(200)),
                    exit = shrinkVertically(tween(200))
                ) {
                    Text(
                        text = "Tap to expand",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }


        }
    }
}

@Composable
fun TimestampPill(
    timestamp: String,
    onClick: (() -> Unit)? = null,
) {
    val modifier = if (onClick != null)
        Modifier.clickable(onClick = onClick)
    else
        Modifier

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
