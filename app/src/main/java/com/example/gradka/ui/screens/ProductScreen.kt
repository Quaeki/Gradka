package com.example.gradka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.gradka.AppViewModel
import com.example.gradka.domain.PRODUCTS
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

@Composable
fun ProductScreen(
    productId: String,
    vm: AppViewModel,
    onBack: () -> Unit,
    onGoCart: () -> Unit,
) {
    val colors = LocalAppColors.current
    val p = PRODUCTS.find { it.id == productId } ?: return
    val qty = vm.cart[p.id] ?: 0
    val isFav = vm.favs.contains(p.id)
    val heroBg = hueColor(p.hue, 0.28f, 0.93f)

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Hero image area
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .background(heroBg),
                    contentAlignment = Alignment.Center,
                ) {
                    ProductPlaceholder(hue = p.hue, size = 220.dp, label = p.cat)
                    // pagination dots
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        listOf(0, 1, 2).forEach { i ->
                            Box(
                                modifier = Modifier
                                    .width(if (i == 0) 24.dp else 6.dp)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (i == 0) colors.ink else colors.ink.copy(alpha = 0.2f)),
                            )
                        }
                    }
                }
            }

            // Content
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 22.dp, bottom = 10.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        LeafIcon(tint = colors.accentDeep, size = 16.dp)
                        Text(
                            text = p.farm,
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, color = colors.accentDeep),
                        )
                    }
                    Text(
                        text = p.name,
                        style = TextStyle(
                            fontFamily = FrauncesFontFamily,
                            fontSize = 28.sp, fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.02).em, lineHeight = (28 * 1.15).sp,
                            color = colors.ink,
                        ),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Text(
                        text = p.subtitle,
                        style = TextStyle(fontSize = 14.sp, color = colors.ink2),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(top = 18.dp),
                    ) {
                        Text(
                            text = "${p.price} ₽",
                            style = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 30.sp, fontWeight = FontWeight.SemiBold, color = colors.ink,
                            ),
                        )
                        Text(
                            text = "/ ${p.unit}",
                            style = TextStyle(fontSize = 13.sp, color = colors.ink3),
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }

                    // Meta grid
                    Row(
                        modifier = Modifier
                            .padding(top = 22.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.surface2),
                    ) {
                        listOf("Свежесть" to "Сегодня", "Страна" to "Россия", "Хранить" to "+4°C").forEach { (label, value) ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 12.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = label.uppercase(),
                                    style = TextStyle(
                                        fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.06.em, color = colors.ink3,
                                    ),
                                )
                                Text(
                                    text = value,
                                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.ink),
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                        }
                    }

                    Text(
                        text = "Описание",
                        style = TextStyle(
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.08.em, color = colors.ink3,
                        ),
                        modifier = Modifier.padding(top = 24.dp),
                    )
                    Text(
                        text = "Собрано сегодня утром. Без ГМО и химических удобрений. Хранится до 5 дней при температуре от 0 до +6°C.",
                        style = TextStyle(fontSize = 14.sp, color = colors.ink2, lineHeight = (14 * 1.55).sp),
                        modifier = Modifier.padding(top = 8.dp),
                    )

                    // Subscribe row
                    Row(
                        modifier = Modifier
                            .padding(top = 22.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(width = 1.dp, color = colors.line2, shape = RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RepeatIcon(tint = colors.accentDeep)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Подписаться на регулярную доставку",
                                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.ink),
                            )
                            Text(
                                text = "Еженедельно · скидка 10%",
                                style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(22.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(colors.surface3)
                                .padding(2.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(colors.surface),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }

        // Transparent top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClick = onBack,
                    ),
                contentAlignment = Alignment.Center,
            ) { BackIcon(tint = colors.ink) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { vm.toggleFav(p.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    HeartIcon(
                        tint = if (isFav) colors.danger else colors.ink,
                        filled = isFav, size = 22.dp,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center,
                ) { MoreIcon(tint = colors.ink) }
            }
        }

        // Bottom action bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(colors.bg)
                .border(width = 1.dp, color = colors.line, shape = RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .navigationBarsPadding(),
        ) {
            if (qty == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.ink)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { vm.addToCart(p.id) },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        PlusIcon(tint = colors.bg, size = 18.dp)
                        Text(
                            text = "В корзину · ${p.price} ₽",
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.bg),
                        )
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.ink)
                            .padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { vm.subFromCart(p.id) },
                            contentAlignment = Alignment.Center,
                        ) { MinusIcon(tint = colors.bg, size = 20.dp) }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "В корзине",
                                style = TextStyle(fontSize = 11.sp, color = colors.bg.copy(alpha = 0.6f)),
                            )
                            Text(
                                text = "$qty · ${qty * p.price} ₽",
                                style = TextStyle(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.bg,
                                ),
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { vm.addToCart(p.id) },
                            contentAlignment = Alignment.Center,
                        ) { PlusIcon(tint = colors.bg, size = 20.dp) }
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.accent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null, onClick = onGoCart,
                            ),
                        contentAlignment = Alignment.Center,
                    ) { BagIcon(tint = colors.accentInk) }
                }
            }
        }
    }
}