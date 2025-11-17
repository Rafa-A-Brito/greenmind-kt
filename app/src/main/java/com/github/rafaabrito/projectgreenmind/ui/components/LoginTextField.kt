package com.github.rafaabrito.projectgreenmind.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.github.rafaabrito.projectgreenmind.ui.theme.LightBlueWhite

@Composable
fun LoginTextField(
    modifier: Modifier = Modifier,
    label: String,
    trailing: String,
){
    var text by remember {
        mutableStateOf("")
    }

    TextField(
        modifier = modifier,
        value = text,
        onValueChange = {text = it},
        label = {
            Text(text = label, style= MaterialTheme.typography.labelMedium)
        },
        colors = TextFieldDefaults.colors(
            unfocusedPlaceholderColor = Color(0xFF475569),
            focusedPlaceholderColor = Color.Black,
            unfocusedContainerColor = LightBlueWhite,
            focusedContainerColor = LightBlueWhite,

            ),
        trailingIcon = {
            TextButton(onClick = {/*TODO*/}) {
                Text(
                    text = trailing,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    )
}
