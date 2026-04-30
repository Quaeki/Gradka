package com.example.gradka.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gradka.AppViewModel
import com.example.gradka.domain.PRODUCTS
import com.example.gradka.domain.Product
import com.example.gradka.domain.Subscription
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

private data class FreqOption(val days: Int, val label: String, val short: String)

private val FREQUENCIES = listOf(
    FreqOption(3,  "Каждые 3 дня",   "3 дн"),
    FreqOption(7,  "Раз в неделю",   "нед"),
    FreqOption(14, "Раз в 2 недели", "2 нед"),
    FreqOption(30, "Раз в месяц",    "мес"),
)

private fun frequencyLabel(days: Int): String =
    FREQUENCIES.find { it.days == days }?.label ?: "$days дн"

@Composable
fun SubscriptionsScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onOpenProduct: (String) -> Unit,
) {
    val colors = LocalAppColors.current
    val subs by vm.subscriptions.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<String?>(null) }
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 110.dp),
        ) {

            // ── Header ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 14.dp, bottom = 4.dp),
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Подписки",
                            style = TextStyle(
                                fontFamily = FrauncesFontFamily, fontSize = 22.sp,
                                fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em,
                                color = colors.ink,
                            ),
                        )
                        if (subs.isNotEmpty()) {
                            Text(
                                text = "${subs.size} ${pluralRu(subs.size, "подписка", "подписки", "подписок")} · " +
                                    "${vm.subscriptionsActiveCount} активн.",
                                style = TextStyle(fontSize = 12.sp, color = colors.ink3),
                                modifier = Modifier.padding(top = 1.dp),
                            )
                        }
                    }
                }
            }

            // ── Stats card ──
            if (subs.any { it.active }) {
                item {
                    StatsCard(
                        monthly = vm.subscriptionsMonthlyTotal,
                        savings = vm.subscriptionsMonthlySavings,
                        activeCount = vm.subscriptionsActiveCount,
                        colors = colors,
                    )
                }
            }

            if (subs.isEmpty()) {
                item {
                    EmptyState(
                        colors = colors,
                        onAdd = { showAddSheet = true },
                    )
                }
            } else {
                item {
                    Text(
                        text = "ВАШИ ПОДПИСКИ",
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.08.em,
                            color = colors.ink3,
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    )
                }

                items(subs.size) { index ->
                    val sub = subs[index]
                    val product = PRODUCTS.find { it.id == sub.productId } ?: return@items
                    Box(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 10.dp)) {
                        SubscriptionCard(
                            sub = sub,
                            product = product,
                            colors = colors,
                            onOpen = { onOpenProduct(product.id) },
                            onEdit = { editingId = sub.id },
                            onTogglePause = { vm.updateSubscription(sub.id, active = !sub.active) },
                            onAdd = { vm.updateSubscription(sub.id, qty = sub.qty + 1) },
                            onSub = { vm.updateSubscription(sub.id, qty = (sub.qty - 1).coerceAtLeast(1)) },
                            onDelete = { pendingDeleteId = sub.id },
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(colors.accentSoft)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            LeafIcon(tint = colors.accentDeep, size = 18.dp)
                            Text(
                                text = "Подписка даёт −5% на каждую доставку",
                                style = TextStyle(fontSize = 12.sp, color = colors.accentDeep),
                            )
                        }
                    }
                }
            }
        }

        // ── Bottom CTA ──
        if (subs.isNotEmpty()) {
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
                        ) { showAddSheet = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        PlusIcon(tint = colors.bg, size = 18.dp)
                        Text(
                            text = "Добавить подписку",
                            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.bg),
                        )
                    }
                }
            }
        }
    }

    if (showAddSheet) {
        AddSubscriptionSheet(
            colors = colors,
            existingProductIds = subs.map { it.productId }.toSet(),
            onDismiss = { showAddSheet = false },
            onSave = { productId, qty, days ->
                vm.addSubscription(productId, qty, days)
                showAddSheet = false
            },
        )
    }

    editingId?.let { id ->
        val sub = subs.find { it.id == id }
        val product = sub?.let { s -> PRODUCTS.find { it.id == s.productId } }
        if (sub != null && product != null) {
            EditSubscriptionSheet(
                sub = sub,
                product = product,
                colors = colors,
                onDismiss = { editingId = null },
                onChangeQty = { qty -> vm.updateSubscription(sub.id, qty = qty) },
                onChangeFreq = { d -> vm.updateSubscription(sub.id, frequencyDays = d) },
                onTogglePause = { vm.updateSubscription(sub.id, active = !sub.active) },
                onDelete = {
                    vm.deleteSubscription(sub.id)
                    editingId = null
                },
            )
        } else {
            editingId = null
        }
    }

    pendingDeleteId?.let { id ->
        val sub = subs.find { it.id == id }
        val product = sub?.let { s -> PRODUCTS.find { it.id == s.productId } }
        if (sub != null && product != null) {
            DeleteSubscriptionSheet(
                productName = product.name,
                colors = colors,
                onDismiss = { pendingDeleteId = null },
                onConfirm = {
                    vm.deleteSubscription(sub.id)
                    pendingDeleteId = null
                },
            )
        } else {
            pendingDeleteId = null
        }
    }
}

