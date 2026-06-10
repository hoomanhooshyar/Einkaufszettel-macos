package com.hooman.einkaufszettel.feature.presentation.add_product.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.feature.presentation.components.ProductImage
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.select_icon
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageGridPicker(
    productIcons: List<String>,
    selectedIcon: String?,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    pickerSize: Dp = 120.dp,

) {
    var showDialog by remember { mutableStateOf(false) }
    val defaultIconBoxSize: Dp = 60.dp
    val cornerRadius:Dp = 12.dp
    val dialogHeight:Dp = 350.dp
    val textPadding:Dp = 16.dp
    val elevationSize:Dp = 8.dp
    val gridSize:Dp = 64.dp
    val gridSpace:Dp = 8.dp
    val boxGridSize:Dp = 64.dp
    val gridBoxRadius:Dp = 8.dp
    Box(
        modifier = modifier
            .size(pickerSize)
            .clickable{showDialog = true}
            .background(Color.Transparent),
    ){
        if(selectedIcon != null){
            ProductImage(
                imageUrl = selectedIcon,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }else{
            Box(
                modifier = Modifier
                    .size(defaultIconBoxSize)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "select image",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }

    }

    if(showDialog){
        Dialog(
            onDismissRequest = {showDialog = false}
        ){
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dialogHeight),
                shape = RoundedCornerShape(cornerRadius),
                color = MaterialTheme.colorScheme.background,
                shadowElevation = elevationSize
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(textPadding)
                ){
                    Text(
                        text = stringResource(Res.string.select_icon),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = textPadding)
                    )

                    //Photo Grid
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = gridSize),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(gridSpace),
                        verticalArrangement = Arrangement.spacedBy(gridSpace)
                    ){
                        items(productIcons.size){ index ->
                            Box(
                                modifier = Modifier
                                    .size(boxGridSize)
                                    .clickable{
                                        onIconSelected(productIcons[index])
                                        showDialog = false
                                    }
                                    .clip(RoundedCornerShape(gridBoxRadius))
                                    .background(whiteColor),

                            ){
                                ProductImage(
                                    imageUrl = productIcons[index],
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}