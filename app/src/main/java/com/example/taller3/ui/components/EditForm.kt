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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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

private fun isValidPassword(password: String) = PASSWORD_REGEX.matches(password)

private fun isValidPhone(phone: String) = Patterns.PHONE.matcher(phone).matches() &&
        phone.count { it.isDigit() } in 7..15

/**
 * Edit profile form: a read-only [email], editable display name and phone, and a password change.
 *
 * [onUserInfoChange] receives the display name and phone when the user saves their info;
 * [onPasswordChange] receives the new password when the user changes it.
 */
@Composable
fun EditForm(
    email: String,
    displayName: String,
    phone: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onUserInfoChange: (displayName: String, phone: String) -> Unit = { _, _ -> },
    onPasswordChange: (password: String) -> Unit = {},
) {
    var name by rememberSaveable(displayName) { mutableStateOf(displayName) }
    var phoneValue by rememberSaveable(phone) { mutableStateOf(phone) }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirm by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val phoneInvalid = phoneValue.isNotEmpty() && !isValidPhone(phoneValue)
    val passwordInvalid = password.isNotEmpty() && !isValidPassword(password)
    val mismatch = confirmPassword.isNotEmpty() && confirmPassword != password

    val infoChanged = name.trim() != displayName.trim() || phoneValue.trim() != phone.trim()
    val infoValid = name.isNotBlank() && isValidPhone(phoneValue)
    val passwordValid = isValidPassword(password) && confirmPassword == password

    @Composable
    fun VisibilityToggle(visible: Boolean, onToggle: () -> Unit) {
        IconButton(onClick = onToggle, enabled = enabled) {
            Icon(
                imageVector = if (visible) Icons.Filled.VisibilityOff
                else Icons.Filled.Visibility,
                contentDescription = stringResource(
                    if (visible) R.string.edit_form_hide_password_icon
                    else R.string.edit_form_show_password_icon
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
            onValueChange = {},
            label = { Text(stringResource(R.string.edit_form_email_label)) },
            singleLine = true,
            enabled = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        PillTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.edit_form_display_name_label)) },
            singleLine = true,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        PillTextField(
            value = phoneValue,
            onValueChange = { phoneValue = it },
            label = { Text(stringResource(R.string.edit_form_phone_label)) },
            singleLine = true,
            enabled = enabled,
            isError = phoneInvalid,
            supportingText = if (phoneInvalid) {{ Text(stringResource(R.string.edit_form_phone_invalid_supporting_text)) }} else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = { onUserInfoChange(name.trim(), phoneValue.trim()) },
            enabled = enabled && infoChanged && infoValid,
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.edit_form_save_changes_button)) }

        PillTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.edit_form_new_password_label)) },
            singleLine = true,
            enabled = enabled,
            isError = passwordInvalid,
            supportingText = if (passwordInvalid) {
                { Text(stringResource(R.string.edit_form_password_invalid_supporting_text)) }
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
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        PillTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.edit_form_confirm_password_label)) },
            singleLine = true,
            enabled = enabled,
            isError = mismatch,
            supportingText = if (mismatch) {{ Text(stringResource(R.string.edit_form_password_mismatch_supporting_text)) }} else null,
            visualTransformation = if (showConfirm) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                VisibilityToggle(showConfirm) { showConfirm = !showConfirm }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedButton(
            onClick = {
                onPasswordChange(password)
                password = ""
                confirmPassword = ""
            },
            enabled = enabled && passwordValid,
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.edit_form_change_password_button)) }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditFormPreview() {
    AppTheme {
        EditForm(email = "jane@example.com", displayName = "Jane Doe", phone = "3131234567")
    }
}
