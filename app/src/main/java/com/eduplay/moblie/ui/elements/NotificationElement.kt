package com.eduplay.moblie.ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.eduplay.moblie.R
import com.eduplay.moblie.models.NotificationData
import com.eduplay.moblie.useCases.DateConverter
import java.time.LocalDateTime

@Composable
fun NotificationElement(
    notificationData: NotificationData,
    navController: NavController
) {
    val onNavigate = { eventId: String ->
        navController.navigate("event_screen/$eventId")
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        when (notificationData) {
            is NotificationData.FavoriteNotificationData -> FavoriteEventNotification(
                notificationData,
                onNavigate
            )

            is NotificationData.EndEventNotificationData -> TODO()
            is NotificationData.EmptyNotification -> {}
        }
        HorizontalDivider(color = colorScheme.tertiary)
    }


}

@Composable
private fun FavoriteEventNotification(
    notification: NotificationData.FavoriteNotificationData,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable(enabled = true, onClick = { onNavigate(notification.eventId) })
    ) {
        Text(
            stringResource(R.string.favorite_event) + " \"${notification.eventName}\" " + stringResource(
                R.string.started
            ),
            style = typography.titleMedium
        )
        Text(
            DateConverter.convertForDisplay(notification.date),
            style = typography.bodyLarge,
            modifier = Modifier.align(Alignment.End)
        )
    }
}


@Composable
@Preview
private fun NotificationPreview() {
    val favoriteEventStarted = NotificationData.FavoriteNotificationData(
        "eve1", "Event 1",
        LocalDateTime.now().plusDays(10)
    )
    NotificationElement(
        favoriteEventStarted,
        rememberNavController()
    )
}