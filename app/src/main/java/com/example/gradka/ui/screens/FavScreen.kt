package com.example.gradka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.gradka.AppViewModel
import com.example.gradka.data.PRODUCTS
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

@Composable
fun FavScreen(
    vm: AppViewModel,
    onOpenProduct: (String) -> Unit,
) {
    val colors = LocalAppColors.current
    val favList = PRODUCTS.filter { vm.favs.contains(it.id) }
    val lists = listOf(
        Triple("Еженедельная закупка", 12, 125f),
        Triple("Завтраки", 6, 48f),
        Triple("Гости в субботу", 18, 28f),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(colors.bg).statusBarsPadding(),
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp).padding(top = 14.dp, bottom = 4.dp)) {
                Text(
                    text = "Избранное",
                    style = TextStyle(
                        fontFamily = FrauncesFontFamily, fontSize = 24.sp,
                        fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.ink,
                    ),
                )
                Text(text = "${favList.size} товаров", style = TextStyle(fontSize = 13.sp, color = colors.ink3), modifier = Modifier.padding(top = 2.dp))
            }
        }

        if (favList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(colors.surface2),
                        contentAlignment = Alignment.Center,
                    ) { HeartIcon(tint = colors.ink3, filled = false, size = 32.dp) }
                    Text(
                        text = "Нажимайте на сердечко в карточке, чтобы сохранять товары",
                        style = TextStyle(fontSize = 14.sp, color = colors.ink2, lineHeight = (14 * 1.5).sp),
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        } else {
            item {
                val chunks = favList.chunked(2)
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    chunks.forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { p ->
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductCard(
                                        product = p, qty = vm.cart[p.id] ?: 0,
                                        onAdd = { vm.addToCart(p.id) }, onSub = { vm.subFromCart(p.id) },
                                        onOpen = { onOpenProduct(p.id) },
                                        onFav = { vm.toggleFav(p.id) }, isFav = true, colors = colors,
                                    )
                                }
                            }
                            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
                Text(
                    text = "Мои списки",
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.em, color = colors.ink3),
                    modifier = Modifier.padding(bottom = 10.dp),
                )
                lists.forEach { (title, count, hue) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.line, RoundedCornerShape(12.dp))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(hueColor(hue, 0.28f, 0.93f)),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink))
                            Text(text = "$count товаров", style = TextStyle(fontSize = 11.sp, color = colors.ink3), modifier = Modifier.padding(top = 1.dp))
                        }
                        ChevronIcon(tint = colors.ink3)
                    }
                }
            }
        }
    }
}