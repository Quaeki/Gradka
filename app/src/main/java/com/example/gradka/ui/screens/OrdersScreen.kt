package com.example.gradka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.gradka.data.ORDERS
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

@Composable
fun OrdersScreen(onBack: () -> Unit, onTracking: () -> Unit) {
    val colors = LocalAppColors.current

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(colors.bg).statusBarsPadding(),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 14.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, onClick = onBack,
                        ),
                    contentAlignment = Alignment.Center,
                ) { BackIcon(tint = colors.ink) }
                Text(
                    text = "Мои заказы",
                    style = TextStyle(
                        fontFamily = FrauncesFontFamily, fontSize = 22.sp,
                        fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.ink,
                    ),
                )
            }
        }

        items(ORDERS) { order ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.line, RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { if (order.status == "В пути") onTracking() }
                    .padding(16.dp),
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column {
                            Text(text = "${order.date} · ${order.number}", style = TextStyle(fontSize = 13.sp, color = colors.ink3))
                            Text(text = "${order.items} товаров", style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.ink), modifier = Modifier.padding(top = 4.dp))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (order.status == "В пути") colors.accentSoft else colors.surface2,
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            ) {
                                Text(
                                    text = order.status,
                                    style = TextStyle(
                                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                        color = if (order.status == "В пути") colors.accentDeep else colors.ink3,
                                    ),
                                )
                            }
                            Text(
                                text = "${order.total} ₽",
                                style = TextStyle(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.ink,
                                ),
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                    }
                    if (order.status == "В пути") {
                        Row(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.surface2)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            DeliveryIcon(tint = colors.accentDeep)
                            Text(
                                text = "Прибудет через 31 мин",
                                style = TextStyle(fontSize = 13.sp, color = colors.ink2),
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(28.dp)) }
    }
}