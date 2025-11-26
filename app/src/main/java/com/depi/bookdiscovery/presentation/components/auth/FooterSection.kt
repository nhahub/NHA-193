package com.depi.bookdiscovery.presentation.components.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.depi.bookdiscovery.R



@Composable
fun FooterText(
    statement: String,
    clickableText: String,
    onClick: () -> Unit
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ){
        Text(
            text = statement,
            modifier = Modifier
                .padding(1.dp),
            color = Color(0xFF6C6C6C),

        )
        ClickableTextAuth(clickableText = clickableText, onClick = onClick )

    }
}


@Composable
fun ClickableTextAuth(
    modifier: Modifier = Modifier,
    clickableText: String,
    onClick: ()-> Unit
){
    Text(
        text = clickableText,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .clickable{
                onClick()
            }
    )
}

@Composable
fun TermsAndPolicyRow(
    checked: Boolean,
    showError: Boolean ,
    onCheckedChange: (Boolean) -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row {

                Text(
                    text = stringResource(R.string.signup_agree),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                ClickableTextAuth(
                    clickableText = stringResource(R.string.signup_terms),
                    onClick = onTermsClick
                )

                Text(
                    text = stringResource(R.string.signup_concate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface

                )
                ClickableTextAuth(
                    clickableText = stringResource(R.string.signup_privacy),
                    onClick = onPrivacyClick,
                )
            }
        }
    }
    if (showError && !checked) {
        Text(
            text = stringResource(R.string.signup_terms_error_message),
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 40.dp)
        )
    }
}
