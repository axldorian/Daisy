package com.daisydev.daisy.repository.remote

import com.daisydev.daisy.models.BlogEntry
import com.daisydev.daisy.models.toBlogEntry
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.Session
import io.appwrite.models.User
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Functions
import io.appwrite.services.Storage
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
    private val databases = Databases(client)
    private val storage = Storage(client)
    private val functions = Functions(client)

    // -- Account --

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

    // -- Storage --



    // -- Functions --



    // -- Databases --

    suspend fun listDocuments(): List<BlogEntry> {
        return withContext(dispatcher) {
            databases.listDocuments(
                "64668e42ab469f0dcf8d",
                "647a1828df7b78f0dfb1"
            ).documents.map {
                toBlogEntry(it)
            }
        }
    }
}