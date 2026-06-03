package com.globalSolution.FireWatch.viewModel


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globalSolution.FireWatch.data.model.FocoIncendio
import com.globalSolution.FireWatch.data.service.FireService

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class FireViewModel : ViewModel() {

    private val fireService = FireService()

    // 1. Estados que a Tela (View) vai "observar"
    private val _fireHotspots = mutableStateOf<List<FocoIncendio>>(emptyList())
    val fireHotspots: State<List<FocoIncendio>> = _fireHotspots

    private val _isLoadingApi = mutableStateOf(true)
    val isLoadingApi: State<Boolean> = _isLoadingApi

    private val _minhaLocalizacao = mutableStateOf(LatLng(-15.7939, -47.8828))
    val minhaLocalizacao: State<LatLng> = _minhaLocalizacao

    init {
        buscarFocosDoInpe()
    }

    private fun buscarFocosDoInpe() {

        viewModelScope.launch {
            _isLoadingApi.value = true
            _fireHotspots.value = fireService.getFocosDeIncendioDoInpe()
            _isLoadingApi.value = false
        }
    }

    fun atualizarLocalizacao(novaLat: Double, novaLon: Double) {
        _minhaLocalizacao.value = LatLng(novaLat, novaLon)
    }

    fun atualizarLocalizacao(novaCoordenada: LatLng) {
        _minhaLocalizacao.value = novaCoordenada
    }
}