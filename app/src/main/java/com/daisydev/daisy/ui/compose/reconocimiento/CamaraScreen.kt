@file:OptIn(ExperimentalPermissionsApi::class)

package com.daisydev.daisy.ui.compose.reconocimiento

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.daisydev.daisy.R
import com.daisydev.daisy.ui.feature.reconocimiento.ReconocimientoViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope

@Composable
fun CamaraScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: ReconocimientoViewModel = hiltViewModel()
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(
        Modifier
            .fillMaxSize()
    ) {
        PermissionRequired(
            context,
            navController,
            cameraPermissionState,
            context.packageName,
            Modifier.align(Alignment.Center),
            viewModel,
            snackbarHostState,
            scope
        )
    }
}

@Composable
private fun PermissionRequired(
    context: Context,
    navController: NavController,
    cameraPermissionState: PermissionState,
    packageName: String,
    modifier: Modifier,
    viewModel: ReconocimientoViewModel,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val openAppSettingsLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) {}

    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            Column(
                modifier = modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_daisy_no_bg),
                    contentDescription = "Header Image",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(70.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = stringResource(R.string.camera_permission_info_0),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Button(onClick = {
                        cameraPermissionState.launchPermissionRequest()
                    }) {
                        Text(stringResource(R.string.camera_permission_grantbutton_0))
                    }
                }
            }
        },
        permissionNotAvailableContent = {
            Column(
                modifier = modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_daisy_no_bg),
                    contentDescription = "Header Image",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(70.dp)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = stringResource(R.string.camera_permission_info_1),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", packageName, null)
                        openAppSettingsLauncher.launch(intent)
                    }) {
                        Text(stringResource(R.string.camera_permission_grantbutton_1))
                    }
                }
            }
        }
    ) {
        BuildCameraUI(
            navController = navController,
            context = context,
            viewModel = viewModel,
            snackbarHostState = snackbarHostState,
            scope = scope
        )
    }
}