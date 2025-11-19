package com.depi.bookdiscovery.presentation.components.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.depi.bookdiscovery.R
import com.depi.bookdiscovery.ui.theme.borderColor
import com.depi.bookdiscovery.ui.theme.inputBackground

@Composable
fun TextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    error: String? = null,


){
    var passwordVisible by remember { mutableStateOf(false) }

    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        focusedBorderColor = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
        errorBorderColor = MaterialTheme.colorScheme.error,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedLabelColor = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
        errorLabelColor = MaterialTheme.colorScheme.error,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorSupportingTextColor = MaterialTheme.colorScheme.error,
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = TextFieldDefaults.MinHeight),
        leadingIcon = {
            Icon(leadingIcon,
            contentDescription = label,
                tint = if (error != null) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )},
        trailingIcon = {
            if(isPassword){
                val image = if (passwordVisible)
                    R.drawable.outline_visibility
                else
                    R.drawable.outline_visibility_off

                IconButton(
                    onClick = {
                        passwordVisible =! passwordVisible
                    }
                ) {
                    Icon(
                        painter = painterResource(id = image),
                        contentDescription = if(passwordVisible) "Hide password" else "Show password",
                        tint = if (error != null) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = modifier.size(20.dp)
                    )
                }

            }
        },
        visualTransformation =
            if(isPassword && !passwordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
        colors = colors,
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        isError = error != null,

        supportingText = {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
    if (error != null) {
        Spacer(modifier = Modifier.height(4.dp))
    }

}