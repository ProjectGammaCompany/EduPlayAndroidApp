package com.eduplay.moblie

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.eduplay.moblie.repository.responseTypes.PlayerStats.StatGroup
import com.eduplay.moblie.repository.responseTypes.PlayerStats.StatUser
import com.eduplay.moblie.ui.screens.EventResultScreen
import com.eduplay.moblie.useCases.AppSettingsManager
import com.eduplay.moblie.useCases.OfflineModeManager
import com.eduplay.moblie.useCases.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

@UninstallModules(ManagersProvider::class)
@HiltAndroidTest
class EventResultScreenUiTest {

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

    val innerPaddingValues: PaddingValues = PaddingValues()
    val onExitScreen: () -> Boolean = { true }

    @Test
    fun users_stats_displays_users_and_points() {
        composeTestRule.apply {
            val statUsers = mutableListOf<StatUser>()
            for (i in 0..5) {
                statUsers.add(StatUser(
                    id = "user_${i}",
                    username = "user ${i}",
                    avatar = "",
                    points = i
                ))
            }
            val users = mutableStateListOf<StatUser>()
            users.addAll(statUsers)
            val groups = mutableStateListOf<StatGroup>()

            setContent {
                EventResultScreen(
                    innerPaddingValues,
                    onExitScreen,
                    users,
                    groups
                )
            }

            for (i in statUsers.indices) {
                onNodeWithTag("user_${statUsers[i].id}", useUnmergedTree = true).assertIsDisplayed()
                onNodeWithTag(
                    "user_${statUsers[i].id}",
                    useUnmergedTree = true
                ).assertTextContains(statUsers[i].username + ":")

                //onNodeWithTag("points_${statUsers[i].id}", useUnmergedTree = true).assertIsDisplayed()
                onNodeWithTag(
                    "points_${statUsers[i].id}",
                    useUnmergedTree = true
                ).assertTextContains(" ${statUsers[i].points} ")
            }
        }
    }

    @Test
    fun group_stats_displays_users_and_points() {
        composeTestRule.apply {
            val statGroup = mutableListOf<StatGroup>()
            for (i in 0..2) {
                val statUsers = mutableListOf<StatUser>(
                    StatUser(
                        id = "user_${i}",
                        username = "user ${i}",
                        avatar = "",
                        points = i
                    )
                )
                statGroup.add(
                    StatGroup(
                        id = "group_${i}",
                        name = "group ${i}",
                        users = statUsers
                    )
                )
            }
            val users = mutableStateListOf<StatUser>()
            val groups = mutableStateListOf<StatGroup>()
            groups.addAll(statGroup)

            setContent {
                EventResultScreen(
                    innerPaddingValues,
                    onExitScreen,
                    users,
                    groups
                )
            }

            for (i in statGroup.indices) {
                onNodeWithTag("groupName${statGroup[i].id}", useUnmergedTree = true).assertIsDisplayed()
                onNodeWithTag(
                    "groupName${statGroup[i].id}",
                    useUnmergedTree = true
                ).assertTextContains(statGroup[i].name)


                for (user in statGroup[i].users) {
                    onNodeWithTag("user_${user.id}", useUnmergedTree = true).assertIsDisplayed()
                    onNodeWithTag(
                        "user_${user.id}",
                        useUnmergedTree = true
                    ).assertTextContains(user.username+":")

                    onNodeWithTag("points_${user.id}", useUnmergedTree = true).assertIsDisplayed()
                    onNodeWithTag(
                        "points_${user.id}",
                        useUnmergedTree = true
                    ).assertTextContains(" ${user.points.toString()} ")
                }
            }
        }
    }
}