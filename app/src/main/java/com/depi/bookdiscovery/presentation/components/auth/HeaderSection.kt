package com.depi.bookdiscovery.presentation.components.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.depi.bookdiscovery.R


@Composable
fun HeaderGreeting(
   image: Int,
   line1: String,
   line2: String,
   modifier: Modifier = Modifier
){
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp)
    ){
        Card (
            shape = CircleShape,
            modifier = modifier
                .padding(16.dp)
                .size(66.dp)
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = "books",
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                Alignment.Center
            )

        }
        Text(
            text = line1,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom =  4.dp)
        )
        Text(
            text = line2,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center

        )


    }
}

@Preview(showBackground = true)
@Composable
fun HeaderGreetingPreview(){
    HeaderGreeting(
        image = R.drawable.books,
        line1 = "Welcome Back",
        line2 = "Sign in to continue your reading journey"
    )
}