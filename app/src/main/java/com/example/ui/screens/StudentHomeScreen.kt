package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.EventCard
import com.example.ui.theme.*
import com.example.viewmodel.CampusViewModel

@Composable
fun StudentHomeScreen(
    viewModel: CampusViewModel,
    onNavigateToFinder: (String?) -> Unit,
    onEventClick: (EventEntity) -> Unit,
    onNavigateToMyEvents: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val events by viewModel.allEvents.collectAsState()
    val savedIds by viewModel.savedEventIds.collectAsState()
    val userRsvps by viewModel.userRsvps.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifs = notifications.count { !it.read }

    val registeredEventIds = remember(userRsvps) { userRsvps.map { it.eventId }.toSet() }

    val categories = listOf(
        "All", "Technical", "Cultural", "Sports", "Workshop", "Seminar",
        "Hackathon", "Symposium", "Club Event", "Placement", "Competition", "Fest"
    )

    // Events happening today (e.g. 2026-10-06 or today)
    val todayEvents = remember(events) {
        events.filter { it.date == "2026-10-06" || it.date == "2026-10-05" }
    }

    // Recommendation logic: If student is CS/IT, recommend Hackathons, Workshops, Technical competitions
    val recommendedEvents = remember(events, currentUser) {
        val userDept = currentUser?.department?.lowercase() ?: ""
        events.filter { event ->
            if (userDept.contains("computer") || userDept.contains("tech")) {
                event.category in listOf("Hackathon", "Workshop", "Technical", "Competition")
            } else {
                event.category in listOf("Cultural", "Fest", "Sports", "Club Event")
            }
        }.take(4)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("student_home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Top Bar: Greeting + Student Profile Avatar + Notification Bell
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(CampusPrimary.copy(alpha = 0.14f), MaterialTheme.colorScheme.background)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onNavigateToProfile)
                    ) {
                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = CircleShape,
                            color = CampusPrimary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (currentUser?.name?.take(2) ?: "AC").uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Hi, ${currentUser?.name?.split(" ")?.firstOrNull() ?: "Student"}! 👋",
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${currentUser?.department ?: "Computer Science"} • ${currentUser?.year ?: "3rd Year"}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Notification Icon with Badge
                    IconButton(
                        onClick = onNavigateToNotifications,
                        modifier = Modifier.testTag("notifications_icon_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifs > 0) {
                                    Badge(containerColor = CampusError) {
                                        Text(text = unreadNotifs.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar Trigger
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigateToFinder(null) }
                        .testTag("home_search_bar"),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Search events, hackathons, workshops...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = CampusPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Active RSVPs Banner (if any)
        if (userRsvps.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clickable(onClick = onNavigateToMyEvents)
                        .testTag("active_rsvps_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CampusSecondary.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CampusSecondary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "You have ${userRsvps.size} Active RSVPs",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tap to view boarding passes & digital tickets",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = CampusSecondary
                        )
                    }
                }
            }
        }

        // Horizontal Category Chips
        item {
            Column(modifier = Modifier.padding(vertical = 10.dp)) {
                Text(
                    text = "Browse Categories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = false,
                            onClick = {
                                viewModel.onCategorySelect(category)
                                onNavigateToFinder(category)
                            }
                        )
                    }
                }
            }
        }

        // Events Happening Today (if any)
        if (todayEvents.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = CampusError.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "HAPPENING TODAY",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = CampusError,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(todayEvents) { event ->
                            EventCard(
                                event = event,
                                isSaved = savedIds.contains(event.id),
                                isRegistered = registeredEventIds.contains(event.id),
                                onClick = { onEventClick(event) },
                                onSaveToggle = { viewModel.toggleSave(event.id) },
                                onRsvpClick = { onEventClick(event) },
                                modifier = Modifier.width(300.dp)
                            )
                        }
                    }
                }
            }
        }

        // Recommended For You
        item {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = CampusAccentGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recommended For You",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Based on your major in ${currentUser?.department ?: "Tech"}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(onClick = { onNavigateToFinder(null) }) {
                        Text(text = "See All", color = CampusPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(recommendedEvents) { event ->
                        EventCard(
                            event = event,
                            isSaved = savedIds.contains(event.id),
                            isRegistered = registeredEventIds.contains(event.id),
                            onClick = { onEventClick(event) },
                            onSaveToggle = { viewModel.toggleSave(event.id) },
                            onRsvpClick = { onEventClick(event) },
                            modifier = Modifier.width(300.dp)
                        )
                    }
                }
            }
        }

        // All Upcoming Events
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Explore All Events",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${events.size} exciting happenings on campus",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(events) { event ->
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                EventCard(
                    event = event,
                    isSaved = savedIds.contains(event.id),
                    isRegistered = registeredEventIds.contains(event.id),
                    onClick = { onEventClick(event) },
                    onSaveToggle = { viewModel.toggleSave(event.id) },
                    onRsvpClick = { onEventClick(event) }
                )
            }
        }
    }
}
