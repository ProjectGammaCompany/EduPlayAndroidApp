package com.eduplay.moblie.ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.eduplay.moblie.R
import com.eduplay.moblie.models.NotificationData
import com.eduplay.moblie.models.NotificationData.EndEventNotificationData
import com.eduplay.moblie.models.NotificationData.EndEventNotificationData.TimeLeft
import com.eduplay.moblie.models.NotificationData.FavoriteNotificationData
import com.eduplay.moblie.models.NotificationData.EmptyNotification
import com.eduplay.moblie.useCases.DateConverter
import java.time.LocalDateTime

@Composable
fun NotificationElement(
    notificationData: NotificationData,
    navController: NavController,
    showNotification: Boolean = true,
    showDeleteButton: Boolean = false,
    onDelete: (String) -> Unit = {_->}
) {
    if (showNotification) {
        val onNavigate = { eventId: String ->
            navController.navigate("event_screen/$eventId")
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            when (notificationData) {
                is FavoriteNotificationData -> FavoriteEventNotification(
                    notificationData,
                    onNavigate,
                    showDeleteButton,
                    onDelete
                )

                is EndEventNotificationData -> EndEventNotification(
                    notificationData,
                    onNavigate,
                    showDeleteButton,
                    onDelete
                )

                is EmptyNotification -> {}
            }
            HorizontalDivider(color = colorScheme.tertiary)
        }
    }


}

@Composable
private fun FavoriteEventNotification(
    notification: FavoriteNotificationData,
    onNavigate: (String) -> Unit,
    showDeleteButton: Boolean,
    onDelete: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable(enabled = true, onClick = { onNavigate(notification.eventId) })
    ) {
        Row {
            Text(
                text = stringResource(R.string.favorite_event) + " \"${notification.eventName}\" "
                        + stringResource(R.string.started),
                style = typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
            )
            if (showDeleteButton) {
                IconButton(onClick = { onDelete(notification.notificationId) }) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.cross),
                        stringResource(R.string.delete_notification)
                    )
                }
            }
        }
        Text(
            DateConverter.convertForDisplay(notification.date),
            style = typography.bodyLarge,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun EndEventNotification(
    notification: EndEventNotificationData,
    onNavigate: (String) -> Unit,
    showDeleteButton: Boolean,
    onDelete: (String) -> Unit
) {
    val title = StringBuilder()

    if (notification.notStartedFavorite) {
        title.append(stringResource(R.string.you_havent_started))
        title.append(" \"${notification.eventName}\".")
    } else {
        title.append("\"${notification.eventName}\" ")
        title.append(stringResource(R.string.event_is_ending_soon))
        title.append(".")
    }
    val body = StringBuilder()
    body.append("\"${notification.eventName}\" ")
    body.append(stringResource(R.string.is_ending))
    body.append(" ")
    when(notification.timeLeft) {
        TimeLeft.HOUR -> body.append(stringResource(R.string.in_hour) + ".")
        TimeLeft.DAY -> body.append(stringResource(R.string.tomorrow) + ".")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable(enabled = true, onClick = { onNavigate(notification.eventId) })
    ) {
        Row {
            Text(
                text = title.toString(),
                style = typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically)

            )
            if (showDeleteButton) {
                IconButton(onClick = { onDelete(notification.notificationId) }) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.cross),
                        stringResource(R.string.delete_notification)
                    )
                }
            }
        }
        Text(
            body.toString(),
            style = typography.bodyLarge,
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
        "n1","eve1", "Event 1",
        LocalDateTime.now().plusDays(10)
    )

    val endNotificationHour = EndEventNotificationData(
        "n1","eve1", "Event 1",
        LocalDateTime.now().plusDays(10),
        timeLeft = TimeLeft.HOUR,
        notStartedFavorite = false
    )

    val notStartedendNotificationHour = EndEventNotificationData(
        "n1","eve1", "Event 1",
        LocalDateTime.now().plusDays(10),
        timeLeft = TimeLeft.HOUR,
        notStartedFavorite = true
    )

    val endNotificationDay = EndEventNotificationData(
        "n1","eve1", "Event 1",
        LocalDateTime.now().plusDays(10),
        timeLeft = TimeLeft.DAY,
        notStartedFavorite = false
    )

    val notStartedendNotificationDay = EndEventNotificationData(
        "n1", "eve1", "Event 1",
        LocalDateTime.now().plusDays(10),
        timeLeft = TimeLeft.DAY,
        notStartedFavorite = true
    )
    Column {
        NotificationElement(
            favoriteEventStarted,
            rememberNavController()
        )
        NotificationElement(
            endNotificationHour,
            rememberNavController()
        )

        NotificationElement(
            notStartedendNotificationHour,
            rememberNavController()
        )
        NotificationElement(
            endNotificationDay,
            rememberNavController()
        )
        NotificationElement(
            notStartedendNotificationDay,
            rememberNavController()
        )
    }
}