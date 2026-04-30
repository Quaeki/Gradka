package com.example.gradka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gradka.AppViewModel
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

private data class PaymentOption(
    val id: String,
    val label: String,
    val sub: String,
)

@Composable
fun CheckoutScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onPay: () -> Unit,
    onAddress: () -> Unit,
) {
    val colors = LocalAppColors.current
    var selectedSlot by remember { mutableIntStateOf(0) }
    var selectedPayment by remember { mutableStateOf("sbp") }
    var showCardSheet by remember { mutableStateOf(false) }
    val addresses by vm.addresses.collectAsState()
    val paymentMethods by vm.paymentMethods.collectAsState()
    val deliveryAddress = addresses.firstOrNull { it.primary } ?: addresses.firstOrNull()

    val slots = listOf("Сегодня · 19:00–20:00", "Сегодня · 20:30–21:30", "Завтра · 08:00–09:00")
    val basePayments = listOf(
        PaymentOption("sbp", "СБП", "Система быстрых платежей"),
        PaymentOption("cash", "Наличными курьеру", "Сдача с 3 000 ₽"),
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
                            Text(
                                text = deliveryAddress?.text ?: "Добавить адрес доставки",
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink),
                            )
                            Text(
                                text = deliveryAddress?.note?.takeIf { it.isNotBlank() }
                                    ?: "Выберите адрес перед оплатой",
                                style = TextStyle(fontSize = 12.sp, color = colors.ink3),
                                modifier = Modifier.padding(top = 2.dp),
                            )
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
                        paymentMethods.forEach { method ->
                            RadioRow(
                                label = "Карта •• ${method.last4}",
                                subtitle = "${method.brand} · ${expiryLabel(method.expiryMonth, method.expiryYear)}",
                                selected = selectedPayment == method.id,
                                onClick = { selectedPayment = method.id },
                                colors = colors,
                            )
                        }
                        basePayments.forEach { p ->
                            RadioRow(
                                label = p.label, subtitle = p.sub,
                                selected = selectedPayment == p.id,
                                onClick = { selectedPayment = p.id },
                                colors = colors,
                            )
                        }
                        AddCardRow(colors = colors, onClick = { showCardSheet = true })
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
                        indication = null,
                        onClick = {
                            if (deliveryAddress == null) onAddress() else onPay()
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (deliveryAddress == null) "Выбрать адрес" else "Оплатить ${vm.cartTotal} ₽",
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.bg),
                )
            }
        }
    }

    if (showCardSheet) {
        NewCardSheet(
            colors = colors,
            onDismiss = { showCardSheet = false },
            onSave = { last4, brand, expiryMonth, expiryYear ->
                val id = vm.addPaymentMethod(last4, brand, expiryMonth, expiryYear)
                selectedPayment = id
                showCardSheet = false
            },
        )
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

@Composable
private fun AddCardRow(colors: AppColors, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .border(
                width = 1.dp,
                color = colors.line,
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
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.surface2),
            contentAlignment = Alignment.Center,
        ) { PlusIcon(tint = colors.ink, size = 16.dp) }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Новая карта",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink),
            )
            Text(
                text = "Visa, Mastercard, МИР",
                style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        ChevronIcon(tint = colors.ink3)
    }
}

