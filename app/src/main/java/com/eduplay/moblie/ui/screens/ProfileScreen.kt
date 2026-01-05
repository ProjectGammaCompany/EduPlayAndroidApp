package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.R

@Composable
fun ProfileScreen(innerPaddingValues: PaddingValues) {

    var editEmail by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("example@com") }
    var showPassword by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("password") }
    val updateEmail: (String)->Unit = { newEmail: String ->
        email = newEmail
    }
    val loginEmailHasErrors: ()->Boolean = {false}

    Column(
        modifier = Modifier
            .padding(
                top = 0.dp, //innerPaddingValues.calculateTopPadding(),
                bottom = innerPaddingValues.calculateBottomPadding(),
                start = innerPaddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPaddingValues.calculateEndPadding(LayoutDirection.Ltr)
            )
            .fillMaxSize()
    ) {
        ProfileTopBar()

        // email
        Text(
            text = stringResource(R.string.profile_info),
            style = typography.titleLarge,
            modifier = Modifier.padding(bottom = 5.dp, top = 10.dp)
        )
        Row {
            Text(
                text = stringResource(R.string.email),
                style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 5.dp)
            )
            if (!editEmail) {
                Text(
                    text = email,
                    style = typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 5.dp)
                )
                IconButton(onClick = {editEmail = true}) {
                    Icon(ImageVector.vectorResource(R.drawable.edit),
                        stringResource(R.string.edit_email))
                }
            } else {
                OutlinedTextField(
                    value = email,
                    onValueChange = updateEmail,
                    maxLines = 1,
                    isError = false, //TODO("error check")
                    label = { Text(stringResource(R.string.email)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(bottom = 15.dp)
                )
            }
        }

        //Password
        Row {
            Text(
                text = stringResource(R.string.password),
                style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 5.dp)
            )
            if (showPassword) {
                Text(
                    text = password,
                    style = typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 5.dp)
                )

            }
            IconButton(onClick = {showPassword = !showPassword}) {
                Icon(if (!showPassword)Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    stringResource(R.string.show_password))
            }
        }

        OutlinedButton(
            onClick = { },
            colors = ButtonColors(
                containerColor = colorScheme.errorContainer,
                contentColor = colorScheme.errorContainer,
                disabledContainerColor = colorScheme.errorContainer,
                disabledContentColor = colorScheme.errorContainer
            ),
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)) { // TODO("logout")}
            Text(stringResource(R.string.logout),
                style = typography.labelLarge.copy(color = colorScheme.error)
                )
        }
    }
}

@Preview
@Composable
fun profilePreview() {
    ProfileScreen(PaddingValues())
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar() {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(stringResource(R.string.profile))
        }
    )
}