package com.example.taller3.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    unfocusedBorderThickness: Dp = 2.dp,
    focusedBorderThickness: Dp = 3.dp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = OutlinedTextFieldDefaults.colors()
    val textColor = if (enabled) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = textColor),
        cursorBrush = SolidColor(
            if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = singleLine,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                isError = isError,
                label = label,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                supportingText = supportingText,
                colors = colors,
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors,
                        shape = CircleShape,
                        focusedBorderThickness = focusedBorderThickness,
                        unfocusedBorderThickness = unfocusedBorderThickness,
                    )
                },
            )
        },
    )
}

@Preview(showBackground = true, name = "Default")
@Composable
private fun PillTextFieldDefaultPreview() {
    MaterialTheme {
        var text by rememberSaveable { mutableStateOf("") }
        PillTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Display name") },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "Password")
@Composable
private fun PillTextFieldPasswordPreview() {
    MaterialTheme {
        var text by rememberSaveable { mutableStateOf("secret#123") }
        var visible by rememberSaveable { mutableStateOf(false) }
        PillTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Password") },
            visualTransformation = if (visible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Filled.VisibilityOff
                        else Icons.Filled.Visibility,
                        contentDescription = if (visible) "Hide password" else "Show password",
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun PillTextFieldErrorPreview() {
    MaterialTheme {
        var text by rememberSaveable { mutableStateOf("bad") }
        PillTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Email") },
            isError = true,
            supportingText = { Text("Enter a valid email address") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "Disabled")
@Composable
private fun PillTextFieldDisabledPreview() {
    MaterialTheme {
        PillTextField(
            value = "Not editable",
            onValueChange = {},
            label = { Text("Phone") },
            enabled = false,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}
