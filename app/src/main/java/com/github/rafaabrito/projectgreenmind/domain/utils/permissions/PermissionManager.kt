package com.github.rafaabrito.projectgreenmind.domain.utils.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

// ✅ Data classes para estados de permissões
data class PermissionsState(
    val locationGranted: Boolean = false,
    val cameraGranted: Boolean = false,
    val allPermissionsGranted: Boolean = false
)

data class AllPermissionsManager(
    val permissionsState: PermissionsState,
    val requestAllPermissions: () -> Unit
)

data class LocationPermissionState(
    val hasPermission: Boolean,
    val requestPermission: () -> Unit
)

// ✅ Gerenciador de TODAS as permissões (chamado no início do app)
@Composable
fun rememberAllPermissionsState(
    onAllPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit
): AllPermissionsManager {
    val context = LocalContext.current

    var permissionsState by remember {
        mutableStateOf(checkAllPermissions(context))
    }

    val permissionsToRequest = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        add(Manifest.permission.CAMERA)

        // Background location apenas para Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val newState = checkAllPermissions(context)
        permissionsState = newState

        if (newState.allPermissionsGranted) {
            onAllPermissionsGranted()
        } else {
            onPermissionsDenied()
        }
    }

    return remember {
        AllPermissionsManager(
            permissionsState = permissionsState,
            requestAllPermissions = {
                launcher.launch(permissionsToRequest.toTypedArray())
            }
        )
    }
}

// ✅ Gerenciador de permissão de LOCALIZAÇÃO individual (para ProfileScreen)
@Composable
fun rememberLocationPermissionState(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
): LocationPermissionState {
    val context = LocalContext.current
    var permissionGranted by remember {
        mutableStateOf(hasLocationPermission(context))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        permissionGranted = fineLocationGranted || coarseLocationGranted

        if (permissionGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    return remember {
        LocationPermissionState(
            hasPermission = permissionGranted,
            requestPermission = {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )
    }
}

// ✅ Funções auxiliares privadas
private fun checkAllPermissions(context: Context): PermissionsState {
    val locationGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

    val cameraGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    val allGranted = locationGranted && cameraGranted

    return PermissionsState(
        locationGranted = locationGranted,
        cameraGranted = cameraGranted,
        allPermissionsGranted = allGranted
    )
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}