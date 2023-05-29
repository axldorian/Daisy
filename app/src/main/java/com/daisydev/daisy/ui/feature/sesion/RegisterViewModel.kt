package com.daisydev.daisy.ui.feature.sesion

import android.util.Log
import androidx.lifecycle.LiveData
import android.util.Patterns
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisydev.daisy.repository.remote.AppWriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val appWriteRepository: AppWriteRepository) :
    ViewModel() {

    private val _user = MutableLiveData<String>()
    val user: LiveData<String> = _user

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _ConditionsChecked = MutableLiveData<Boolean>()
    val conditionsChecked: LiveData<Boolean> = _ConditionsChecked

    private val _registerEnable = MutableLiveData<Boolean>()
    val registerEnable: LiveData<Boolean> = _registerEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    private val _showError = MutableLiveData<Boolean>()
    val showError: LiveData<Boolean> = _showError

    fun onRegisterChanged(
        user: String,
        email: String,
        password: String,
        conditionsChecked: Boolean
    ) {
        _user.value = user
        _email.value = email
        _password.value = password
        _ConditionsChecked.value = conditionsChecked
        _registerEnable.value =
            isValidUser(user) && isValidEmail(email) && isValidPassword(password) && conditionsChecked
    }

    private fun isValidUser(user: String): Boolean = user.length >= 3

    private fun isValidPassword(password: String): Boolean = password.length >= 8

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun onRegisterSelected() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Register user
                appWriteRepository.register(
                    password = password.value!!,
                    email = email.value!!,
                    name = user.value!!
                )

                // Login user
                val result = appWriteRepository.login(email.value!!, password.value!!)

                Log.d("LoginViewModel", "onLoginSelected success: ${result.id}")
                // TODO: 2021-10-13 save session
                _registerSuccess.value = true
            } catch (e: Exception) {
                _showError.value = true
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun showSnackbar(snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {
            snackbarHostState
                .showSnackbar("Error al crear cuenta, intente nuevamente")
            _showError.value = false
        }
    }
}