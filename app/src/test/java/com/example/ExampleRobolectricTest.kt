package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.CampusDatabase
import com.example.data.repository.CampusRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CampusConnect", appName)
    }

    @Test
    fun `seed database and retrieve user`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = CampusDatabase.getInstance(context)
        val repository = CampusRepository(db)

        repository.initSeedDataIfEmpty()
        val user = repository.getUserByEmail("alex.chen@campus.edu")

        assertNotNull(user)
        assertEquals("Alex Chen", user?.name)
        assertEquals("CS-2023-042", user?.studentId)
    }
}
