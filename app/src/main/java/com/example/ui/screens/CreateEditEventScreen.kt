package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventEntity
import com.example.ui.theme.*
import com.example.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditEventScreen(
    viewModel: CampusViewModel,
    existingEvent: EventEntity? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(existingEvent?.title ?: "") }
    var description by remember { mutableStateOf(existingEvent?.description ?: "") }
    var category by remember { mutableStateOf(existingEvent?.category ?: "Technical") }
    var date by remember { mutableStateOf(existingEvent?.date ?: "2026-10-25") }
    var startTime by remember { mutableStateOf(existingEvent?.startTime ?: "10:00 AM") }
    var endTime by remember { mutableStateOf(existingEvent?.endTime ?: "03:00 PM") }
    var venue by remember { mutableStateOf(existingEvent?.venue ?: "Turing Auditorium, Block B") }
    var organizer by remember { mutableStateOf(existingEvent?.organizer ?: "Campus Tech Council") }
    var organizerEmail by remember { mutableStateOf(existingEvent?.organizerEmail ?: "organizer@campus.edu") }
    var contact by remember { mutableStateOf(existingEvent?.contactNumber ?: "+1 (555) 234-5678") }
    var capacityStr by remember { mutableStateOf(existingEvent?.capacity?.toString() ?: "100") }
    var eligibility by remember { mutableStateOf(existingEvent?.eligibility ?: "Open to all students") }
    var rules by remember { mutableStateOf(existingEvent?.rules ?: "Standard college code of conduct applies") }
    var requirements by remember { mutableStateOf(existingEvent?.requirements ?: "College ID and registration QR pass") }
    var speakers by remember { mutableStateOf(existingEvent?.speakers ?: "Industry Guest Speaker") }
    var schedule by remember { mutableStateOf(existingEvent?.schedule ?: "10:00 AM - Keynote\n12:00 PM - Workshop\n02:30 PM - Q&A & Wrap up") }

    val categories = listOf(
        "Technical", "Cultural", "Sports", "Workshop", "Seminar",
        "Hackathon", "Symposium", "Club Event", "Placement", "Competition", "Fest"
    )

    var categoryExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingEvent == null) "Create New Event" else "Edit Event",
                        fontWeight = FontWeight.Bold
                    )
                },
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
                .testTag("create_event_form")
        ) {
            Text(
                text = "Event Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Event Name *") },
                modifier = Modifier.fillMaxWidth().testTag("event_title_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Selector Exposed Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth().testTag("event_category_input")
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Event Description *") },
                modifier = Modifier.fillMaxWidth().testTag("event_desc_input"),
                minLines = 3,
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Date, Time & Venue",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (YYYY-MM-DD) *") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("event_date_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start Time *") },
                    modifier = Modifier.weight(1f).testTag("event_start_time_input"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End Time *") },
                    modifier = Modifier.weight(1f).testTag("event_end_time_input"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = venue,
                onValueChange = { venue = it },
                label = { Text("Campus Venue *") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("event_venue_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = capacityStr,
                onValueChange = { capacityStr = it },
                label = { Text("Maximum Capacity *") },
                leadingIcon = { Icon(Icons.Default.EventSeat, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().testTag("event_capacity_input"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Organizer & Contact",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = organizer,
                onValueChange = { organizer = it },
                label = { Text("Organizer Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = organizerEmail,
                    onValueChange = { organizerEmail = it },
                    label = { Text("Email") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Phone") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Eligibility, Rules & Schedule",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = eligibility,
                onValueChange = { eligibility = it },
                label = { Text("Eligibility") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = rules,
                onValueChange = { rules = it },
                label = { Text("Rules & Guidelines") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = requirements,
                onValueChange = { requirements = it },
                label = { Text("Requirements (e.g. Laptop, ID Card)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = speakers,
                onValueChange = { speakers = it },
                label = { Text("Featured Speakers") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = schedule,
                onValueChange = { schedule = it },
                label = { Text("Event Schedule Timeline") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons: Publish Event & Save Draft
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val cap = capacityStr.toIntOrNull() ?: 100
                        viewModel.saveEvent(
                            title = title.ifBlank { "Untitled Event" },
                            description = description,
                            category = category,
                            date = date,
                            startTime = startTime,
                            endTime = endTime,
                            venue = venue,
                            organizer = organizer,
                            organizerEmail = organizerEmail,
                            contact = contact,
                            capacity = cap,
                            eligibility = eligibility,
                            rules = rules,
                            requirements = requirements,
                            speakers = speakers,
                            schedule = schedule,
                            existingId = existingEvent?.id
                        )
                        onSaved()
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Draft", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        val cap = capacityStr.toIntOrNull() ?: 100
                        viewModel.saveEvent(
                            title = title.ifBlank { "Campus Event" },
                            description = description,
                            category = category,
                            date = date,
                            startTime = startTime,
                            endTime = endTime,
                            venue = venue,
                            organizer = organizer,
                            organizerEmail = organizerEmail,
                            contact = contact,
                            capacity = cap,
                            eligibility = eligibility,
                            rules = rules,
                            requirements = requirements,
                            speakers = speakers,
                            schedule = schedule,
                            existingId = existingEvent?.id
                        )
                        onSaved()
                    },
                    enabled = title.isNotBlank() && venue.isNotBlank(),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("publish_event_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CampusPrimary)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (existingEvent == null) "Publish Event" else "Save Changes",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
