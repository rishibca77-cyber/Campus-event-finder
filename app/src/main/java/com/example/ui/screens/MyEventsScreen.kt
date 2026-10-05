package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.data.model.RsvpEntity
import com.example.ui.components.EventCard
import com.example.ui.components.getCategoryColor
import com.example.ui.theme.*
import com.example.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    viewModel: CampusViewModel,
    onViewTicket: (RsvpEntity, EventEntity) -> Unit,
    onEventClick: (EventEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Upcoming, 1: Completed, 2: Saved
    val tabs = listOf("Upcoming", "Completed", "Saved")

    val allEvents by viewModel.allEvents.collectAsState()
    val userRsvps by viewModel.userRsvps.collectAsState()
    val savedIds by viewModel.savedEventIds.collectAsState()

    var rsvpToCancel by remember { mutableStateOf<RsvpEntity?>(null) }

    val upcomingRsvps = remember(userRsvps) {
        userRsvps.filter { it.attendanceStatus != "VERIFIED" }
    }

    val completedRsvps = remember(userRsvps) {
        userRsvps.filter { it.attendanceStatus == "VERIFIED" }
    }

    val savedEvents = remember(allEvents, savedIds) {
        allEvents.filter { savedIds.contains(it.id) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("my_events_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "My Campus Events",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(14.dp))

            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = CampusPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            val count = when (index) {
                                0 -> upcomingRsvps.size
                                1 -> completedRsvps.size
                                else -> savedEvents.size
                            }
                            Text(
                                text = "$title ($count)",
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Upcoming RSVPs
                if (upcomingRsvps.isEmpty()) {
                    EmptySection(
                        title = "No Upcoming RSVPs",
                        desc = "You haven't registered for any upcoming events yet. Discover events in the Finder!"
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(upcomingRsvps) { rsvp ->
                            val event = allEvents.find { it.id == rsvp.eventId }
                            if (event != null) {
                                MyRsvpCard(
                                    rsvp = rsvp,
                                    event = event,
                                    onViewTicket = { onViewTicket(rsvp, event) },
                                    onCancelRsvp = { rsvpToCancel = rsvp },
                                    onCardClick = { onEventClick(event) }
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                // Completed RSVPs
                if (completedRsvps.isEmpty()) {
                    EmptySection(
                        title = "No Completed Events",
                        desc = "Events you have verified entrance for will appear here after attendance check-in."
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(completedRsvps) { rsvp ->
                            val event = allEvents.find { it.id == rsvp.eventId }
                            if (event != null) {
                                MyRsvpCard(
                                    rsvp = rsvp,
                                    event = event,
                                    onViewTicket = { onViewTicket(rsvp, event) },
                                    onCancelRsvp = null,
                                    onCardClick = { onEventClick(event) }
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // Saved / Bookmarked Events
                if (savedEvents.isEmpty()) {
                    EmptySection(
                        title = "No Saved Events",
                        desc = "Tap the bookmark icon on any event card to save it for later review."
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(savedEvents) { event ->
                            val isRegistered = userRsvps.any { it.eventId == event.id }
                            EventCard(
                                event = event,
                                isSaved = true,
                                isRegistered = isRegistered,
                                onClick = { onEventClick(event) },
                                onSaveToggle = { viewModel.toggleSave(event.id) },
                                onRsvpClick = { onEventClick(event) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Cancel RSVP Confirmation Dialog
    if (rsvpToCancel != null) {
        val rsvp = rsvpToCancel!!
        val event = allEvents.find { it.id == rsvp.eventId }
        AlertDialog(
            onDismissRequest = { rsvpToCancel = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = CampusError) },
            title = { Text(text = "Cancel Event RSVP?") },
            text = {
                Text(
                    text = "Are you sure you want to cancel your reservation for '${event?.title ?: "this event"}'? Your ticket and seat will be released to other students."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelRsvp(rsvp.id, rsvp.eventId)
                        rsvpToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CampusError)
                ) {
                    Text("Yes, Cancel RSVP")
                }
            },
            dismissButton = {
                TextButton(onClick = { rsvpToCancel = null }) {
                    Text("Keep RSVP")
                }
            }
        )
    }
}

@Composable
private fun MyRsvpCard(
    rsvp: RsvpEntity,
    event: EventEntity,
    onViewTicket: () -> Unit,
    onCancelRsvp: (() -> Unit)?,
    onCardClick: () -> Unit
) {
    val isVerified = rsvp.attendanceStatus == "VERIFIED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = getCategoryColor(event.category).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = event.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color = getCategoryColor(event.category),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    color = if (isVerified) CampusSuccess.copy(alpha = 0.15f) else CampusPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = if (isVerified) CampusSuccess else CampusPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isVerified) "ATTENDED" else "CONFIRMED",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isVerified) CampusSuccess else CampusPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = event.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${event.date} • ${event.startTime}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = event.venue,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onViewTicket,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CampusPrimary)
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "View Ticket", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                if (onCancelRsvp != null && !isVerified) {
                    OutlinedButton(
                        onClick = onCancelRsvp,
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CampusError)
                    ) {
                        Text(text = "Cancel", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySection(title: String, desc: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.EventBusy,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = desc,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
