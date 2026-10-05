package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileScreen(
    viewModel: CampusViewModel,
    onSwitchToAdmin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userRsvps by viewModel.userRsvps.collectAsState()
    val savedIds by viewModel.savedEventIds.collectAsState()
    val attendedCount = userRsvps.count { it.attendanceStatus == "VERIFIED" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("student_profile_screen")
    ) {
        Text(
            text = "Student Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Student Digital Identity Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = CampusPrimary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = (currentUser?.name?.take(2) ?: "ST").uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = currentUser?.name ?: "Student",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = currentUser?.email ?: "",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = CampusPrimary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "VERIFIED STUDENT",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = CampusPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                ProfileField(label = "Student Register Number", value = currentUser?.studentId ?: "CS-2023-042", isCode = true)
                Spacer(modifier = Modifier.height(10.dp))
                ProfileField(label = "Academic Department", value = currentUser?.department ?: "Computer Science & Engineering")
                Spacer(modifier = Modifier.height(10.dp))
                ProfileField(label = "Graduation Year", value = currentUser?.year ?: "3rd Year (Class of 2027)")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Activity Stats Strip
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatColumn(count = userRsvps.size.toString(), label = "RSVPs")
                VerticalDivider(modifier = Modifier.height(36.dp))
                StatColumn(count = attendedCount.toString(), label = "Attended")
                VerticalDivider(modifier = Modifier.height(36.dp))
                StatColumn(count = savedIds.size.toString(), label = "Saved")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Role Switch & Admin Actions
        Text(
            text = "PLATFORM CONTROLS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                ListItem(
                    headlineContent = { Text("Switch to Admin / Organizer View", fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text("Create events, view analytics, and scan ticket QR codes") },
                    leadingContent = {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CampusSecondary)
                    },
                    trailingContent = {
                        Button(
                            onClick = {
                                viewModel.switchRole("ADMIN")
                                onSwitchToAdmin()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CampusSecondary)
                        ) {
                            Text("Switch")
                        }
                    }
                )

                HorizontalDivider()

                ListItem(
                    headlineContent = { Text("Sign Out", fontWeight = FontWeight.SemiBold, color = CampusError) },
                    leadingContent = {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = CampusError)
                    },
                    modifier = Modifier.testTag("logout_btn"),
                    trailingContent = {
                        TextButton(onClick = onLogout) {
                            Text("Log Out", color = CampusError, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String, isCode: Boolean = false) {
    Column {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = if (isCode) FontFamily.Monospace else FontFamily.Default,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StatColumn(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = CampusPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
