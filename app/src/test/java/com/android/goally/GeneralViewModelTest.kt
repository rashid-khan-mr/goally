package com.android.goally
import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.InvalidationTracker
import com.android.goally.app.GoallyApp
import com.android.goally.data.model.api.ErrorResponse
import com.android.goally.data.model.api.response.copilet.Activities
import com.android.goally.data.model.api.response.copilet.CopilotResponse
import com.android.goally.data.model.api.response.copilet.Routines
import com.android.goally.data.model.api.response.copilet.ScheduleV2
import com.android.goally.data.network.rest.api.GeneralApi
import com.android.goally.data.repo.GeneralRepo
import com.android.goally.ui.viewmodels.GeneralViewModel
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.io.IOException
import java.util.concurrent.CountDownLatch

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class GeneralViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule() // For LiveData

    @Mock
    private lateinit var generalRepo: GeneralRepo

    private lateinit var viewModel: GeneralViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = GeneralViewModel(generalRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun `test getCopiletFromDb returns copilets successfully`() = runTest {
        val mockCopilets = listOf(
            Routines(
                _id = "1",
                name = "Routine 1",
                type = "Daily",
                imgURL = "url",
                scheduleV2 = null,
                activities = emptyList(),
                folder = "Folder1"
            )
        )
        `when`(generalRepo.getAllCopilets()).thenReturn(mockCopilets)

        // Act
        val result = viewModel.getCopiletFromDb()

        // Assert
        assertEquals(mockCopilets, result)
        verify(generalRepo).getAllCopilets() // Verify the repository call was made
    }

    @Test
    fun `test getCopiletDetails calls success callback with correct data`() = runTest {
        // Arrange
        val copiletId = "1"
        val mockCopilet = Routines(
            _id = copiletId,
            name = "Routine 1",
            type = "Daily",
            imgURL = "url",
            scheduleV2 = null,
            activities = emptyList(),
            folder = "Folder1"
        )
        `when`(generalRepo.getCopiletById(copiletId)).thenReturn(mockCopilet)

        var successResult: Routines? = null

        // Act
        viewModel.getCopiletDetails(copiletId) { result ->
            successResult = result
        }

        // Assert
        assertEquals(mockCopilet, successResult)
        verify(generalRepo).getCopiletById(copiletId) 
    }
}
