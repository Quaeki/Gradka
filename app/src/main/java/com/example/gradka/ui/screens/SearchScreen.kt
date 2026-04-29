package com.example.gradka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.gradka.AppViewModel
import com.example.gradka.data.PRODUCTS
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.LocalAppColors

@Composable
fun SearchScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onOpenProduct: (String) -> Unit,
) {
    val colors = LocalAppColors.current
    var query by remember { mutableStateOf("") }
    val results = if (query.isNotEmpty()) PRODUCTS.filter { it.name.contains(query, ignoreCase = true) } else emptyList()
    val recent = listOf("творог", "базилик", "говядина")
    val suggestions = listOf("Молоко", "Яблоки", "Хлеб ржаной", "Авокадо", "Лосось")
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
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

            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surface2)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SearchIcon(tint = colors.ink3)
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(fontSize = 15.sp, color = colors.ink),
                    cursorBrush = SolidColor(colors.ink),
                    singleLine = true,
                    decorationBox = { inner ->
                        Box {
                            if (query.isEmpty()) {
                                Text(
                                    text = "Молоко, хлеб, яблоки…",
                                    style = TextStyle(fontSize = 15.sp, color = colors.ink3),
                                )
                            }
                            inner()
                        }
                    },
                )
                if (query.isNotEmpty()) {
                    Box(
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { query = "" },
                    ) { CloseIcon(tint = colors.ink3) }
                } else {
                    MicIcon(tint = colors.ink3)
                }
            }
        }

        if (query.isEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                item {
                    Text(
                        text = "Недавно искали",
                        style = TextStyle(
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.08.em, color = colors.ink3,
                        ),
                        modifier = Modifier.padding(top = 8.dp, bottom = 10.dp),
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        recent.forEach { r ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(colors.surface)
                                    .border(1.dp, colors.line, RoundedCornerShape(999.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) { query = r }
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                            ) {
                                Text(text = r, style = TextStyle(fontSize = 13.sp, color = colors.ink2))
                            }
                        }
                    }
                    Text(
                        text = "Популярные запросы",
                        style = TextStyle(
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.08.em, color = colors.ink3,
                        ),
                        modifier = Modifier.padding(top = 28.dp, bottom = 10.dp),
                    )
                }
                items(suggestions) { s ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { query = s }
                            .border(width = 0.dp, color = colors.line, shape = RoundedCornerShape(0.dp))
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SearchIcon(tint = colors.ink3)
                        Text(text = s, style = TextStyle(fontSize = 15.sp, color = colors.ink), modifier = Modifier.weight(1f))
                        ChevronIcon(tint = colors.ink3, size = 14.dp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Text(
                        text = "Найдено: ${results.size}",
                        style = TextStyle(fontSize = 13.sp, color = colors.ink3),
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
                if (results.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Ничего не нашли. Попробуйте другой запрос.",
                                style = TextStyle(fontSize = 14.sp, color = colors.ink3),
                            )
                        }
                    }
                } else {
                    items(results) { p ->
                        ProductRow(
                            product = p,
                            qty = vm.cart[p.id] ?: 0,
                            onAdd = { vm.addToCart(p.id) },
                            onSub = { vm.subFromCart(p.id) },
                            onOpen = { onOpenProduct(p.id) },
                            colors = colors,
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(28.dp)) }
            }
        }
    }
}