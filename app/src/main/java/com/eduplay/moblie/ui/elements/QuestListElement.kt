package com.eduplay.moblie.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.eduplay.moblie.R
import com.eduplay.moblie.models.EventTag
import com.eduplay.moblie.models.QuestShortInfo

@Composable
fun QuestListElement(
    questShortInfo: QuestShortInfo, onClick: () -> Unit, onFavouriteToggle: (Boolean) -> Unit,
    headers: State<NetworkHeaders>
) {

    val isFavourite = remember { mutableStateOf(questShortInfo.isFavourite) }

    Row(
        modifier = Modifier
            .padding(horizontal = 3.dp, vertical = 5.dp)
            .fillMaxWidth()
            .border(1.dp, colorScheme.tertiary, RoundedCornerShape(10.dp))
            .padding(5.dp)
            .clickable(true, onClick = { onClick() })
            .testTag("quest_element_main_container")
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(questShortInfo.imageUrl)
                .httpHeaders(headers.value)
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = questShortInfo.name,
            placeholder = painterResource(R.drawable.eduplaylogo),
            error = painterResource(id = R.drawable.ic_launcher_background),
            modifier = Modifier
                .testTag("quest_element_image")
                .height(60.dp)
                .width(60.dp)
                .clip(RoundedCornerShape(10.dp))


        )

        Column(
            modifier = Modifier
                .align(alignment = Alignment.CenterVertically)
                .weight(1f)
                .padding(start = 5.dp, end = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .padding(start = 5.dp, end = 2.dp)
                        .fillMaxWidth(0.7f)
                ) {
                    Text(
                        text = questShortInfo.name,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                        style = typography.titleLarge
                            .copy(color = colorScheme.onBackground),
                        modifier = Modifier
                            .testTag("quest_element_title")
                            .weight(1f)

                    )
                    if (questShortInfo.isDownloaded) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.download),
                            contentDescription = stringResource(id = R.string.downloaded),
                            tint = colorScheme.onBackground,
                            modifier = Modifier
                                .testTag("quest_element_isDownloaded")
                                .align(Alignment.CenterVertically)
                                .weight(0.15f)

                        )
                    }
                }
                Text(
                    text = String.format("%.2f⭐", questShortInfo.rate),
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.End,
                    style = typography.labelMedium
                        .copy(color = colorScheme.onBackground),
                    modifier = Modifier
                        .testTag("quest_element_rate")
                        .weight(0.25f)
                        .align(Alignment.CenterVertically)

                )
            }
            // список тегов
            Row(
                modifier = Modifier.testTag("tag_row").fillMaxWidth()
            ) {
                questShortInfo.tags.forEach { tag ->
                    QuestTag(tag.name)
                }

            }
            if (questShortInfo.description.isNotBlank()) {
                Text(
                    text = questShortInfo.description,
                    textAlign = TextAlign.Start,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = typography.bodyMedium
                        .copy(color = colorScheme.onBackground),
                    modifier = Modifier
                        .padding(start = 5.dp, end = 3.dp, top = 5.dp, bottom = 3.dp)
                )
            }
        }
        IconButton(
            {
                isFavourite.value = !isFavourite.value
                onFavouriteToggle(isFavourite.value)
            }, modifier = Modifier
                .testTag("quest_element_favourite_btn")
                .align(Alignment.CenterVertically)

        ) {
            if (isFavourite.value) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.star_filled),
                    contentDescription = stringResource(id = R.string.remove_from_favourite),
                    tint = colorScheme.onBackground,
                    modifier = Modifier.testTag("quest_element_is_favourite")
                )
            } else {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.star),
                    contentDescription = stringResource(id = R.string.add_to_favourite),
                    tint = colorScheme.onBackground,
                    modifier = Modifier.testTag("quest_element_isNotFavourite")
                )
            }
        }
    }
}

@Composable
private fun QuestTag(tagName: String) {
    Text(
        text = tagName,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = colorScheme.onSecondary,
        style = typography.labelSmall
            .copy(color = colorScheme.onSecondary),
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .wrapContentWidth()
            .background(colorScheme.secondary, shape = RoundedCornerShape(5.dp))
            .padding(2.dp)
    )
}

@Composable
@Preview
fun QuestListElementPreview() {
    val headers = remember { mutableStateOf(NetworkHeaders.Builder().build()) }
    Column {
        QuestListElement(
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "long enough description",
                "some url",
                4.3333333,
                true,
                listOf(
                    EventTag("", "tag 1"),
                    EventTag("", "funny"),
                    EventTag("", "long as hell tag")
                ),
                true
            ), {}, {}, headers
        )

        QuestListElement(
            QuestShortInfo(
                "id_funny",
                "Название квеста",
                "",
                "some url",
                4.3333333,
                false,
                listOf(
                    EventTag("", "tag 1"),
                    EventTag("", "funny"),
                    EventTag("", "long as hell tag"),
                    EventTag("", "long as hell tag"),
                    EventTag("", "long as hell tag"),
                    EventTag("", "long as hell tag"),
                ),
                false
            ), {},
            {},
            headers

        )

        QuestListElement(
            QuestShortInfo(
                "id_funny",
                "The very fuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuunny name",
                "loooooooooooooong \n  enough  \n description \n long",
                "some url",
                5.00,
                false,
                listOf(),
                false
            ), {}, {},
            headers
        )
    }
}