@Composable
private fun NewCardSheet(
    colors: AppColors,
    onDismiss: () -> Unit,
    onSave: (last4: String, brand: String, expiryMonth: Int, expiryYear: Int) -> Unit,
) {
    var number by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var holder by remember { mutableStateOf("") }

    val digits = number.filter { it.isDigit() }
    val expiryDigits = expiry.filter { it.isDigit() }
    val expiryMonth = expiryDigits.take(2).toIntOrNull()
    val expiryYear = expiryDigits.drop(2).take(2).toIntOrNull()?.let { 2000 + it }
    val hasValidExpiry = expiryMonth != null &&
        expiryYear != null &&
        isValidExpiry(expiryMonth, expiryYear)
    val isValid = digits.length == 16 &&
        hasValidExpiry &&
        cvv.filter { it.isDigit() }.length == 3

    val brand = when {
        digits.startsWith("220") -> "МИР"
        digits.startsWith("4") -> "Visa"
        digits.startsWith("5") || digits.startsWith("2") -> "Mastercard"
        else -> "Карта"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.ink.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onDismiss() },
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(colors.surface)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {}
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(colors.line2),
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Новая карта",
                    style = TextStyle(
                        fontFamily = FrauncesFontFamily, fontSize = 20.sp,
                        fontWeight = FontWeight.Medium, color = colors.ink,
                    ),
                )
                Text(
                    text = "Данные защищены и не передаются магазину",
                    style = TextStyle(fontSize = 12.sp, color = colors.ink3),
                    modifier = Modifier.padding(top = 6.dp),
                )
                Spacer(modifier = Modifier.height(18.dp))

                CardField(
                    value = number,
                    onValueChange = { v ->
                        val d = v.filter { it.isDigit() }.take(16)
                        number = d.chunked(4).joinToString(" ")
                    },
                    placeholder = "0000 0000 0000 0000",
                    keyboardType = KeyboardType.Number,
                    colors = colors,
                    monospace = true,
                    trailing = {
                        if (digits.isNotEmpty()) {
                            Text(
                                text = brand,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.ink3,
                                    letterSpacing = 0.04.em,
                                ),
                            )
                        }
                    },
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CardField(
                            value = expiry,
                            onValueChange = { v ->
                                val d = v.filter { it.isDigit() }.take(4)
                                expiry = when {
                                    d.length >= 3 -> d.take(2) + "/" + d.substring(2)
                                    else -> d
                                }
                            },
                            placeholder = "MM/ГГ",
                            keyboardType = KeyboardType.Number,
                            colors = colors,
                            monospace = true,
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        CardField(
                            value = cvv,
                            onValueChange = { v ->
                                cvv = v.filter { it.isDigit() }.take(3)
                            },
                            placeholder = "CVV",
                            keyboardType = KeyboardType.NumberPassword,
                            colors = colors,
                            monospace = true,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                CardField(
                    value = holder,
                    onValueChange = { holder = it.uppercase().filter { c -> c.isLetter() || c == ' ' } },
                    placeholder = "Имя на карте",
                    keyboardType = KeyboardType.Text,
                    colors = colors,
                    monospace = false,
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isValid) colors.ink else colors.surface2)
                        .clickable(
                            enabled = isValid,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            if (isValid) {
                                onSave(digits.takeLast(4), brand, expiryMonth, expiryYear)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Сохранить карту",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isValid) colors.bg else colors.ink3,
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, onClick = onDismiss,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Отмена",
                        style = TextStyle(fontSize = 14.sp, color = colors.ink3),
                    )
                }
            }
        }
    }
}

@Composable
private fun CardField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    colors: AppColors,
    monospace: Boolean,
    trailing: (@Composable () -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = 15.sp,
            color = colors.ink,
            fontFamily = if (monospace) JetBrainsMonoFontFamily else null,
            letterSpacing = if (monospace) 0.04.em else 0.em,
        ),
        cursorBrush = SolidColor(colors.ink),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        decorationBox = { inner ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.bg)
                    .border(1.5.dp, if (focused) colors.ink else colors.line, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                fontSize = 15.sp,
                                color = colors.ink3,
                                fontFamily = if (monospace) JetBrainsMonoFontFamily else null,
                                letterSpacing = if (monospace) 0.04.em else 0.em,
                            ),
                        )
                    }
                    inner()
                }
                if (trailing != null) trailing()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused },
    )
}

private fun expiryLabel(month: Int, year: Int): String {
    val monthLabel = month.coerceIn(1, 12).toString().padStart(2, '0')
    val yearLabel = (year % 100).toString().padStart(2, '0')
    return "$monthLabel/$yearLabel"
}

private fun isValidExpiry(month: Int, year: Int): Boolean {
    if (month !in 1..12) return false
    val calendar = java.util.Calendar.getInstance()
    val currentYear = calendar.get(java.util.Calendar.YEAR)
    val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1
    return year > currentYear || (year == currentYear && month >= currentMonth)
}
