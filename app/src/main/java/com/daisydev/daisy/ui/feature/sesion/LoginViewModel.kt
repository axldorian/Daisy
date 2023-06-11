package com.daisydev.daisy.ui.feature.sesion

import android.util.Patterns
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisydev.daisy.models.Session
import com.daisydev.daisy.repository.local.SessionDataStore
import com.daisydev.daisy.repository.remote.AppWriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de login
 * Contiene la logica de negocio de la pantalla
 * @property appWriteRepository AppWriteRepository
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appWriteRepository: AppWriteRepository,
    private val sessionDataStore: SessionDataStore
) :
    ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _loginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _showError = MutableLiveData<Boolean>()
    val showError: LiveData<Boolean> = _showError

    // Actualiza el valor de email y password y habilita el boton de login si son validos
    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPassword(password)
    }

    // Valida que la contraseña tenga al menos 8 caracteres
    private fun isValidPassword(password: String): Boolean = password.length >= 8

    // Valida que el email tenga el formato correcto
    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Realiza el login
    fun onLoginSelected() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Login
                val result = appWriteRepository.login(email.value!!, password.value!!)

                // Get user data
                val user = appWriteRepository.getAccount()

                // Save session
                sessionDataStore.saveSession(
                    Session(
                        id = user.id,
                        name = user.name,
                        email = user.email
                    )
                )

                _loginSuccess.value = true
            } catch (e: Exception) {
                _showError.value = true
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Muestra el snackbar de error
    fun showSnackbar(snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {
            snackbarHostState
                .showSnackbar("Credenciales incorrectas, intente nuevamente")
            _showError.value = false
        }
    }
}