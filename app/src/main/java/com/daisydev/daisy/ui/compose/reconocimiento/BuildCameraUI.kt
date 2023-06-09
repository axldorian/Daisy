package com.daisydev.daisy.ui.compose.reconocimiento

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.daisydev.daisy.R
import com.daisydev.daisy.ui.feature.reconocimiento.ReconocimientoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

@Composable
fun BuildCameraUI(
    navController: NavController,
    context: Context,
    viewModel: ReconocimientoViewModel,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    Box(Modifier.fillMaxSize()) {
        val executor = remember(context) { ContextCompat.getMainExecutor(context) }
        val imageCapture: MutableState<ImageCapture?> = remember { mutableStateOf(null) }

        // Camera preview
        CameraView(
            modifier = Modifier.fillMaxSize(),
            imageCapture = imageCapture,
            executor = executor,
            context = context
        )

        // Button for return to last screen
        Box(Modifier.padding(10.dp)) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painterResource(id = R.drawable.ic_back),
                    contentDescription = "Regresar a pantalla anterior",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                )
            }
        }

        // Button for take the photo and process it
        ExtendedFloatingActionButton(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 20.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onClick = {
                imageCapture.value?.takePicture(
                    executor,
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            /* TODO: implementar función de procesado de imagen */
                            Log.d("CameraUI", image.toString())
                        }

                        override fun onError(exception: ImageCaptureException) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = exception.message ?: "Error al tomar la foto",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    })
            }) {
            Icon(
                painterResource(id = R.drawable.ic_search),
                contentDescription = "Reconocimiento",
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp), text = "Escanear"
            )
        }
    }
}

@Composable
private fun CameraView(
    modifier: Modifier,
    imageCapture: MutableState<ImageCapture?>,
    executor: Executor,
    context: Context
) {
    val previewCameraView = remember { PreviewView(context) }
    val cameraProviderFuture = remember(context) { ProcessCameraProvider.getInstance(context) }
    val cameraProvider = remember(cameraProviderFuture) { cameraProviderFuture.get() }
    var cameraSelector: CameraSelector? by remember { mutableStateOf(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(modifier = modifier, factory = {
        cameraProviderFuture.addListener(
            {
                cameraSelector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                imageCapture.value = ImageCapture.Builder().build()

                cameraProvider.unbindAll()

                val prev = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewCameraView.surfaceProvider)
                }

                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector as CameraSelector, imageCapture.value, prev
                )
            }, executor
        )
        previewCameraView
    })
}