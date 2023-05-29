package com.daisydev.daisy.ui.feature.sesion

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daisydev.daisy.repository.remote.AppWriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.appwrite.models.User
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SesionViewModel @Inject constructor(private val appWriteRepository: AppWriteRepository) :
    ViewModel() {

    private val _userData = MutableLiveData<User<Map<String, Any>>>()
    val userData: LiveData<User<Map<String, Any>>> = _userData

    private val _isUserLogged = MutableLiveData<Boolean>()
    val isUserLogged: LiveData<Boolean> = _isUserLogged

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun isLogged() {
        viewModelScope.launch {
            try {
                val result = appWriteRepository.isLoggedIn()
                Log.d("SesionViewModel", "isLogged: ${result.id}")
                _userData.value = appWriteRepository.getAccount()
                _isUserLogged.value = true
            } catch (e: Exception) {
                _isUserLogged.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun closeSession() {
        viewModelScope.launch {
            try {
                appWriteRepository.logout()
                _isUserLogged.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}