// ── StatsCard ──
@Composable
private fun StatsCard(monthly: Int, savings: Int, activeCount: Int, colors: AppColors) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.ink)
            .padding(horizontal = 18.dp, vertical = 18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = "В МЕСЯЦ",
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.1.em,
                        color = colors.bg.copy(alpha = 0.55f),
                    ),
                )
                Text(
                    text = "$monthly ₽",
                    style = TextStyle(
                        fontFamily = FrauncesFontFamily,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = (-0.02).em,
                        color = colors.bg,
                    ),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(colors.bg.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    text = "$activeCount активн.",
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = colors.bg),
                )
            }
        }
        Row(
            modifier = Modifier.padding(top = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(colors.accent)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    text = "−5%",
                    style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colors.bg),
                )
            }
            Text(
                text = "Экономия ≈ $savings ₽/мес",
                style = TextStyle(fontSize = 12.sp, color = colors.bg.copy(alpha = 0.7f)),
            )
        }
    }
}

// ── Empty state ──
@Composable
private fun EmptyState(colors: AppColors, onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 40.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(colors.accentSoft),
            contentAlignment = Alignment.Center,
        ) {
            RepeatIcon(tint = colors.accentDeep, size = 38.dp)
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Подписок пока нет",
            style = TextStyle(
                fontFamily = FrauncesFontFamily, fontSize = 20.sp,
                fontWeight = FontWeight.Medium, color = colors.ink,
            ),
        )
        Text(
            text = "Получайте любимые продукты автоматически и со скидкой 5%",
            style = TextStyle(fontSize = 13.sp, color = colors.ink3, lineHeight = (13 * 1.45).sp),
            modifier = Modifier.padding(top = 6.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(colors.ink)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, onClick = onAdd,
                )
                .padding(horizontal = 18.dp, vertical = 13.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PlusIcon(tint = colors.bg, size = 16.dp)
                Text(
                    text = "Создать подписку",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.bg),
                )
            }
        }
    }
}

