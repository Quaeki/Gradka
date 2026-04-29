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
import com.example.gradka.AppViewModel
import com.example.gradka.data.PRODUCTS
import com.example.gradka.data.RECIPES
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

@Composable
fun RecipesScreen(
    vm: AppViewModel,
    onCartNavigate: () -> Unit,
) {
    val colors = LocalAppColors.current

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(colors.bg).statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp).padding(top = 14.dp)) {
                Text(
                    text = "Рецепты",
                    style = TextStyle(
                        fontFamily = FrauncesFontFamily, fontSize = 24.sp,
                        fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.ink,
                    ),
                )
                Text(
                    text = "Ингредиенты в корзину в 1 клик",
                    style = TextStyle(fontSize = 13.sp, color = colors.ink3),
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        items(RECIPES) { recipe ->
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.line, RoundedCornerShape(18.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                ) {
                    ProductPlaceholder(hue = recipe.hue, size = 140.dp, label = "recipe")
                }
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Text(
                        text = recipe.title,
                        style = TextStyle(
                            fontSize = 16.sp, fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.01).em, color = colors.ink,
                        ),
                    )
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            ClockIcon(tint = colors.ink3)
                            Text(text = recipe.time, style = TextStyle(fontSize = 12.sp, color = colors.ink3))
                        }
                        Text(text = "${recipe.items} ингредиентов", style = TextStyle(fontSize = 12.sp, color = colors.ink3))
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth()
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.ink)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                val count = minOf(recipe.items, PRODUCTS.size)
                                PRODUCTS.take(count).forEach { vm.addToCart(it.id) }
                                onCartNavigate()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Добавить все в корзину",
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.bg),
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(28.dp)) }
    }
}