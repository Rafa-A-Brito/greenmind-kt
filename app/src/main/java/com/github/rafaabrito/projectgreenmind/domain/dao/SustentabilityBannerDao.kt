package com.github.rafaabrito.projectgreenmind.domain.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.rafaabrito.projectgreenmind.domain.entities.SustentabilityBanner
import kotlinx.coroutines.flow.Flow

@Dao
interface SustentabilityBannerDao {

    // --- Inserção da info do dia ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: SustentabilityBanner)

    // --- Atualização conforme o tempo ---
    @Update
    suspend fun updateBanner(banner: SustentabilityBanner)

    // --- Remoção de banners para maior conexão à realidade ---
    @Delete
    suspend fun deleteBanner(banner: SustentabilityBanner)

    // --- Funções Adicionais Úteis (Leitura) ---
    @Query("SELECT * FROM sustentabilityBanner WHERE statusBanner = 1 LIMIT 1")
    fun getActiveBanner(): Flow<SustentabilityBanner?>

    @Query("SELECT * FROM sustentabilityBanner WHERE id = :bannerId")
    fun getBannerById(bannerId: Int): Flow<SustentabilityBanner?>
}