// ── Subscription card ──
@Composable
private fun SubscriptionCard(
    sub: Subscription,
    product: Product,
    colors: AppColors,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onTogglePause: () -> Unit,
    onAdd: () -> Unit,
    onSub: () -> Unit,
    onDelete: () -> Unit,
) {
    val cardAlpha = if (sub.active) 1f else 0.6f
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(1.dp, colors.line, RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .alpha(cardAlpha)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClick = onOpen,
                    ),
            ) {
                ProductPlaceholder(hue = product.hue, size = 64.dp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            style = TextStyle(
                                fontSize = 14.sp, fontWeight = FontWeight.Medium,
                                color = colors.ink.copy(alpha = cardAlpha),
                                lineHeight = (14 * 1.3).sp,
                            ),
                        )
                        Text(
                            text = "${product.subtitle} · ${product.unit}",
                            style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colors.surface2)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null, onClick = onEdit,
                            ),
                        contentAlignment = Alignment.Center,
                    ) { MoreIcon(tint = colors.ink2, size = 18.dp) }
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatusChip(
                        text = if (sub.active) frequencyLabel(sub.frequencyDays) else "На паузе",
                        bg = if (sub.active) colors.accentSoft else colors.surface2,
                        fg = if (sub.active) colors.accentDeep else colors.ink3,
                        leadingIcon = {
                            if (sub.active) RepeatIcon(tint = colors.accentDeep, size = 12.dp)
                            else PauseIcon(tint = colors.ink3, size = 12.dp)
                        },
                    )
                    if (sub.active) {
                        StatusChip(
                            text = sub.nextDelivery,
                            bg = colors.surface2,
                            fg = colors.ink2,
                            leadingIcon = { ClockIcon(tint = colors.ink2, size = 12.dp) },
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.line))
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "${product.price * sub.qty} ₽",
                    style = TextStyle(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.ink,
                    ),
                )
                Text(
                    text = "за доставку",
                    style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Stepper(
                    qty = sub.qty,
                    onAdd = onAdd,
                    onSub = onSub,
                    compact = true,
                    colors = colors,
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (sub.active) colors.surface2 else colors.accentSoft)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, onClick = onTogglePause,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (sub.active) {
                        PauseIcon(tint = colors.ink2, size = 14.dp)
                    } else {
                        PlayIcon(tint = colors.accentDeep, size = 13.dp)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colors.surface2)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, onClick = onDelete,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    TrashIcon(tint = colors.danger, size = 15.dp)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    bg: Color,
    fg: Color,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (leadingIcon != null) leadingIcon()
        Text(
            text = text,
            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = fg),
        )
    }
}

// ── Add bottom sheet ──
@Composable
private fun AddSubscriptionSheet(
    colors: AppColors,
    existingProductIds: Set<String>,
    onDismiss: () -> Unit,
    onSave: (productId: String, qty: Int, days: Int) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var queryFocused by remember { mutableStateOf(false) }
    var selectedProductId by remember { mutableStateOf<String?>(null) }
    var qty by remember { mutableIntStateOf(1) }
    var days by remember { mutableIntStateOf(7) }

    val filtered = remember(query) {
        PRODUCTS.filter {
            !existingProductIds.contains(it.id) &&
                (query.isBlank() ||
                    it.name.contains(query, ignoreCase = true) ||
                    it.subtitle.contains(query, ignoreCase = true))
        }
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
                    .heightIn(max = 640.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(colors.surface)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {}
                    .imePadding()
                    .navigationBarsPadding(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(colors.line2),
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Новая подписка",
                        style = TextStyle(
                            fontFamily = FrauncesFontFamily, fontSize = 20.sp,
                            fontWeight = FontWeight.Medium, color = colors.ink,
                        ),
                    )
                    Text(
                        text = if (selectedProductId == null) "Шаг 1 · выберите продукт" else "Шаг 2 · параметры доставки",
                        style = TextStyle(fontSize = 12.sp, color = colors.ink3),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                if (selectedProductId == null) {
                    // Search
                    BasicTextField(
                        value = query,
                        onValueChange = { query = it },
                        textStyle = TextStyle(fontSize = 15.sp, color = colors.ink),
                        cursorBrush = SolidColor(colors.ink),
                        singleLine = true,
                        decorationBox = { inner ->
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 12.dp)
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.bg)
                                    .border(
                                        1.5.dp,
                                        if (queryFocused) colors.ink else colors.line,
                                        RoundedCornerShape(12.dp),
                                    )
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                SearchIcon(tint = colors.ink3, size = 18.dp)
                                Box(modifier = Modifier.weight(1f)) {
                                    if (query.isEmpty()) {
                                        Text(
                                            text = "Поиск продукта…",
                                            style = TextStyle(fontSize = 15.sp, color = colors.ink3),
                                        )
                                    }
                                    inner()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { queryFocused = it.isFocused },
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filtered.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = if (query.isBlank()) "Все продукты уже в подписках"
                                else "Ничего не найдено",
                                style = TextStyle(fontSize = 13.sp, color = colors.ink3),
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            items(filtered.size) { i ->
                                val p = filtered[i]
                                ProductPickerRow(
                                    product = p,
                                    colors = colors,
                                    onClick = { selectedProductId = p.id },
                                )
                            }
                        }
                    }
                } else {
                    val product = PRODUCTS.find { it.id == selectedProductId }!!
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        // Selected product preview
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.bg)
                                .border(1.dp, colors.line, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            ProductPlaceholder(hue = product.hue, size = 56.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink),
                                )
                                Text(
                                    text = "${product.price} ₽ · ${product.unit}",
                                    style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 12.sp, color = colors.ink3),
                                    modifier = Modifier.padding(top = 2.dp),
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(colors.surface2)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) { selectedProductId = null },
                                contentAlignment = Alignment.Center,
                            ) { CloseIcon(tint = colors.ink3, size = 14.dp) }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "ЧАСТОТА",
                            style = TextStyle(
                                fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.08.em, color = colors.ink3,
                            ),
                            modifier = Modifier.padding(bottom = 8.dp),
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FREQUENCIES.forEach { f ->
                                FrequencyRow(
                                    label = f.label,
                                    days = f.days,
                                    selected = days == f.days,
                                    onClick = { days = f.days },
                                    colors = colors,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = "КОЛИЧЕСТВО",
                                    style = TextStyle(
                                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.08.em, color = colors.ink3,
                                    ),
                                )
                                Text(
                                    text = "$qty × ${product.unit}",
                                    style = TextStyle(fontSize = 13.sp, color = colors.ink, fontWeight = FontWeight.Medium),
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                            Stepper(
                                qty = qty.coerceAtLeast(1),
                                onAdd = { qty += 1 },
                                onSub = { qty = (qty - 1).coerceAtLeast(1) },
                                colors = colors,
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f, fill = false))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    val canSave = selectedProductId != null
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (canSave) colors.ink else colors.surface2)
                            .clickable(
                                enabled = canSave,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                selectedProductId?.let { onSave(it, qty.coerceAtLeast(1), days) }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (canSave) "Подписаться" else "Выберите продукт",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (canSave) colors.bg else colors.ink3,
                            ),
                        )
                    }
                }
            }
        }
    }
}

