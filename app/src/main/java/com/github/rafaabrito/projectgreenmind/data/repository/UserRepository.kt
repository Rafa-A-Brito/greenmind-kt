import com.github.rafaabrito.projectgreenmind.domain.dao.UserDao
import com.github.rafaabrito.projectgreenmind.domain.dao.CredentialsDao
import com.github.rafaabrito.projectgreenmind.domain.entities.UserEntity
import com.github.rafaabrito.projectgreenmind.domain.entities.CredentialsEntity // Import necessário para a orquestração
import com.github.rafaabrito.projectgreenmind.domain.utils.PasswordHasher 
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first // Import necessário para obter valor do Flow

class UserRepository(
    private val userDao: UserDao, 
    private val credentialsDao: CredentialsDao, // DAO de Credenciais injetado
    private val passwordHasher: PasswordHasher
) {

    // Função para guardar as informações do User
    suspend fun saveUser(user: UserEntity){
        userDao.saveUser(user)
    }

    // Função para obter usuário por ID (retorna Flow)
    fun getUserById(id: Int): Flow<UserEntity?> {
        return userDao.getUser(id)
    }


    suspend fun createNewUser(name: String, email: String, password: String): Boolean {
        if (userDao.getUserByEmail(email) != null) {
            return false // Email já cadastrado
        }
        
        val hashedPassword = passwordHasher.hashPassword(password)

        val newUser = UserEntity(
            name = name,
            email = email,
            hashPassword = hashedPassword,
            firebaseUid = "ROOM_LOCAL_${System.currentTimeMillis()}" 
        )
        
        userDao.saveUser(newUser)
        return true
    }

    suspend fun login(email: String, password: String): UserEntity? {
        val user = userDao.getUserByEmail(email)

        if (user != null && user.hashPassword != null) {
            
            val isPasswordCorrect = passwordHasher.verifyPassword(password, user.hashPassword)
            
            if (isPasswordCorrect) {
                return user 
            }
        }
        return null 
    }

    suspend fun associateFirebaseUser(
        name: String?, // name deve ser nullable se vier do Firebase ou for opcional
        email: String, 
        authId: String // O UID do Firebase
    ): UserEntity { 
        
        val existingCredential = credentialsDao.getCredentialByAuthId(authId)

        if (existingCredential != null) {
            // Se a Credencial existe, o usuário local existe. Retorna o UserEntity ligado.
            return userDao.getUser(existingCredential.userId).first()!! 
        }
        
        // Cria e salva o UserEntity 
        val newUser = UserEntity(
            name = name, 
            email = email, 
            hashPassword = null, 
            firebaseUid = authId // Usa o authId como identificador
        )
        userDao.saveUser(newUser)

        val userWithId = userDao.getUserByFirebaseUid(authId)!! 

        val newCredential = CredentialsEntity(
            userId = userWithId.userId,
            authId = authId
        )
        credentialsDao.saveCredential(newCredential)

        return userWithId
    }
}