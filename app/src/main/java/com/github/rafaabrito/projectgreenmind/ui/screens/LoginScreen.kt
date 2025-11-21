@file:Suppress("DEPRECATION")

package com.github.rafaabrito.projectgreenmind.ui.screens

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.rafaabrito.projectgreenmind.ui.components.LoginTextField
import com.github.rafaabrito.projectgreenmind.R
import com.github.rafaabrito.projectgreenmind.ui.components.SocialMediaLogin
import com.github.rafaabrito.projectgreenmind.ui.theme.Roboto
import com.github.rafaabrito.projectgreenmind.ui.theme.ScreenOrientation
import com.github.rafaabrito.projectgreenmind.ui.theme.dimens
import com.github.rafaabrito.projectgreenmind.ui.viewModel.LoginViewModel

@Composable
    fun LoginScreen(
        viewModel: LoginViewModel = hiltViewModel(),
        onLoginSuccess: (userId: Int) -> Unit,
        onNavigateToRegister: () -> Unit,
    ) {
    val loginState by viewModel.loginState.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.handleGoogleSignInResult(result.data)
            } else {
                viewModel.handleGoogleSignInResult(null)
            }
        }
    )

    // Estados locais para campos
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Indica se o formulário está carregando
    val isLoading = loginState is LoginViewModel.LoginState.Loading

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginViewModel.LoginState.Success -> {
                onLoginSuccess(state.user.userId) // Passa o ID do usuário
            }
            is LoginViewModel.LoginState.Error -> {
                // Mostrar Toast
            }
            // 🟢 NOVO TRATAMENTO PARA LANÇAR O INTENTSENDER
            is LoginViewModel.LoginState.AwaitingSocialAuth -> {
                launcher.launch(
                    IntentSenderRequest.Builder(state.intentSender).build()
                )
            }
            else -> Unit
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        // Lógica de orientação mantida, mas agora chamando a versão atualizada
        if (ScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            PortraitLoginScreen(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                isLoading = isLoading,
                onLoginClick = { viewModel.signInLocal(email, password) },
                onGoogleClick = { viewModel.getGoogleSignInIntentSender() },
                onNavigateToRegister = onNavigateToRegister
            )
        } else {
            // Se Landscape for igual a Portrait, use os mesmos argumentos
            PortraitLoginScreen(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                isLoading = isLoading,
                onLoginClick = { viewModel.signInLocal(email, password) },
                onGoogleClick = { viewModel.getGoogleSignInIntentSender() },
                onNavigateToRegister = onNavigateToRegister
            )
        }
    }
}

@Composable
private fun PortraitLoginScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopLoginSection()
        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            LoginSection(
                email = email,
                onEmailChange = onEmailChange,
                password = password,
                onPasswordChange = onPasswordChange,
                onLoginClick = onLoginClick,
                isLoading = isLoading,
                isButtonEnabled = email.isNotEmpty() && password.isNotEmpty() && !isLoading
            )
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))

            DividerText()
            Spacer(modifier = Modifier.height(20.dp))

            SocialMediaSection(
                onGoogleClick = onGoogleClick,
                enabled = !isLoading
            )
        }

        Spacer(modifier = Modifier.weight(0.8f))
        CreateAccount(onNavigateToRegister = onNavigateToRegister)
        Spacer(modifier = Modifier.weight(0.3f))
    }
}
@Composable
private fun TopLoginSection() {

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
private fun LoginSection(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean,
    isButtonEnabled: Boolean
) {
    // 1. Campo de Email
    LoginTextField(
        value = email,
        onValueChange = onEmailChange,
        label = "Email",
        trailing = "",
        keyboardType = KeyboardType.Email,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))

    // 2. Campo de Senha
    LoginTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = "Senha",
        trailing = "Esqueceu a senha?",
        isPassword = true,
        keyboardType = KeyboardType.Password,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

    // 3. Botão de Login
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.buttonHeight),
        onClick = onLoginClick, // Executa a ação do ViewModel
        enabled = isButtonEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = "Log in",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
            )
        }
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
private fun SocialMediaSection(
    onGoogleClick: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        SocialMediaLogin(icon = R.drawable.google, text = "Google",
            modifier = Modifier.weight(1f),
            enabled = enabled,
            onClick = onGoogleClick)
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
    LoginScreen(onLoginSuccess = {}, onNavigateToRegister = { })}