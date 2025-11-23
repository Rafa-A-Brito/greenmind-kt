package com.github.rafaabrito.projectgreenmind.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.domain.utils.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ✅ Data class para representar uma mensagem da comunidade
data class CommunityMessage(
    val id: String = "",
    val userName: String = "",
    val userPhotoUrl: String? = null,
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    // ✅ Lista de mensagens
    private val _messages = MutableStateFlow<List<CommunityMessage>>(emptyList())
    val messages: StateFlow<List<CommunityMessage>> = _messages.asStateFlow()

    // ✅ Texto que o usuário está digitando
    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    // ✅ Estado de carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ✅ Dados do usuário atual
    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName: StateFlow<String?> = _currentUserName.asStateFlow()

    private val _currentUserPhoto = MutableStateFlow<String?>(null)
    val currentUserPhoto: StateFlow<String?> = _currentUserPhoto.asStateFlow()

    init {
        loadCurrentUser()
        loadInitialMessages()
    }

    // ✅ Carregar dados do usuário logado
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val authDetails = authService.getCurrentUserProfileDetails()
                if (authDetails != null) {
                    _currentUserName.value = authDetails.name ?: "Usuário"
                    _currentUserPhoto.value = authDetails.profilePictureUrl
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ✅ Carregar mensagens iniciais (mock - em produção viria do Firestore)
    private fun loadInitialMessages() {
        val initialMessages = listOf(
            CommunityMessage(
                id = "1",
                userName = "Mauricio Henrique",
                message = "Você viu que a associação de moradores vai começar aquele projeto de hortas comunitárias? Achei uma ideia excelente! Além de ajudar na alimentação, é uma forma de incentivar a sustentabilidade aqui no bairro."
            ),
            CommunityMessage(
                id = "2",
                userName = "Lorena Alves",
                message = "Sim, eu vi! Fiquei super empolgada. Acho importante a gente pensar mais no meio ambiente e em pequenas atitudes podem fazer diferença. Cultivar alimentos sem agrotóxicos, reutilizar materiais, fazer compostagem... tudo isso ajuda muito."
            )
        )
        _messages.value = initialMessages
    }

    // ✅ Atualizar texto digitado
    fun updateMessageText(text: String) {
        _messageText.value = text
    }

    // ✅ Enviar mensagem
    fun sendMessage() {
        val text = _messageText.value.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            try {
                _isLoading.value = true

                // ✅ Criar nova mensagem
                val newMessage = CommunityMessage(
                    id = System.currentTimeMillis().toString(),
                    userName = _currentUserName.value ?: "Usuário",
                    userPhotoUrl = _currentUserPhoto.value,
                    message = text,
                    timestamp = System.currentTimeMillis()
                )

                // ✅ Adicionar à lista
                _messages.value = _messages.value + newMessage

                // ✅ Limpar campo de texto
                _messageText.value = ""

                // TODO: Em produção, salvar no Firestore
                // firestore.collection("community_messages").add(newMessage)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ Deletar mensagem (opcional)
    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            try {
                _messages.value = _messages.value.filter { it.id != messageId }

                // TODO: Em produção, deletar do Firestore
                // firestore.collection("community_messages").document(messageId).delete()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ✅ Função para adicionar imagem (placeholder para futura implementação)
    fun attachImage() {
        // TODO: Implementar seleção de imagem
    }

    // ✅ Função para tirar foto (placeholder para futura implementação)
    fun takePhoto() {
        // TODO: Implementar câmera
    }
}