// ── Edit bottom sheet ──
@Composable
private fun EditSubscriptionSheet(
    sub: Subscription,
    product: Product,
    colors: AppColors,
    onDismiss: () -> Unit,
    onChangeQty: (Int) -> Unit,
    onChangeFreq: (Int) -> Unit,
    onTogglePause: () -> Unit,
    onDelete: () -> Unit,
) {
    var confirmDelete by remember { mutableStateOf(false) }

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

                AnimatedVisibility(
                    visible = !confirmDelete,
                    enter = fadeIn(tween(120)),
                    exit = fadeOut(tween(80)),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            ProductPlaceholder(hue = product.hue, size = 48.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    style = TextStyle(
                                        fontFamily = FrauncesFontFamily, fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium, color = colors.ink,
                                    ),
                                )
                                Text(
                                    text = if (sub.active) "Активна · ${frequencyLabel(sub.frequencyDays)}" else "На паузе",
                                    style = TextStyle(fontSize = 12.sp, color = colors.ink3),
                                    modifier = Modifier.padding(top = 2.dp),
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "ЧАСТОТА",
                            style = TextStyle(
                                fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.08.em, color = colors.ink3,
                            ),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FREQUENCIES.forEach { f ->
                                FrequencyRow(
                                    label = f.label,
                                    days = f.days,
                                    selected = sub.frequencyDays == f.days,
                                    onClick = { onChangeFreq(f.days) },
                                    colors = colors,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.line))
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = "Количество",
                                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.ink),
                                )
                                Text(
                                    text = "${sub.qty} × ${product.unit} · ${product.price * sub.qty} ₽",
                                    style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                                    modifier = Modifier.padding(top = 2.dp),
                                )
                            }
                            Stepper(
                                qty = sub.qty,
                                onAdd = { onChangeQty(sub.qty + 1) },
                                onSub = { onChangeQty((sub.qty - 1).coerceAtLeast(1)) },
                                colors = colors,
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (sub.active) colors.surface2 else colors.accentSoft)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null, onClick = onTogglePause,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    if (sub.active) PauseIcon(tint = colors.ink2, size = 14.dp)
                                    else PlayIcon(tint = colors.accentDeep, size = 13.dp)
                                    Text(
                                        text = if (sub.active) "Пауза" else "Возобновить",
                                        style = TextStyle(
                                            fontSize = 14.sp, fontWeight = FontWeight.Medium,
                                            color = if (sub.active) colors.ink2 else colors.accentDeep,
                                        ),
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(colors.surface2)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) { confirmDelete = true },
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    TrashIcon(tint = colors.danger, size = 14.dp)
                                    Text(
                                        text = "Удалить",
                                        style = TextStyle(
                                            fontSize = 14.sp, fontWeight = FontWeight.Medium,
                                            color = colors.danger,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = confirmDelete,
                    enter = fadeIn(tween(120)),
                    exit = fadeOut(tween(80)),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(colors.surface2),
                            contentAlignment = Alignment.Center,
                        ) {
                            TrashIcon(tint = colors.danger)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Отписаться?",
                            style = TextStyle(
                                fontFamily = FrauncesFontFamily, fontSize = 20.sp,
                                fontWeight = FontWeight.Medium, color = colors.ink,
                            ),
                        )
                        Text(
                            text = "«${product.name}» больше не будет приходить автоматически",
                            style = TextStyle(fontSize = 13.sp, color = colors.ink3, lineHeight = (13 * 1.4).sp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.danger)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null, onClick = onDelete,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Отписаться",
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.bg),
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.surface2)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { confirmDelete = false },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Отмена",
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.ink),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Delete confirmation sheet ──
@Composable
private fun DeleteSubscriptionSheet(
    productName: String,
    colors: AppColors,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
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
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(colors.surface2),
                    contentAlignment = Alignment.Center,
                ) { TrashIcon(tint = colors.danger) }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Отписаться?",
                    style = TextStyle(
                        fontFamily = FrauncesFontFamily, fontSize = 20.sp,
                        fontWeight = FontWeight.Medium, color = colors.ink,
                    ),
                )
                Text(
                    text = "«$productName» больше не будет приходить автоматически",
                    style = TextStyle(fontSize = 13.sp, color = colors.ink3, lineHeight = (13 * 1.4).sp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.danger)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, onClick = onConfirm,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Отписаться",
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.bg),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.surface2)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, onClick = onDismiss,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Отмена",
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.ink),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductPickerRow(
    product: Product,
    colors: AppColors,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.bg)
            .border(1.dp, colors.line, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, onClick = onClick,
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProductPlaceholder(hue = product.hue, size = 44.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink),
            )
            Text(
                text = "${product.subtitle} · ${product.unit}",
                style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                modifier = Modifier.padding(top = 1.dp),
            )
        }
        Text(
            text = "${product.price} ₽",
            style = TextStyle(
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
            ),
        )
    }
}

