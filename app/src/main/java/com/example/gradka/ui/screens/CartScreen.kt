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
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

@Composable
fun CartScreen(
    vm: AppViewModel,
    onCheckout: () -> Unit,
    onOpenProduct: (String) -> Unit,
    onCatalog: () -> Unit,
) {
    val colors = LocalAppColors.current
    val items = vm.cart.entries
        .filter { it.value > 0 }
        .mapNotNull { (id, qty) -> PRODUCTS.find { it.id == id }?.let { it to qty } }

    if (items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(colors.bg),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(40.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(colors.surface2),
                    contentAlignment = Alignment.Center,
                ) { BagIcon(tint = colors.ink3, size = 40.dp) }
                Text(
                    text = "Корзина пуста",
                    style = TextStyle(
                        fontFamily = FrauncesFontFamily, fontSize = 26.sp,
                        fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.ink,
                    ),
                )
                Text(
                    text = "Добавьте что-нибудь вкусное из каталога",
                    style = TextStyle(fontSize = 14.sp, color = colors.ink2, lineHeight = (14 * 1.5).sp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(colors.ink)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, onClick = onCatalog,
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                ) {
                    Text(text = "К каталогу", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.bg))
                }
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 14.dp, bottom = 10.dp)
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Корзина",
                        style = TextStyle(
                            fontFamily = FrauncesFontFamily, fontSize = 24.sp,
                            fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.ink,
                        ),
                    )
                    Text(text = "${items.size} товаров", style = TextStyle(fontSize = 13.sp, color = colors.ink3))
                }
            }

            if (vm.cartDelivery > 0) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.accentSoft)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text = buildAnnotatedString(vm.cartSubtotal, colors),
                            style = TextStyle(fontSize = 13.sp, color = colors.accentDeep),
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(colors.ink.copy(alpha = 0.08f)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = (vm.cartSubtotal / 1500f).coerceIn(0f, 1f))
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(colors.accent),
                            )
                        }
                    }
                }
            }

            items(items) { (p, qty) ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.line, RoundedCornerShape(14.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onOpenProduct(p.id) }) {
                        ProductPlaceholder(hue = p.hue, size = 64.dp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = p.name, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink))
                        Text(text = p.unit, style = TextStyle(fontSize = 11.sp, color = colors.ink3), modifier = Modifier.padding(top = 2.dp))
                        Text(
                            text = "${p.price * qty} ₽",
                            style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.ink),
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                    Stepper(
                        qty = qty, onAdd = { vm.addToCart(p.id) }, onSub = { vm.subFromCart(p.id) },
                        compact = true, colors = colors,
                    )
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "Товары", style = TextStyle(fontSize = 14.sp, color = colors.ink2))
                        Text(text = "${vm.cartSubtotal} ₽", style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, color = colors.ink2))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "Доставка", style = TextStyle(fontSize = 14.sp, color = colors.ink2))
                        Text(
                            text = if (vm.cartDelivery == 0) "Бесплатно" else "${vm.cartDelivery} ₽",
                            style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, color = colors.ink2),
                        )
                    }
                    Spacer(
                        modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.line).padding(vertical = 10.dp),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "Итого", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colors.ink))
                        Text(
                            text = "${vm.cartTotal} ₽",
                            style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colors.ink),
                        )
                    }
                }
            }
        }

        // Sticky CTA
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(colors.bg)
                .border(width = 1.dp, color = colors.line, shape = RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.ink)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClick = onCheckout,
                    )
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Оформить заказ", style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.bg))
                Text(
                    text = "${vm.cartTotal} ₽ →",
                    style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.bg),
                )
            }
        }
    }
}

private fun buildAnnotatedString(
    subtotal: Int,
    colors: com.example.gradka.ui.theme.AppColors,
): androidx.compose.ui.text.AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        append("До бесплатной доставки: ")
        append("${1500 - subtotal} ₽")
    }
}