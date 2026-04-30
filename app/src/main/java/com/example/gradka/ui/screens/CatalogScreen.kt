package com.example.gradka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.gradka.AppViewModel
import com.example.gradka.domain.CATEGORIES
import com.example.gradka.domain.PRODUCTS
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.FrauncesFontFamily
import com.example.gradka.ui.theme.LocalAppColors

@Composable
fun CatalogScreen(
    vm: AppViewModel,
    onOpenProduct: (String) -> Unit,
) {
    val colors = LocalAppColors.current
    val filtered = if (vm.catFilter == "all") PRODUCTS else PRODUCTS.filter { it.cat == vm.catFilter }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .border(width = 1.dp, color = colors.line, shape = RoundedCornerShape(0.dp))
                    .padding(horizontal = 20.dp)
                    .padding(top = 14.dp, bottom = 10.dp)
                    .statusBarsPadding(),
            ) {
                Text(
                    text = "Каталог",
                    style = TextStyle(
                        fontFamily = FrauncesFontFamily,
                        fontSize = 24.sp, fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em,
                        color = colors.ink,
                    ),
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface)
                    .border(width = 1.dp, color = colors.line, shape = RoundedCornerShape(0.dp))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CATEGORIES.forEach { cat ->
                    Chip(
                        active = vm.catFilter == cat.id,
                        onClick = { vm.catFilter = cat.id },
                        colors = colors,
                    ) {
                        Text(
                            text = cat.label,
                            style = TextStyle(
                                fontSize = 13.sp, fontWeight = FontWeight.Medium,
                                color = if (vm.catFilter == cat.id) colors.bg else colors.ink,
                            ),
                        )
                    }
                }
            }
        }

        item {
            val chunks = filtered.chunked(2)
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp).padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                chunks.forEach { row ->
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
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}