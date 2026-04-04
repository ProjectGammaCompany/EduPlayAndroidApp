package com.eduplay.moblie.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.TableInfo
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.viewmodel.JoinByGroupViewModel

@Composable
fun JoinGroupDialog(
    eventId: String,
    onDismissRequest: () -> Unit,
    onProceedRequest: () -> Unit,
    viewModel: JoinByGroupViewModel = hiltViewModel()
) {
    if (viewModel.canProceedToEvent.value) {
        onProceedRequest()
    }
    JoinGroupDialog(
        onDismissRequest,
        {group: String, password: String -> viewModel.joinByGroup(eventId, group, password)},
        viewModel.badPassword
    )
}

@Composable
fun JoinGroupDialog(
    onDismissRequest: () -> Unit,
    onProceedRequest: (String, String) -> Unit,
    badPassword: State<Boolean>
) {
    val groupName = rememberTextFieldState()
    val groupPassword= rememberTextFieldState()
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.join_group))
        },
        text = {
            Column {
                TextField(
                    groupName,
                    placeholder = { Text(stringResource(R.string.group)) },
                    label = {
                        if (badPassword.value)
                            Text(stringResource(R.string.incorrect_password))
                        else
                            Text(stringResource(R.string.group))
                    },
                    isError = badPassword.value,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                TextField(
                    groupPassword,
                    placeholder = { Text(stringResource(R.string.group_password)) },
                    label = {
                        if (badPassword.value)
                            Text(stringResource(R.string.incorrect_password))
                        else
                            Text(stringResource(R.string.group_password))
                    },
                    isError = badPassword.value,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = { onProceedRequest(groupName.text.toString(), groupPassword.text.toString()) },
                modifier = Modifier.padding(8.dp),
            ) {
                Text(stringResource(R.string.proceed))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() },
                modifier = Modifier.padding(8.dp),
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )
}


@Preview
@Composable
fun PreviewJoinGroup() {
    JoinGroupDialog(
        {},
        {_, _->},
        remember { mutableStateOf(false) }
    )
}