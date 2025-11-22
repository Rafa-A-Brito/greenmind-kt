package com.github.rafaabrito.projectgreenmind.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.github.rafaabrito.projectgreenmind.ui.components.LoginTextField
import com.github.rafaabrito.projectgreenmind.ui.components.SocialMediaLogin
import com.github.rafaabrito.projectgreenmind.ui.viewModel.RegisterViewModel
import com.github.rafaabrito.projectgreenmind.ui.viewModel.RegisterViewModel.RegisterState
import com.github.rafaabrito.projectgreenmind.R

import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.GetCredentialException
import kotlinx.coroutines.CancellationException

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: (userId: Int) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? androidx.activity.ComponentActivity

    val registerState by viewModel.registerState.collectAsState()

    val credentialManager = remember(context) { CredentialManager.create(context) }

    // Estados locais para campos (Fonte da Verdade na UI)
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isEnabled = registerState is RegisterViewModel.RegisterState.Loading

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    val scrollState = rememberScrollState() // Definido fora do Column

    // Tratamento de Sucesso e Erro
    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                Toast.makeText(context, "Registro efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                onRegisterSuccess((registerState as RegisterState.Success).user.userId)
                viewModel.resetState()
            }
            is RegisterState.Error -> {
                Toast.makeText(context, (registerState as RegisterState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // ✅ CORREÇÃO: Verificar activity e usar no CredentialManager
    LaunchedEffect(registerState) {
        if (registerState is RegisterState.AwaitingSocialAuth) {
            val request = (registerState as RegisterState.AwaitingSocialAuth).request

            // ✅ Verificar se temos uma Activity válida
            if (activity == null) {
                Toast.makeText(context, "Erro: Contexto inválido para registro social", Toast.LENGTH_LONG).show()
                viewModel.resetState()
                return@LaunchedEffect
            }

            try {
                println("🔍 Iniciando getCredential com Activity: ${activity.javaClass.simpleName}")

                // ✅ USAR ACTIVITY em vez de context
                val result = credentialManager.getCredential(
                    context = activity, // ✅ CORREÇÃO PRINCIPAL
                    request = request
                )

                println("✅ Credencial obtida: ${result.credential.type}")
                viewModel.handleGoogleSignInCredential(result.credential)

            } catch (e: GetCredentialException) {
                println("❌ GetCredentialException: ${e.type} - ${e.message}")
                e.printStackTrace()

                val errorMessage = when (e) {
                    is androidx.credentials.exceptions.GetCredentialCancellationException -> {
                        "Registro cancelado"
                    }
                    is androidx.credentials.exceptions.NoCredentialException -> {
                        "Nenhuma conta Google encontrada. Adicione uma conta nas configurações do dispositivo."
                    }
                    else -> {
                        "Falha no registro social: ${e.message}"
                    }
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                viewModel.resetState()

            } catch (e: CancellationException) {
                println("ℹ️ Registro cancelado pelo usuário")
                viewModel.resetState()

            } catch (e: Exception) {
                println("❌ Erro inesperado: ${e.message}")
                e.printStackTrace()
                Toast.makeText(context, "Erro inesperado: ${e.message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
    }
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            TopRegisterSection()
            Spacer(modifier = Modifier.height(36.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
            )
            {
                RegisterSection(
                    name = name,
                    onNameChange = { name = it },
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { confirmPassword = it },
                    onRegisterClick = {
                        if (password == confirmPassword) {
                            viewModel.signUpLocal(name, email, password)
                        } else {
                            showToast("As senhas não coincidem.")
                        }
                    },
                    isLoading = isEnabled
                )
                Spacer(modifier = Modifier.height(30.dp))

                DividerText()
                Spacer(modifier = Modifier.height(20.dp))
                SocialMediaRegisterSection(
                    onGoogleClick = { viewModel.getGoogleSignInRequest() },
                    enabled = !isEnabled
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            BackToLogin(onNavigateToLogin = onNavigateToLogin)
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
private fun TopRegisterSection() {
    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.40f),
            painter = painterResource(id = R.drawable.shape),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Row(
            modifier = Modifier.padding(top = 30.dp, bottom = 10.dp),
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
                .padding(bottom = 5.dp)
                .align(alignment = Alignment.BottomCenter),
            text = stringResource(id = R.string.register_txt),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
@Composable
private fun RegisterSection(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    isLoading: Boolean
){
    val isPasswordValid = password.isNotEmpty() && password == confirmPassword
    val isFormValid = name.isNotEmpty() && email.isNotEmpty() && isPasswordValid && !isLoading

    // 1. Nome Completo
    LoginTextField(
        value = name,
        onValueChange = onNameChange,
        label = "Nome Completo",
        keyboardType = KeyboardType.Text,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(15.dp))

    // 2. E-mail
    LoginTextField(
        value = email,
        onValueChange = onEmailChange,
        label = "E-mail",
        keyboardType = KeyboardType.Email,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(15.dp))

    // 3. Senha
    LoginTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = "Senha",
        isPassword = true,
        keyboardType = KeyboardType.Password,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(15.dp))

    // 4. Confirmar Senha
    LoginTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = "Confirmar a senha",
        isPassword = true,
        keyboardType = KeyboardType.Password,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(20.dp))

    // 5. Botão de Registro
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        onClick = onRegisterClick, // Ação do ViewModel
        enabled = isFormValid,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp)
    )
    {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = "Registrar",
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
private fun SocialMediaRegisterSection(
    onGoogleClick: () -> Unit,
    enabled: Boolean
){
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
private fun BackToLogin(onNavigateToLogin: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Já possue uma conta?",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color(0xFF3B3B3B),
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            modifier = Modifier
                .clickable { onNavigateToLogin() }
                .padding(vertical = 4.dp),
            text = "Faça login agora.",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Preview
@Composable
private fun RegisterPreview() {
    RegisterScreen(onRegisterSuccess = {}, onNavigateToLogin = {})
}
