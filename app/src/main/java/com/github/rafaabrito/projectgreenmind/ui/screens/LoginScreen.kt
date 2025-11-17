@file:Suppress("DEPRECATION")

package com.github.rafaabrito.projectgreenmind.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.rafaabrito.projectgreenmind.ui.components.LoginTextField
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.components.SocialMediaLogin
import com.github.rafaabrito.projectgreenmind.ui.theme.Roboto
import com.github.rafaabrito.projectgreenmind.ui.theme.ScreenOrientation
import com.github.rafaabrito.projectgreenmind.ui.theme.dimens

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    Surface {
        if (ScreenOrientation == Configuration.ORIENTATION_PORTRAIT){
            PortraitLoginScreen(onLoginSuccess = onLoginSuccess, onNavigateToRegister = onNavigateToRegister)
        }else{
            PortraitLoginScreen(onLoginSuccess = onLoginSuccess, onNavigateToRegister = onNavigateToRegister)
        }
    }
}

@Composable
private fun LandscapeLoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.Center)
    {
        LoginSection(onLoginSuccess = onLoginSuccess)
        SocialMediaSection()
    }
}

@Composable
private fun PortraitLoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ){
        // Possível estruturação de alteração de tema

        TopSection()
        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp))
        {
            LoginSection(onLoginSuccess = onLoginSuccess)
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

            DividerText()
            Spacer(modifier = Modifier.height(20.dp))
            SocialMediaSection()

        }
        Spacer( modifier = Modifier.weight(0.8f))
        CreateAccount(onNavigateToRegister = onNavigateToRegister)
        Spacer( modifier = Modifier.weight(0.3f))

    }
}
@Composable
private fun TopSection() {

    val screenHeight = LocalConfiguration.current.screenHeightDp
    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height((screenHeight / 2.12).dp),
            painter = painterResource(id = R.drawable.shape),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Row(
            modifier = Modifier.padding(
                top = (screenHeight/9).dp,
                bottom = MaterialTheme.dimens.medium2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(MaterialTheme.dimens.logoSize), // 100.dp
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = stringResource(id = R.string.app_logo),

                )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(id = R.string.title_project),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.slogan_project),
                    style = MaterialTheme.typography.titleMedium,

                )
            }
        }
        Text(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(alignment = Alignment.BottomCenter),
            text = stringResource(id = R.string.login_txt),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
@Composable
private fun LoginSection(onLoginSuccess:() -> Unit){
    LoginTextField(
        label = "Email",
        trailing = "",
        modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))
    LoginTextField(
        label = "Senha",
        trailing = "Esqueceu a senha?",
        modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.buttonHeight),
        onClick = onLoginSuccess,
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
            text = "ou continue com",
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
        Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))
        SocialMediaLogin(icon = R.drawable.facebook, text = "Facebook",
            modifier = Modifier.weight(1f)) { }
    }
}

@Composable
private fun ColumnScope.CreateAccount(
    onNavigateToRegister: () -> Unit // Mantém o callback
){
    Row(
        modifier = Modifier
            .align(alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Não possue uma conta?",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color(0xFF3B3B3B),
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(modifier = Modifier.width(4.dp)) // Adiciona um pequeno espaço

        Text(
            modifier = Modifier
                .clickable { onNavigateToRegister() }
                .padding(vertical = 4.dp),
            text = "Registre-se agora.",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.Black,
                fontFamily = Roboto,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Preview
@Composable
private fun LoginPreview() {
    LoginScreen(onLoginSuccess = {}, onNavigateToRegister = { })
}