package com.example.taller3.ui.components

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.taller3.ui.theme.AppTheme

private val PASSWORD_REGEX = Regex("^(?=.*[^A-Za-z0-9\\s]).{8,}$")

private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

private fun isValidPassword(password: String) = PASSWORD_REGEX.matches(password)

private fun isValidPhone(phone: String) = Patterns.PHONE.matcher(phone).matches() &&
        phone.count { it.isDigit() } in 7..15

@Composable
fun RegisterForm(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    registerEnabled: Boolean = true,
    onRegister: (email: String, displayName: String, phone: String, password: String) -> Unit =
        { _, _, _, _ -> },
) {
    var email by rememberSaveable { mutableStateOf("") }
    var displayName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirm by rememberSaveable { mutableStateOf(false) }

    // A field is flagged once it has content or the user has left it with the keyboard action.
    var emailTouched by rememberSaveable { mutableStateOf(false) }
    var displayNameTouched by rememberSaveable { mutableStateOf(false) }
    var phoneTouched by rememberSaveable { mutableStateOf(false) }
    var passwordTouched by rememberSaveable { mutableStateOf(false) }
    var confirmTouched by rememberSaveable { mutableStateOf(false) }

    val emailInvalid = (emailTouched || email.isNotEmpty()) && !isValidEmail(email)
    val displayNameInvalid = displayNameTouched && displayName.isBlank()
    val phoneInvalid = (phoneTouched || phone.isNotEmpty()) && !isValidPhone(phone)
    val passwordInvalid = (passwordTouched || password.isNotEmpty()) && !isValidPassword(password)
    val mismatch = (confirmTouched || confirmPassword.isNotEmpty()) && confirmPassword != password

    val formValid = isValidEmail(email) && displayName.isNotBlank() &&
            isValidPhone(phone) && isValidPassword(password) && confirmPassword == password
    val canRegister = enabled && registerEnabled && formValid
    val focusManager = LocalFocusManager.current
    val submit = { onRegister(email.trim(), displayName.trim(), phone.trim(), password) }

    @Composable
    fun VisibilityToggle(visible: Boolean, onToggle: () -> Unit) {
        IconButton(onClick = onToggle, enabled = enabled) {
            Icon(
                imageVector = if (visible) Icons.Filled.VisibilityOff
                else Icons.Filled.Visibility,
                contentDescription = stringResource(
                    if (visible) R.string.register_form_hide_password_icon
                    else R.string.register_form_show_password_icon
                ),
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PillTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.register_form_email_label)) },
            singleLine = true,
            enabled = enabled,
            isError = emailInvalid,
            supportingText = if (emailInvalid) {{ Text(stringResource(R.string.register_form_email_invalid_supporting_text)) }} else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    emailTouched = true
                    focusManager.moveFocus(FocusDirection.Down)
                },
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        PillTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text(stringResource(R.string.register_form_display_name_label)) },
            singleLine = true,
            enabled = enabled,
            isError = displayNameInvalid,
            supportingText = if (displayNameInvalid) {{ Text(stringResource(R.string.register_form_display_name_invalid_supporting_text)) }} else null,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    displayNameTouched = true
                    focusManager.moveFocus(FocusDirection.Down)
                },
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        PillTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.register_form_phone_label)) },
            singleLine = true,
            enabled = enabled,
            isError = phoneInvalid,
            supportingText = if (phoneInvalid) {{ Text(stringResource(R.string.register_form_phone_invalid_supporting_text)) }} else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    phoneTouched = true
                    focusManager.moveFocus(FocusDirection.Down)
                },
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        PillTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.register_form_password_label)) },
            singleLine = true,
            enabled = enabled,
            isError = passwordInvalid,
            supportingText = if (passwordInvalid) {
                { Text(stringResource(R.string.register_form_password_invalid_supporting_text)) }
            } else null,
            visualTransformation = if (showPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                VisibilityToggle(showPassword) { showPassword = !showPassword }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    passwordTouched = true
                    focusManager.moveFocus(FocusDirection.Down)
                },
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        PillTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.register_form_confirm_password_label)) },
            singleLine = true,
            enabled = enabled,
            isError = mismatch,
            supportingText = if (mismatch) {{ Text(stringResource(R.string.register_form_password_mismatch_supporting_text)) }} else null,
            visualTransformation = if (showConfirm) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                VisibilityToggle(showConfirm) { showConfirm = !showConfirm }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    confirmTouched = true
                    if (canRegister) submit()
                },
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = submit,
            enabled = canRegister,
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.register_form_register_button)) }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterFormPreview() {
    AppTheme { RegisterForm() }
}
