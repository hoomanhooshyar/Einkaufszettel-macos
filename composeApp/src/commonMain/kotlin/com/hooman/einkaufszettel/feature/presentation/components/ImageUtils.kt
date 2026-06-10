package com.hooman.einkaufszettel.feature.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.local_banana
import einkaufszettel.composeapp.generated.resources.local_bread
import einkaufszettel.composeapp.generated.resources.local_cheese
import einkaufszettel.composeapp.generated.resources.local_hen
import einkaufszettel.composeapp.generated.resources.local_milk
import einkaufszettel.composeapp.generated.resources.local_orange
import einkaufszettel.composeapp.generated.resources.local_paprika
import einkaufszettel.composeapp.generated.resources.local_strawberry
import einkaufszettel.composeapp.generated.resources.noimage
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    if(imageUrl.startsWith("http")) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier
                .graphicsLayer(
                    scaleX = 1.5f,
                    scaleY = 1.5f
                ),
            contentScale = contentScale
        )
    }else{
        val painter = getLocalPainter(imageUrl)
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier
                .graphicsLayer(
                    scaleX = 1.5f,
                    scaleY = 1.5f
                ),
            contentScale = contentScale
        )

    }
}

@Composable
fun getLocalPainter(imageString: String) = when(imageString){
    "local_banana" -> painterResource(Res.drawable.local_banana)
    "local_bread" -> painterResource(Res.drawable.local_bread)
    "local_cheese" -> painterResource(Res.drawable.local_cheese)
    "local_hen" -> painterResource(Res.drawable.local_hen)
    "local_milk" -> painterResource(Res.drawable.local_milk)
    "local_orange" -> painterResource(Res.drawable.local_orange)
    "local_paprika" -> painterResource(Res.drawable.local_paprika)
    "local_strawberry" -> painterResource(Res.drawable.local_strawberry)
    else -> painterResource(Res.drawable.noimage)
}