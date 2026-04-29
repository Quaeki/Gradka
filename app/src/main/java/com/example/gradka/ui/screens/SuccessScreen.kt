package com.example.gradka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

@Composable
fun SuccessScreen(onTracking: () -> Unit, onHome: () -> Unit) {
    val colors = LocalAppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 28.dp, vertical = 40.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(colors.accentSoft),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(colors.accent),
                contentAlignment = Alignment.Center,
            ) {
                CheckIcon(tint = colors.accentInk, size = 28.dp)
            }
        }

        Text(
            text = "Заказ принят!",
            style = TextStyle(
                fontFamily = FrauncesFontFamily,
                fontSize = 30.sp, fontWeight = FontWeight.Medium,
                letterSpacing = (-0.025).em, lineHeight = (30 * 1.05).sp,
                color = colors.ink,
            ),
            modifier = Modifier.padding(top = 24.dp),
        )

        Text(
            text = "Курьер соберёт его прямо сейчас. Ждём вас через 40 минут.",
            style = TextStyle(
                fontSize = 15.sp, color = colors.ink2, lineHeight = (15 * 1.55).sp,
            ),
            modifier = Modifier
                .padding(top = 12.dp)
                .widthIn(max = 260.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        Row(
            modifier = Modifier
                .padding(top = 28.dp)
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.ink)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, onClick = onTracking,
                ),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DeliveryIcon(tint = colors.bg)
            Text(
                text = "Следить за заказом",
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.bg),
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.bg)
                .border(1.dp, colors.line, RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, onClick = onHome,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "На главную", style = TextStyle(fontSize = 14.sp, color = colors.ink))
        }
    }
}