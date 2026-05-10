package com.eduplay.moblie

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.eduplay.moblie.models.EventTag
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.ui.screens.DownloadedEventsUpdateScreen
import com.eduplay.moblie.useCases.managers.AppSettingsManager
import com.eduplay.moblie.useCases.managers.OfflineModeManager
import com.eduplay.moblie.useCases.managers.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@UninstallModules(ManagersProvider::class)
@HiltAndroidTest
class DownloadEventsUpdatesScreenUiTest {

    @Module
    @InstallIn(SingletonComponent::class)
    class FakeProvider {
        @Provides
        fun provideFakeModeManager(): OfflineModeManager {
            return object : OfflineModeManager {
                override fun getAppMode(): Flow<OfflineModeManager.AppModes> {
                    return flowOf(OfflineModeManager.AppModes.ONLINE)
                }

                override suspend fun saveAppMode(mode: OfflineModeManager.AppModes) {}

                override fun getCurrentUserId(): Flow<String> {
                    return flowOf("user1")
                }

                override suspend fun removeCurrentUserId() {}

                override suspend fun saveCurrentUserId(id: String) {}

            }
        }

        @Provides
        @Singleton
        fun provideFakeTokenManager(): TokenManager {
            return object : TokenManager {
                override fun getAccessToken(): Flow<String> {
                    return flowOf("access")
                }

                override suspend fun saveAccessToken(token: String) {}

                override fun getRefreshToken(): Flow<String> {
                    return flowOf("refresh")
                }

                override suspend fun saveRefreshToken(token: String) {}

            }
        }

        @Provides
        @Singleton
        fun provideFakeAppSettingsManager(): AppSettingsManager {
            return object : AppSettingsManager {
                override fun getTheme(): Flow<AppSettingsManager.Themes> {
                    return flowOf(AppSettingsManager.Themes.LIGHT)
                }

                override suspend fun saveTheme(mode: AppSettingsManager.Themes) {}

            }
        }
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltComponentActivity>()

    private class UpdateData {
        val innerPaddingValues = PaddingValues()
        val onGoBack = {}
        val events: SnapshotStateList<QuestShortInfo> = mutableStateListOf()
        val networkError: MutableState<Boolean> = mutableStateOf(false)
        val onProceed: () -> Unit = {}
        val onChooseEvent: (String) -> Unit = { _ -> }
        val onDeleteEvent: (String) -> Unit = { _ -> }
        val onNavigateToEvent: (String) -> Unit = { _ -> }
    }

    @SpyK
    private lateinit var updateData: UpdateData

    @Before
    fun setUp() {
        updateData = UpdateData()
        MockKAnnotations.init(this)
    }


    @Composable
    fun FillScreen() {
        DownloadedEventsUpdateScreen(
            updateData.innerPaddingValues,
            updateData.onGoBack,
            updateData.events,
            updateData.networkError,
            updateData.onProceed,
            updateData.onChooseEvent,
            updateData.onDeleteEvent,
            updateData.onNavigateToEvent
        )
    }

    @Test
    fun when_network_error_is_true_only_error_text_is_displayed() {
        composeTestRule.apply {
            every { updateData.networkError } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }

            onNodeWithTag("network_error_text", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("need_update_text", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("update_list", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun when_network_error_is_false_error_text_is_not_displayed_update_section_is_displayed() {
        composeTestRule.apply {
            every { updateData.networkError } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("network_error_text", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("need_update_text", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun check_update_button_updates_its_event() {
        composeTestRule.apply {
            var eventId = ""
            val info = QuestShortInfo(
                id = "id1",
                name = "",
                description = "",
                imageUrl = "",
                rate = 0.0,
                isFavourite = false,
                tags = listOf<EventTag>(),
                isDownloaded = true
            )
            every { updateData.networkError } returns mutableStateOf(false)
            every { updateData.onChooseEvent } returns { eventId = it }
            every { updateData.events } returns mutableStateListOf(info)
            setContent {
                FillScreen()
            }
            onNodeWithTag("download_btn_${info.id}", useUnmergedTree = true).performClick()

            assertEquals(info.id, eventId)
        }
    }

    @Test
    fun check_delete_button_delete_its_event() {
        composeTestRule.apply {
            var eventId = ""
            val info = QuestShortInfo(
                id = "id1",
                name = "",
                description = "",
                imageUrl = "",
                rate = 0.0,
                isFavourite = false,
                tags = listOf<EventTag>(),
                isDownloaded = true
            )
            every { updateData.networkError } returns mutableStateOf(false)
            every { updateData.onDeleteEvent } returns { eventId = it }
            every { updateData.events } returns mutableStateListOf(info)
            setContent {
                FillScreen()
            }
            onNodeWithTag("delete_btn_${info.id}", useUnmergedTree = true).performClick()

            assertEquals(info.id, eventId)
        }
    }

    @Test
    fun check_click_on_event_navigates_to_event_page() {
        composeTestRule.apply {
            var eventId = ""
            val info = QuestShortInfo(
                id = "id1",
                name = "",
                description = "",
                imageUrl = "",
                rate = 0.0,
                isFavourite = false,
                tags = listOf<EventTag>(),
                isDownloaded = true
            )
            every { updateData.networkError } returns mutableStateOf(false)
            every { updateData.onNavigateToEvent } returns { eventId = it }
            every { updateData.events } returns mutableStateListOf(info)
            setContent {
                FillScreen()
            }
            onNodeWithTag("element_${info.id}", useUnmergedTree = true).performClick()

            assertEquals(info.id, eventId)
        }
    }
}