@Composable
private fun FrequencyRow(
    label: String,
    days: Int,
    selected: Boolean,
    onClick: () -> Unit,
    colors: AppColors,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bg)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) colors.ink else colors.line,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
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
        Text(
            text = label,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "$days дн.",
            style = TextStyle(
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 12.sp,
                color = colors.ink3,
            ),
        )
    }
}

// ── Helpers ──
@Composable
private fun PauseIcon(tint: Color, size: Dp = 14.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val w = s * 0.16f
        drawRoundRect(
            color = tint,
            topLeft = Offset(s * 0.30f, s * 0.20f),
            size = androidx.compose.ui.geometry.Size(w, s * 0.60f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.4f),
        )
        drawRoundRect(
            color = tint,
            topLeft = Offset(s * 0.54f, s * 0.20f),
            size = androidx.compose.ui.geometry.Size(w, s * 0.60f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.4f),
        )
    }
}

@Composable
private fun PlayIcon(tint: Color, size: Dp = 14.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.30f, s * 0.18f)
            lineTo(s * 0.82f, s * 0.50f)
            lineTo(s * 0.30f, s * 0.82f)
            close()
        }
        drawPath(path = path, color = tint)
        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

private fun pluralRu(n: Int, one: String, few: String, many: String): String {
    val mod10 = n % 10
    val mod100 = n % 100
    return when {
        mod10 == 1 && mod100 != 11 -> one
        mod10 in 2..4 && (mod100 < 12 || mod100 > 14) -> few
        else -> many
    }
}
