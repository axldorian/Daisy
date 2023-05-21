package com.daisydev.daisy.repository.remote

import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.Session
import io.appwrite.models.User
import io.appwrite.services.Account
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppWriteRepository @Inject constructor(
    private val client: Client,
    private val dispatcher: CoroutineDispatcher
) {
    private val account = Account(client)

    suspend fun login(email: String, password: String): Session {
        return withContext(dispatcher) {
            account.createEmailSession(email, password)
        }
    }

    suspend fun register(
        email: String, password: String,
        name: String
    ): User<Map<String, Any>> {
        return withContext(dispatcher) {
            account.create(
                userId = ID.unique(),
                email = email,
                password = password,
                name = name
            )
        }
    }

    suspend fun logout() {
        return withContext(dispatcher) {
            account.deleteSession("current")
        }
    }

    suspend fun getAccount(): User<Map<String, Any>> {
        return withContext(dispatcher) {
            account.get()
        }
    }

    suspend fun isLoggedIn(): Session {
        return withContext(dispatcher) {
            account.getSession("current")
        }
    }
}