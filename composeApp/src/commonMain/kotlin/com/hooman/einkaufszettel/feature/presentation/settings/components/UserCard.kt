package com.hooman.einkaufszettel.feature.presentation.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.size.Scale
import com.hooman.einkaufszettel.core.presentation.AppDimens
import com.hooman.einkaufszettel.core.presentation.orangeGradient
import com.hooman.einkaufszettel.core.presentation.redColor
import com.hooman.einkaufszettel.core.presentation.redGradient
import com.hooman.einkaufszettel.core.presentation.whiteColor
import com.hooman.einkaufszettel.domain.model.User
import com.hooman.einkaufszettel.feature.presentation.components.CEButton
import einkaufszettel.composeapp.generated.resources.Res
import einkaufszettel.composeapp.generated.resources.logout
import einkaufszettel.composeapp.generated.resources.logout_24px
import einkaufszettel.composeapp.generated.resources.name
import einkaufszettel.composeapp.generated.resources.unknown_user
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun UserCard(
    modifier: Modifier = Modifier,
    user: User,
    onButtonClick: () -> Unit,
    icon: Any?,
    buttonText: String,
    buttonBackground: Brush
) {
    Card(
        modifier = modifier
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .background(brush = orangeGradient, shape = RoundedCornerShape(16.dp))
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(width = 3.dp, color = whiteColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ){
        val userImage = user.imageUrl
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = orangeGradient),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppDimens.spacingMedium, horizontal = AppDimens.spacingSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                if(userImage != null && userImage != "" && userImage.startsWith("http")){
                    AsyncImage(
                        modifier = Modifier.clip(CircleShape),
                        model = user.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }else{
                    Image(
                        modifier = Modifier.clip(CircleShape),
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    val username = user.name ?: stringResource(Res.string.unknown_user)
                    Text(
                        text = stringResource(Res.string.name) + ": $username",
                        color = whiteColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }


            CEButton(
                icon = icon,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimens.spacingMedium),
                onClick = {
                    onButtonClick()
                },
                text = buttonText,
                contentColor = whiteColor,
                containerColor = buttonBackground
            )
        }

    }
}