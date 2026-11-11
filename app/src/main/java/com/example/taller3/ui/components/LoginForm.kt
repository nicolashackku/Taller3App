package com.example.taller3.ui.components

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taller3.R

private val PASSWORD_REGEX = Regex("^(?=.*[^A-Za-z0-9\\s]).{8,}$")

private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

private fun isValidPassword(password: String) = PASSWORD_REGEX.matches(password)

@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onRegister: () -> Unit = {},
    onForgotPassword: (email: String) -> Unit = { _ -> },
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    val emailInvalid = email.isNotEmpty() && !isValidEmail(email)
    val passwordInvalid = password.isNotEmpty() && !isValidPassword(password)

    val focusManager = LocalFocusManager.current
    val canLogin = enabled && isValidEmail(email) && isValidPassword(password)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PillTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.login_form_email_label)) },
            singleLine = true,
            enabled = enabled,
            isError = emailInvalid,
            supportingText = { if (emailInvalid) Text(stringResource(R.string.login_form_email_invalid_supporting_text)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        PillTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.login_form_password_label)) },
            singleLine = true,
            enabled = enabled,
            isError = passwordInvalid,
            supportingText = {
                if (passwordInvalid) Text(stringResource(R.string.login_form_password_invalid_supporting_text))
            },
            visualTransformation = if (showPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }, enabled = enabled) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.VisibilityOff
                        else Icons.Filled.Visibility,
                        contentDescription = stringResource(
                            if (showPassword) R.string.login_form_hide_password_icon
                            else R.string.login_form_show_password_icon
                        ),
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { if (canLogin) onLogin(email, password) }),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = { onLogin(email, password) },
                enabled = canLogin,
                modifier = Modifier.weight(1f),
            ) { Text(stringResource(R.string.login_form_login_button)) }
            OutlinedButton(
                onClick = onRegister,
                enabled = enabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.login_form_register_button))
            }
        }
        TextButton(
            onClick = { onForgotPassword(email) },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.login_form_forgot_password_button), color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginFormPreview() {
    MaterialTheme { LoginForm() }
}
