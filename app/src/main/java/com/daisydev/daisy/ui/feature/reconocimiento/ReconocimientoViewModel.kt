package com.daisydev.daisy.ui.feature.reconocimiento

import androidx.lifecycle.ViewModel
import com.daisydev.daisy.repository.remote.AppWriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReconocimientoViewModel
@Inject constructor(private val appWriteRepository: AppWriteRepository) : ViewModel() {

}