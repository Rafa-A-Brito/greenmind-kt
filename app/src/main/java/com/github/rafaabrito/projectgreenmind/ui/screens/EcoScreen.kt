package com.github.rafaabrito.projectgreenmind.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.github.rafaabrito.projectgreenmind.R

@Composable
fun EcoScreen(){

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

        }
        // Título Screen Localização
        Row {
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = null
            )
            Text(
                "Localização e Reciclagem"
            )
        }

        // Demonstração da API do Mapa (OpenStreetMap)
        Box{

        }


    }
}

@Composable
private fun LocalSection(){
    // Seção dos Ecopontos(nome, distância, tipos de reciclagem)

    Column {
        Box{

        }
        Box{

        }
        Box{

        }

    }
}