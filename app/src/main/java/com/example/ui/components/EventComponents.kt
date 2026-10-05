package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.ui.theme.*

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "technical" -> CatTechnicalColor
        "cultural" -> CatCulturalColor
        "sports" -> CatSportsColor
        "workshop" -> CatWorkshopColor
        "seminar" -> CatSeminarColor
        "hackathon" -> CatHackathonColor
        "symposium" -> CatSymposiumColor
        "club event", "club" -> CatClubColor
        "placement" -> CatPlacementColor
        "competition" -> CatCompetitionColor
        "fest" -> CatFestColor
        else -> CampusPrimary
    }
}

fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "technical" -> Icons.Default.Computer
        "cultural" -> Icons.Default.Celebration
        "sports" -> Icons.Default.SportsBasketball
        "workshop" -> Icons.Default.Build
        "seminar" -> Icons.Default.CastForEducation
        "hackathon" -> Icons.Default.Code
        "symposium" -> Icons.Default.Psychology
        "club event", "club" -> Icons.Default.GroupWork
        "placement" -> Icons.Default.Work
        "competition" -> Icons.Default.EmojiEvents
        "fest" -> Icons.Default.Festival
        else -> Icons.Default.Event
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val catColor = getCategoryColor(category)
    val containerColor = if (isSelected) catColor else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .testTag("cat_chip_$category"),
        shape = RoundedCornerShape(50),
        color = containerColor,
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getCategoryIcon(category),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun EventCard(
    event: EventEntity,
    isSaved: Boolean,
    isRegistered: Boolean,
    onClick: () -> Unit,
    onSaveToggle: () -> Unit,
    onRsvpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val catColor = getCategoryColor(event.category)
    val remainingSeats = (event.capacity - event.registrationCount).coerceAtLeast(0)
    val progress = (event.registrationCount.toFloat() / event.capacity.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Event Header Graphic with Gradient & Category Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                catColor.copy(alpha = 0.85f),
                                catColor.copy(alpha = 0.55f),
                                CampusPrimary.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .padding(14.dp)
            ) {
                // Background decorative pattern
                Row(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getCategoryIcon(event.category),
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        tint = Color.White.copy(alpha = 0.18f)
                    )
                }

                // Top row badges: Category + Save Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(event.category),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = event.category,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Save / Bookmark action
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                            .testTag("save_btn_${event.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isSaved) "Remove bookmark" else "Bookmark event",
                            tint = if (isSaved) CampusAccentGold else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Date Pill at bottom left
                Surface(
                    modifier = Modifier.align(Alignment.BottomStart),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = CampusPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${event.date} • ${event.startTime}",
                            color = CampusPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Card Body
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.venue,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "By ${event.organizer}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Seats progress bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (remainingSeats == 0) "Sold Out" else "$remainingSeats seats left",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (remainingSeats <= 5) CampusError else CampusSuccess
                        )
                        Text(
                            text = "${event.registrationCount}/${event.capacity} RSVPs",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (remainingSeats == 0) CampusError else CampusPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Button
                if (isRegistered) {
                    FilledTonalButton(
                        onClick = onRsvpClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("view_ticket_btn_${event.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = CampusSuccess.copy(alpha = 0.15f),
                            contentColor = CampusSuccess
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "View Digital Ticket",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    val isFull = remainingSeats == 0 || event.status == "REGISTRATION_FULL"
                    Button(
                        onClick = onRsvpClick,
                        enabled = !isFull,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("rsvp_btn_${event.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CampusPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = if (isFull) "Registration Full" else "RSVP Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
