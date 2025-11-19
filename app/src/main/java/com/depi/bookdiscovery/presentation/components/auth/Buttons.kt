package com.depi.bookdiscovery.presentation.components.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.depi.bookdiscovery.R





@Composable
fun RegisterButton(
    text : String,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),

        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        shape = RoundedCornerShape(14.dp),

    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge

        )

    }
}

@Composable
fun OrDivider(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            thickness = 1.dp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "OR",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall

        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            thickness = 1.dp,
            modifier = Modifier.weight(1f)

        )
    }
}


@Composable
fun GoogleButton(
    onClick: () -> Unit,
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                shape = RoundedCornerShape(14.dp),
                width = 0.4.dp,
                color = MaterialTheme.colorScheme.outline
            )
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            Icon(
                painter = painterResource(id = R.drawable.google_icon),
                contentDescription = "Continue with Google",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Text(
                text = "Continue with Google",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium

            )

        }
    }
}
