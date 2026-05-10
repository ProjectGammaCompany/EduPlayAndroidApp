package com.eduplay.moblie.ui.elements

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.clearText
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
import androidx.navigation.NavController
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.viewmodel.JoinByCodeViewModel

@Composable
fun JoinByCodeDialog(
    onDismissRequest: () -> Unit,
    navController: NavController,
    viewModel: JoinByCodeViewModel = hiltViewModel()
) {
    if (viewModel.noInternet.value) {
        NoInternetConnectionToast()
    }
    if (viewModel.unauthorised.value) {
        Log.d("main_screen", "unauthorised")
        AuthScreenNavigator(navController)
    }

    val onGetFields = { code: String -> viewModel.getFields(code) }
    val resetDialog = {
        viewModel.proceedToPassword.value = false
        onDismissRequest()
    }

    if (!viewModel.proceedToPassword.value) {
        CodeDialog(
            resetDialog,
            onGetFields,
            viewModel.badCode
        )
    } else {
        PasswordDialog(
            resetDialog,
            viewModel.showGroupFields,
            viewModel.badPasswords,
            viewModel::validatePasswords
        )
    }

}

@Composable
fun CodeDialog(
    onDismissRequest: () -> Unit,
    onGetFields: (String) -> Unit,
    badCode: State<Boolean>
) {
    val codeState = rememberTextFieldState()

    AlertDialog(
        title = {
            Text(text = stringResource(R.string.join_code))
        },
        text = {
            TextField(
                codeState,
                placeholder = { Text(stringResource(R.string.join_code)) },
                label = {
                    if (badCode.value)
                        Text(stringResource(R.string.wrong_code))
                    else
                        Text(stringResource(R.string.join_code))
                },
                isError = badCode.value
            )
        },
        onDismissRequest = {
            codeState.clearText()
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = { onGetFields(codeState.text.toString()) },
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

@Composable
fun PasswordDialog(
    onDismissRequest: () -> Unit,
    showGroupFields: State<Boolean>,
    badPasswords: State<Boolean>,
    sendPasswords: (String, String, String) -> Unit
) {
    val eventPassword = rememberTextFieldState()
    val group = rememberTextFieldState()
    val groupPassword = rememberTextFieldState()

    AlertDialog(
        title = {
            Text(text = stringResource(R.string.password))
        },
        text = {
            Column {
                TextField(
                    eventPassword,
                    placeholder = { Text(stringResource(R.string.event_password)) },
                    label = {
                        if (badPasswords.value)
                            Text(stringResource(R.string.incorrect_password))
                        else
                            Text(stringResource(R.string.event_password))
                    },
                    isError = badPasswords.value,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                if (showGroupFields.value) {
                    TextField(
                        group,
                        placeholder = { Text(stringResource(R.string.group)) },
                        label = {
                            if (badPasswords.value) Text(stringResource(R.string.incorrect_password)) else Text(
                                stringResource(R.string.group)
                            )
                        },
                        isError = badPasswords.value,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    TextField(
                        groupPassword,
                        placeholder = { Text(stringResource(R.string.group_password)) },
                        label = {
                            if (badPasswords.value)
                                Text(stringResource(R.string.incorrect_password))
                            else
                                Text(stringResource(R.string.group_password))
                        },
                        isError = badPasswords.value,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    sendPasswords(
                        eventPassword.text.toString(),
                        group.text.toString(),
                        groupPassword.text.toString()
                    )
                },
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