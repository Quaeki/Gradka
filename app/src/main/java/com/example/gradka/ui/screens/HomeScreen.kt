package com.example.gradka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.gradka.AppViewModel
import com.example.gradka.data.PRODUCTS
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

@Composable
fun HomeScreen(
    vm: AppViewModel,
    onOpenProduct: (String) -> Unit,
    onNavigate: (String) -> Unit,
) {
    val colors = LocalAppColors.current
    val addresses by vm.addresses.collectAsState()
    val primaryAddress = addresses.firstOrNull { it.primary } ?: addresses.firstOrNull()
    val hero = PRODUCTS[4]
    val gridItems = PRODUCTS.drop(5).take(4)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg),
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 14.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Logo(size = 22.dp, colors = colors)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.line, RoundedCornerShape(19.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { onNavigate("search") },
                        contentAlignment = Alignment.Center,
                    ) { SearchIcon(tint = colors.ink) }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.line, RoundedCornerShape(19.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { onNavigate("profile") },
                        contentAlignment = Alignment.Center,
                    ) { UserIcon(tint = colors.ink) }
                }
            }
        }

        // Editorial hero text
        item {
            Column(
                modifier = Modifier
                    .padding(top = 28.dp)
                    .padding(horizontal = 20.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(1.dp)
                            .background(colors.ink3),
                    )
                    Text(
                        text = "Сезон №14 · Апрель",
                        style = TextStyle(
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.1.em, color = colors.ink3,
                        ),
                    )
                }

                Text(
                    text = buildAnnotatedString {
                        append("С грядки ")
                        withStyle(
                            SpanStyle(
                                fontStyle = FontStyle.Italic,
                                color = colors.accentDeep,
                            )
                        ) { append("прямо") }
                        append("\nна стол — за 40 минут.")
                    },
                    style = TextStyle(
                        fontFamily = FrauncesFontFamily,
                        fontSize = 38.sp, fontWeight = FontWeight.Normal,
                        letterSpacing = (-0.03).em, lineHeight = (38 * 1.0).sp,
                        color = colors.ink,
                    ),
                    modifier = Modifier.padding(top = 12.dp),
                )

                Text(
                    text = "Отбираем у 32 фермеров средней полосы. Без холодных складов — только то, что собрано сегодня.",
                    style = TextStyle(fontSize = 14.sp, color = colors.ink2, lineHeight = (14 * 1.5).sp),
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .widthIn(max = 300.dp),
                )

                Row(
                    modifier = Modifier
                        .padding(top = 18.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(colors.ink)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onNavigate("address") }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PinIcon(tint = colors.bg, size = 18.dp)
                    Text(
                        text = primaryAddress?.text ?: "Добавить адрес",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.bg),
                    )
                }
            }
        }

        // Featured product card
        item {
            val hue = hero.hue
            val cardBg = hueColor(hue, 0.28f, 0.93f)
            val darkTone = hueColor(hue, 0.22f, 0.2f)
            val medTone = hueColor(hue, 0.2f, 0.35f)

            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 28.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardBg)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onOpenProduct(hero.id) }
                    .padding(20.dp),
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column {
                            Text(
                                text = "Фермер недели".uppercase(),
                                style = TextStyle(
                                    fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.08.em, color = medTone,
                                ),
                            )
                            Text(
                                text = "Теплицы Юга\nБакинский розовый",
                                style = TextStyle(
                                    fontFamily = FrauncesFontFamily,
                                    fontSize = 22.sp, fontWeight = FontWeight.Medium,
                                    letterSpacing = (-0.02).em, lineHeight = (22 * 1.1).sp,
                                    color = darkTone,
                                ),
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                        Text(
                            text = "${hero.price} ₽",
                            style = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 24.sp, fontWeight = FontWeight.SemiBold,
                                color = darkTone,
                            ),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = 14.dp)
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(14.dp)),
                    ) {
                        ProductPlaceholder(hue = hue, size = 140.dp, label = "pomidor · baku")
                    }
                }
            }
        }

        // Section title + product grid
        item {
            SectionTitle(
                title = "Сегодня на грядке",
                action = "Весь каталог",
                onAction = { onNavigate("catalog") },
                colors = colors,
            )
        }

        item {
            // 2-column grid (non-lazy since inside LazyColumn)
            val chunked = gridItems.chunked(2)
            Column(
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                chunked.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { p ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = p,
                                    qty = vm.cart[p.id] ?: 0,
                                    onAdd = { vm.addToCart(p.id) },
                                    onSub = { vm.subFromCart(p.id) },
                                    onOpen = { onOpenProduct(p.id) },
                                    onFav = { vm.toggleFav(p.id) },
                                    isFav = vm.favs.contains(p.id),
                                    colors = colors,
                                )
                            }
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}