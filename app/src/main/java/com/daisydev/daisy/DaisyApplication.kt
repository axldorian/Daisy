package com.daisydev.daisy

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// HiltAndroidApp es una anotación que se usa para generar un componente de aplicación de Hilt
// esta clase se usa para inicializar Hilt
@HiltAndroidApp
class DaisyApplication: Application()