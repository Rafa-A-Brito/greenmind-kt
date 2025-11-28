package com.github.rafaabrito.projectgreenmind.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.rafaabrito.projectgreenmind.data.repository.ScoreRepository
import com.github.rafaabrito.projectgreenmind.domain.entities.ScoreEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    // ✅ Flow ordenado por pontuação (do maior para o menor)
    val rankingUsers: StateFlow<List<ScoreEntity>> = scoreRepository.getAllScores()
        .map { scores ->
            // Ordenar por pontuação decrescente
            scores.sortedByDescending { it.totalScore }
        }
        .catch { e ->
            // ✅ Tratamento de erro
            e.printStackTrace()
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ✅ Função para adicionar pontuação de teste (opcional, para desenvolvimento)
    fun addMockScore(userId: Int, score: Int) {
        // Esta função pode ser usada para testes
        // Na produção, remova ou proteja com flag de debug
    }
}