package com.github.rafaabrito.projectgreenmind.data.repository

import com.github.rafaabrito.projectgreenmind.data.model.User
import com.github.rafaabrito.projectgreenmind.ui.registration.RegistrationViewParams

interface UserRepository {

    fun createUser(registrationViewParams: RegistrationViewParams)

    fun getUser(id: Int): User

    fun login(email: String, password: String): Int

}