package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.ui.components.AttendanceGaugeCard
import com.example.ui.components.CategoryDistributionCard
import com.example.ui.components.RsvpsByEventBarChart
import com.example.ui.theme.*
import com.example.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: CampusViewModel,
    onCreateEvent: () -> Unit,
    onManageEvents: () -> Unit,
    onOpenScanner: () -> Unit,
    onSwitchToStudent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val events by viewModel.allEvents.collectAsState()
    val allRsvps by viewModel.allRsvps.collectAsState()

    val totalEvents = events.size
    val totalRsvps = allRsvps.size
    val totalAttended = allRsvps.count { it.attendanceStatus == "VERIFIED" }
    val totalCapacity = events.sumOf { it.capacity }
    val totalAvailableSeats = (totalCapacity - totalRsvps).coerceAtLeast(0)
    val mostPopularEvent = events.maxByOrNull { it.registrationCount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Surface(
                            color = CampusSecondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "ADMIN & ORGANIZER CONSOLE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = CampusSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Campus Analytics",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Switch back to Student View
                    FilledTonalButton(
                        onClick = {
                            viewModel.switchRole("STUDENT")
                            onSwitchToStudent()
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = CampusPrimary.copy(alpha = 0.15f))
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp), tint = CampusPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Student Mode", color = CampusPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Action Hub Buttons
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text(
                    text = "Organizer Quick Actions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionCard(
                        title = "Create Event",
                        subtitle = "Draft & publish",
                        icon = Icons.Default.AddCircle,
                        color = CampusPrimary,
                        onClick = onCreateEvent,
                        modifier = Modifier.weight(1f).testTag("admin_create_event_btn")
                    )
                    ActionCard(
                        title = "QR Scanner",
                        subtitle = "Scan tickets",
                        icon = Icons.Default.QrCodeScanner,
                        color = CampusSuccess,
                        onClick = onOpenScanner,
                        modifier = Modifier.weight(1f).testTag("admin_scanner_btn")
                    )
                    ActionCard(
                        title = "Manage",
                        subtitle = "Rosters & edits",
                        icon = Icons.Default.ManageAccounts,
                        color = CampusSecondary,
                        onClick = onManageEvents,
                        modifier = Modifier.weight(1f).testTag("admin_manage_events_btn")
                    )
                }
            }
        }

        // Key Analytics Metric Cards Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Text(
                    text = "Key Campus Metrics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Total Events",
                        value = totalEvents.toString(),
                        subtitle = "${events.count { it.status == "REGISTRATION_OPEN" }} Open",
                        icon = Icons.Default.Event,
                        color = CampusPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total RSVPs",
                        value = totalRsvps.toString(),
                        subtitle = "+14 today",
                        icon = Icons.Default.ConfirmationNumber,
                        color = CampusSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Attendees Verified",
                        value = totalAttended.toString(),
                        subtitle = "Entrance check-in",
                        icon = Icons.Default.Verified,
                        color = CampusSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Available Seats",
                        value = totalAvailableSeats.toString(),
                        subtitle = "Out of $totalCapacity total",
                        icon = Icons.Default.EventSeat,
                        color = CampusAccentGold,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (mostPopularEvent != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CampusAccentGold.copy(alpha = 0.15f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = CampusAccentGold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "MOST POPULAR CAMPUS EVENT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CampusAccentGold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = mostPopularEvent.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${mostPopularEvent.registrationCount} registered • ${mostPopularEvent.category}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Charts Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AttendanceGaugeCard(
                    totalRsvps = totalRsvps,
                    attendedCount = totalAttended
                )

                RsvpsByEventBarChart(events = events)

                CategoryDistributionCard(events = events)
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
