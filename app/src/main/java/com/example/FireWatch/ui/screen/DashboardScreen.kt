package com.globalSolution.FireWatch.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.globalSolution.FireWatch.R
import com.globalSolution.FireWatch.data.model.FocoIncendio
import com.globalSolution.FireWatch.data.model.Severidade
import com.globalSolution.FireWatch.ui.theme.AppColors
import com.globalSolution.FireWatch.viewModel.FireViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale
import kotlin.math.*

fun calcularDistanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    return try {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
        vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth * 2, vectorDrawable.intrinsicHeight * 2)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth * 2, vectorDrawable.intrinsicHeight * 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        BitmapDescriptorFactory.fromBitmap(bitmap)
    } catch (e: Exception) { null }
}

@Composable
fun DashboardScreen(
    onNavigateToProfile: () -> Unit,
    viewModel: FireViewModel = viewModel()
) {
    val context = LocalContext.current
    var personIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var focoSelecionado by remember { mutableStateOf<FocoIncendio?>(null) }

    LaunchedEffect(Unit) { personIcon = bitmapDescriptorFromVector(context, R.drawable.ic_person) }

    val minhaLocalizacao by viewModel.minhaLocalizacao
    val fireHotspots by viewModel.fireHotspots


    var inputLat by remember(minhaLocalizacao) { mutableStateOf(minhaLocalizacao.latitude.toString()) }
    var inputLon by remember(minhaLocalizacao) { mutableStateOf(minhaLocalizacao.longitude.toString()) }

    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(minhaLocalizacao, 5.5f) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {


        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Monitoramento Global", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.TextDark)
            Surface(shape = CircleShape, color = AppColors.Primary, modifier = Modifier.size(40.dp).clickable { onNavigateToProfile() }) {
                Box(contentAlignment = Alignment.Center) { Text("JS", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF8F9FA),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Minha Localização (Lat / Lon)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = inputLat,
                        onValueChange = { inputLat = it },
                        label = { Text("Latitude", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = inputLon,
                        onValueChange = { inputLon = it },
                        label = { Text("Longitude", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            val lat = inputLat.toDoubleOrNull()
                            val lon = inputLon.toDoubleOrNull()
                            if (lat != null && lon != null) {
                                viewModel.atualizarLocalizacao(lat, lon)
                                // Move a câmera para a nova localização
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(minhaLocalizacao, 5.5f)
                            }
                        },
                        modifier = Modifier
                            .background(AppColors.Primary, RoundedCornerShape(8.dp))
                            .size(56.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Atualizar Localização", tint = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(16.dp)).border(1.dp, AppColors.Border, RoundedCornerShape(16.dp))) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { focoSelecionado = null }
            ) {

                Marker(
                    state = MarkerState(position = minhaLocalizacao),
                    title = "Minha Propriedade",
                    icon = personIcon ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                    zIndex = 2f
                )


                fireHotspots.forEach { foco ->
                    val corPino = when (foco.severidade) {
                        Severidade.BAIXO -> BitmapDescriptorFactory.HUE_YELLOW
                        Severidade.MEDIO -> BitmapDescriptorFactory.HUE_ORANGE
                        Severidade.CRITICO -> BitmapDescriptorFactory.HUE_RED
                    }

                    val isSelected = focoSelecionado == foco
                    val markerAlpha = if (focoSelecionado == null || isSelected) 1.0f else 0.4f
                    val zIndex = if (isSelected) 1f else 0f

                    Marker(
                        state = MarkerState(position = foco.localizacao),
                        title = "Foco: ${foco.severidade}",
                        icon = BitmapDescriptorFactory.defaultMarker(corPino),
                        alpha = markerAlpha,
                        zIndex = zIndex,
                        onClick = { focoSelecionado = foco; true }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        if (focoSelecionado != null) {
            val dist = calcularDistanciaKm(minhaLocalizacao.latitude, minhaLocalizacao.longitude, focoSelecionado!!.localizacao.latitude, focoSelecionado!!.localizacao.longitude)
            val corDestaque = when(focoSelecionado!!.severidade) {
                Severidade.CRITICO -> Color(0xFFC0392B)
                Severidade.MEDIO -> Color(0xFFF39C12)
                else -> Color(0xFF27AE60)
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = BorderStroke(2.dp, corDestaque)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, null, tint = corDestaque)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Análise de Risco", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Distância: ${String.format(Locale.US, "%.1f", dist)} km", fontSize = 14.sp)
                    Text("Severidade: ${focoSelecionado!!.severidade}", color = corDestaque, fontWeight = FontWeight.ExtraBold)
                }
            }
        }


        Button(
            onClick = { /* Lógica Bombeiros */ },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Call, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ligar para Bombeiros (193)", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}