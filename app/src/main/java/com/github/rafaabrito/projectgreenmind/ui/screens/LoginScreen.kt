package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.rafaabrito.projectgreenmind.LoginTextField
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.SocialMediaLogin
import com.github.rafaabrito.projectgreenmind.ui.theme.Roboto

@Composable
fun LoginScreen() {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize()
        ){
            // Possível estruturação de alteração de tema

            TopSection()
            Spacer(modifier = Modifier.height(36.dp))
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp))
            {
                LoginSection()
                Spacer(modifier = Modifier.height(30.dp))

                DividerText()
                Spacer(modifier = Modifier.height(20.dp))
                SocialMediaSection()

                Box(
                    modifier = Modifier.fillMaxHeight(fraction = 0.8f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ){
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xFF3B3B3B),
                                    fontSize = 14.sp,
                                    fontFamily = Roboto,
                                    fontWeight = FontWeight.Normal
                                )
                            ){
                                append("Não possue uma conta?")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontFamily = Roboto,
                                    fontWeight = FontWeight.Medium
                                )
                            ){
                                append(" ")
                                append("Registre-se agora.")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopSection() {
    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.4f),
            painter = painterResource(id = R.drawable.shape),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Row(
            modifier = Modifier.padding(top = 40.dp, bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = stringResource(id = R.string.app_logo),

                )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.title_project),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = stringResource(id = R.string.slogan_project),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Text(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(alignment = Alignment.BottomCenter),
            text = stringResource(id = R.string.login_txt),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
@Composable
private fun LoginSection(){
    LoginTextField(
        label = "Email",
        trailing = "",
        modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(15.dp))
    LoginTextField(
        label = "Senha",
        trailing = "Esqueceu a senha?",
        modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(20.dp))
    Button(
        modifier = Modifier.fillMaxWidth()
            .height(40.dp),
        onClick = {},
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp)
    )
    {
        Text(text = "Log in",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium ))
    }
}


@Composable
private fun DividerText(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Color.Black
        )

        Text(
            text = "OU",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.DarkGray,
            )
        )

        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = Color.Black
        )
    }
}

@Composable
private fun SocialMediaSection(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SocialMediaLogin(icon = R.drawable.google, text = "Google",
            modifier = Modifier.weight(1f)) { }
        Spacer(modifier = Modifier.width(20.dp))
        SocialMediaLogin(icon = R.drawable.facebook, text = "Facebook",
            modifier = Modifier.weight(1f)) { }
    }
}
@Preview
@Composable
private fun LoginPreview() {
    LoginScreen()
}