package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AttendanceScanResult
import com.example.ui.theme.*
import com.example.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScannerScreen(
    viewModel: CampusViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scanResult by viewModel.scanResult.collectAsState()
    val allRsvps by viewModel.allRsvps.collectAsState()

    var manualCodeInput by remember { mutableStateOf("") }

    // Scanner Laser Animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    val totalRsvps = allRsvps.size
    val totalCheckedIn = allRsvps.count { it.attendanceStatus == "VERIFIED" }
    val attendancePct = if (totalRsvps > 0) (totalCheckedIn * 100) / totalRsvps else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Attendance QR Scanner", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .testTag("attendance_scanner_screen"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Stats Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "TOTAL PASSES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = totalRsvps.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "CHECKED IN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CampusSuccess)
                        Text(text = totalCheckedIn.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = CampusSuccess)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "ATTENDANCE RATE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CampusPrimary)
                        Text(text = "$attendancePct%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = CampusPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Scanner Viewfinder Overlay
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0F172A))
                    .border(2.dp, CampusPrimary.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Viewfinder Reticle Corners
                Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    val w = size.width
                    val h = size.height
                    val cornerLen = 24.dp.toPx()
                    val strokeW = 4.dp.toPx()

                    // Top Left
                    drawLine(CampusPrimary, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW)
                    drawLine(CampusPrimary, Offset(0f, 0f), Offset(0f, cornerLen), strokeW)

                    // Top Right
                    drawLine(CampusPrimary, Offset(w, 0f), Offset(w - cornerLen, 0f), strokeW)
                    drawLine(CampusPrimary, Offset(w, 0f), Offset(w, cornerLen), strokeW)

                    // Bottom Left
                    drawLine(CampusPrimary, Offset(0f, h), Offset(cornerLen, h), strokeW)
                    drawLine(CampusPrimary, Offset(0f, h), Offset(0f, h - cornerLen), strokeW)

                    // Bottom Right
                    drawLine(CampusPrimary, Offset(w, h), Offset(w - cornerLen, h), strokeW)
                    drawLine(CampusPrimary, Offset(w, h), Offset(w, h - cornerLen), strokeW)

                    // Laser Scanning Bar
                    val laserCurrentY = laserY * h
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = Offset(0f, laserCurrentY),
                        end = Offset(w, laserCurrentY),
                        strokeWidth = 3.dp.toPx()
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Point camera at digital ticket QR",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Scan Result Card / Feedback Alert
            if (scanResult != null) {
                when (val res = scanResult!!) {
                    is AttendanceScanResult.Success -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("scan_success_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CampusSuccess.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(shape = CircleShape, color = CampusSuccess, modifier = Modifier.size(44.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Attendance Verified ✓",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = CampusSuccess
                                    )
                                    Text(
                                        text = "${res.rsvp.studentName} (${res.rsvp.studentId})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${res.rsvp.department} • ${res.eventTitle}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Ticket: ${res.rsvp.rsvpId}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = CampusPrimary
                                    )
                                }
                                IconButton(onClick = { viewModel.clearScanResult() }) {
                                    Icon(Icons.Default.Close, contentDescription = "Dismiss")
                                }
                            }
                        }
                    }
                    is AttendanceScanResult.AlreadyVerified -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("scan_already_verified_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CampusAccentGold.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(shape = CircleShape, color = CampusAccentGold, modifier = Modifier.size(44.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Duplicate Check-in ⚠️",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = CampusAccentGold
                                    )
                                    Text(
                                        text = "${res.rsvp.studentName} has already checked in!",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = res.message,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { viewModel.clearScanResult() }) {
                                    Icon(Icons.Default.Close, contentDescription = "Dismiss")
                                }
                            }
                        }
                    }
                    is AttendanceScanResult.NotFound -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("scan_not_found_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CampusError.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(shape = CircleShape, color = CampusError, modifier = Modifier.size(44.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Invalid Ticket Pass ❌",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = CampusError
                                    )
                                    Text(
                                        text = res.error,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { viewModel.clearScanResult() }) {
                                    Icon(Icons.Default.Close, contentDescription = "Dismiss")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Simulate / Test Scan Buttons
            Text(
                text = "SIMULATE TICKET SCAN (TEST CODES)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Default Alex ticket code
                FilledTonalButton(
                    onClick = {
                        viewModel.scanAttendance("RSVP-2026-00125")
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("simulate_alex_ticket_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Scan: Alex Chen (RSVP-2026-00125)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                // If other RSVPs exist, show quick button for the first unverified one
                val unverified = allRsvps.find { it.attendanceStatus != "VERIFIED" && it.rsvpId != "RSVP-2026-00125" }
                if (unverified != null) {
                    FilledTonalButton(
                        onClick = {
                            viewModel.scanAttendance(unverified.rsvpId)
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Scan: ${unverified.studentName} (${unverified.rsvpId})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Manual Code Input
            Text(
                text = "OR ENTER RSVP ID MANUALLY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = manualCodeInput,
                    onValueChange = { manualCodeInput = it },
                    placeholder = { Text("e.g. RSVP-2026-00125") },
                    modifier = Modifier.weight(1f).testTag("manual_code_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (manualCodeInput.isNotBlank()) {
                            viewModel.scanAttendance(manualCodeInput.trim())
                            manualCodeInput = ""
                        }
                    },
                    modifier = Modifier.height(54.dp).testTag("verify_code_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CampusPrimary)
                ) {
                    Text("Verify")
                }
            }
        }
    }
}
