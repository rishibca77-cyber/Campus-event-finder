package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.ui.components.CategoryChip
import com.example.ui.components.EventCard
import com.example.ui.theme.*
import com.example.viewmodel.CampusViewModel
import com.example.viewmodel.EventSortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFinderScreen(
    viewModel: CampusViewModel,
    onEventClick: (EventEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val events by viewModel.allEvents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedDept by viewModel.selectedDepartment.collectAsState()
    val selectedSort by viewModel.selectedSort.collectAsState()

    val savedIds by viewModel.savedEventIds.collectAsState()
    val userRsvps by viewModel.userRsvps.collectAsState()
    val registeredEventIds = remember(userRsvps) { userRsvps.map { it.eventId }.toSet() }

    val categories = listOf(
        "All", "Technical", "Cultural", "Sports", "Workshop", "Seminar",
        "Hackathon", "Symposium", "Club Event", "Placement", "Competition", "Fest"
    )

    val departments = listOf(
        "All", "Computer Science & Engineering", "Information Technology",
        "Mechanical & Mechatronics", "Design & Creative Arts", "Physical Education",
        "All Departments"
    )

    // Dynamic Filter & Search Computation
    val filteredEvents = remember(events, searchQuery, selectedCategory, selectedDept, selectedSort) {
        events.filter { event ->
            val matchesQuery = searchQuery.isBlank() ||
                    event.title.contains(searchQuery, ignoreCase = true) ||
                    event.description.contains(searchQuery, ignoreCase = true) ||
                    event.venue.contains(searchQuery, ignoreCase = true) ||
                    event.organizer.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategory == "All" ||
                    event.category.equals(selectedCategory, ignoreCase = true)

            val matchesDept = selectedDept == "All" ||
                    event.department.contains(selectedDept, ignoreCase = true) ||
                    event.department.equals("All Departments", ignoreCase = true)

            matchesQuery && matchesCategory && matchesDept
        }.let { list ->
            when (selectedSort) {
                EventSortOption.UPCOMING -> list.sortedBy { it.date }
                EventSortOption.POPULAR -> list.sortedByDescending { it.registrationCount }
                EventSortOption.NEWEST -> list.sortedByDescending { it.createdAt }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("event_finder_screen")
    ) {
        // Top Search Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Text(
                text = "Event Discovery",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search input field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Search by name, AI, hackathon, speaker...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("finder_search_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sort Selector Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SORT BY:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    EventSortOption.values().forEach { sort ->
                        FilterChip(
                            selected = selectedSort == sort,
                            onClick = { viewModel.onSortSelect(sort) },
                            label = {
                                Text(
                                    text = sort.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedSort == sort) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CampusPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Horizontal Category Filter Strip
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = { viewModel.onCategorySelect(category) }
                )
            }
        }

        // Results Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredEvents.size} events found",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (selectedCategory != "All" || searchQuery.isNotEmpty()) {
                TextButton(
                    onClick = {
                        viewModel.onSearchQueryChange("")
                        viewModel.onCategorySelect("All")
                    }
                ) {
                    Text(text = "Reset Filters", color = CampusPrimary, fontSize = 12.sp)
                }
            }
        }

        // Event List or Empty State
        if (filteredEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No events found",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try adjusting your search terms or selecting another category filter.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredEvents) { event ->
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
}
