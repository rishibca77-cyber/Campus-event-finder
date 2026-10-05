package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.data.model.RsvpEntity
import com.example.ui.theme.*
import com.example.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventRsvpsScreen(
    event: EventEntity,
    viewModel: CampusViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allRsvps by viewModel.allRsvps.collectAsState()
    val eventRsvps = remember(allRsvps, event) {
        allRsvps.filter { it.eventId == event.id }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedDept by remember { mutableStateOf("All") }

    val departments = remember(eventRsvps) {
        listOf("All") + eventRsvps.map { it.department }.distinct()
    }

    val filteredRsvps = remember(eventRsvps, searchQuery, selectedDept) {
        eventRsvps.filter { rsvp ->
            val matchesQuery = searchQuery.isBlank() ||
                    rsvp.studentName.contains(searchQuery, ignoreCase = true) ||
                    rsvp.studentId.contains(searchQuery, ignoreCase = true) ||
                    rsvp.collegeEmail.contains(searchQuery, ignoreCase = true)

            val matchesDept = selectedDept == "All" || rsvp.department == selectedDept
            matchesQuery && matchesDept
        }
    }

    val checkedInCount = eventRsvps.count { it.attendanceStatus == "VERIFIED" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Attendee Roster", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = event.title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Export CSV Action
                    IconButton(
                        onClick = {
                            val csvContent = buildString {
                                appendLine("Student Name,Register No,Department,Year,Email,Phone,RSVP ID,Attendance Status")
                                eventRsvps.forEach { r ->
                                    appendLine("\"${r.studentName}\",\"${r.studentId}\",\"${r.department}\",\"${r.year}\",\"${r.collegeEmail}\",\"${r.phone}\",\"${r.rsvpId}\",\"${r.attendanceStatus}\"")
                                }
                            }
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, csvContent)
                                putExtra(Intent.EXTRA_SUBJECT, "Attendees_${event.title.take(15)}.csv")
                                type = "text/csv"
                            }
                            context.startActivity(Intent.createChooser(intent, "Export Attendees List"))
                        },
                        modifier = Modifier.testTag("export_csv_btn")
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export CSV", tint = CampusPrimary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("event_rsvps_screen")
        ) {
            // Stats Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${eventRsvps.size} Total Registrations",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$checkedInCount checked in (${if (eventRsvps.isNotEmpty()) (checkedInCount * 100 / eventRsvps.size) else 0}%)",
                            fontSize = 12.sp,
                            color = CampusSuccess,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            val csvContent = buildString {
                                appendLine("Student Name,Register No,Department,Year,Email,Phone,RSVP ID,Attendance Status")
                                eventRsvps.forEach { r ->
                                    appendLine("\"${r.studentName}\",\"${r.studentId}\",\"${r.department}\",\"${r.year}\",\"${r.collegeEmail}\",\"${r.phone}\",\"${r.rsvpId}\",\"${r.attendanceStatus}\"")
                                }
                            }
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, csvContent)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Roster CSV"))
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CampusPrimary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Export CSV", fontSize = 12.sp)
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by student name or register no...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Roster List
            if (filteredRsvps.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No registrations match your search",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredRsvps) { rsvp ->
                        RsvpAttendeeCard(
                            rsvp = rsvp,
                            onToggleVerify = {
                                viewModel.scanAttendance(rsvp.rsvpId)
                            },
                            onCancel = {
                                viewModel.cancelRsvp(rsvp.id, rsvp.eventId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RsvpAttendeeCard(
    rsvp: RsvpEntity,
    onToggleVerify: () -> Unit,
    onCancel: () -> Unit
) {
    val isVerified = rsvp.attendanceStatus == "VERIFIED"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = rsvp.studentName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (isVerified) CampusSuccess.copy(alpha = 0.15f) else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isVerified) "CHECKED IN" else "REGISTERED",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = if (isVerified) CampusSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${rsvp.studentId} • ${rsvp.department} (${rsvp.year})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "ID: ${rsvp.rsvpId} • ${rsvp.collegeEmail}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CampusPrimary
                )
            }

            // Quick check-in action
            IconButton(onClick = onToggleVerify) {
                Icon(
                    imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Verify attendance",
                    tint = if (isVerified) CampusSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
