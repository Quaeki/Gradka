package com.example.gradka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.gradka.AppViewModel
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

@Composable
fun CheckoutScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onPay: () -> Unit,
    onAddress: () -> Unit,
) {
    val colors = LocalAppColors.current
    var selectedSlot by remember { mutableIntStateOf(0) }
    var selectedPayment by remember { mutableStateOf("card") }
    val slots = listOf("Сегодня · 19:00–20:00", "Сегодня · 20:30–21:30", "Завтра · 08:00–09:00")
    val payments = listOf(
        Triple("card",  "Карта •• 4821",         "Visa"),
        Triple("sbp",   "СБП",                    "Система быстрых платежей"),
        Triple("cash",  "Наличными курьеру",       "Сдача с 3 000 ₽"),
    )

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 14.dp, bottom = 10.dp)
                        .statusBarsPadding(),
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
                        text = "Оформление",
                        style = TextStyle(
                            fontFamily = FrauncesFontFamily, fontSize = 22.sp,
                            fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.ink,
                        ),
                    )
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    // Address
                    SectionLabel("Адрес доставки", colors)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.line, RoundedCornerShape(14.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null, onClick = onAddress,
                            )
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.accentSoft),
                            contentAlignment = Alignment.Center,
                        ) { PinIcon(tint = colors.accentDeep, size = 20.dp) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "ул. Лесная, 14, кв. 47", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink))
                            Text(text = "Код домофона 47В", style = TextStyle(fontSize = 12.sp, color = colors.ink3), modifier = Modifier.padding(top = 2.dp))
                        }
                        ChevronIcon(tint = colors.ink3)
                    }

                    // Time slots
                    SectionLabel("Время доставки", colors)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        slots.forEachIndexed { i, slot ->
                            RadioRow(
                                label = slot, subtitle = null,
                                selected = selectedSlot == i,
                                onClick = { selectedSlot = i },
                                colors = colors,
                            )
                        }
                    }

                    // Payment
                    SectionLabel("Оплата", colors)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        payments.forEach { (id, label, sub) ->
                            RadioRow(
                                label = label, subtitle = sub,
                                selected = selectedPayment == id,
                                onClick = { selectedPayment = id },
                                colors = colors,
                            )
                        }
                    }

                    // Summary
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.line))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "Товары", style = TextStyle(fontSize = 14.sp, color = colors.ink2))
                            Text(text = "${vm.cartSubtotal} ₽", style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, color = colors.ink2))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "Доставка", style = TextStyle(fontSize = 14.sp, color = colors.ink2))
                            Text(
                                text = if (vm.cartDelivery == 0) "Бесплатно" else "${vm.cartDelivery} ₽",
                                style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, color = colors.ink2),
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(text = "К оплате", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colors.ink))
                            Text(
                                text = "${vm.cartTotal} ₽",
                                style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colors.ink),
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(colors.bg)
                .border(width = 1.dp, color = colors.line, shape = RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.ink)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClick = onPay,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Оплатить ${vm.cartTotal} ₽",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.bg),
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, colors: AppColors) {
    Text(
        text = text.uppercase(),
        style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.08.em, color = colors.ink3),
    )
}

@Composable
private fun RadioRow(label: String, subtitle: String?, selected: Boolean, onClick: () -> Unit, colors: AppColors) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) colors.ink else colors.line,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(9.dp))
                .border(
                    2.dp,
                    if (selected) colors.ink else colors.line2,
                    RoundedCornerShape(9.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colors.ink),
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink))
            if (subtitle != null) {
                Text(text = subtitle, style = TextStyle(fontSize = 11.sp, color = colors.ink3), modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}