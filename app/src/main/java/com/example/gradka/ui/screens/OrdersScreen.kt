package com.example.gradka.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
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
import com.example.gradka.data.Order
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

private val ORDER_STAGES = listOf("Принят", "Собирается", "В пути", "Доставлен")

private fun stageIndex(status: String) = when (status) {
    "Собирается" -> 1
    "В пути"     -> 2
    "Доставлен"  -> 3
    "Отменён"    -> -1
    else         -> 0
}

private fun isActive(status: String) = status == "В пути" || status == "Собирается"

@Composable
fun OrdersScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onTracking: () -> Unit,
) {
    val colors = LocalAppColors.current
    val allOrders by vm.orders.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Все", "Активные", "История")

    val shown = when (selectedTab) {
        1    -> allOrders.filter { isActive(it.status) }
        2    -> allOrders.filter { it.status == "Доставлен" || it.status == "Отменён" }
        else -> allOrders
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .statusBarsPadding(),
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
                            indication = null,
                            onClick = onBack,
                        ),
                    contentAlignment = Alignment.Center,
                ) { BackIcon(tint = colors.ink) }
                Column {
                    Text(
                        text = "Мои заказы",
                        style = TextStyle(
                            fontFamily = FrauncesFontFamily, fontSize = 22.sp,
                            fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em,
                            color = colors.ink,
                        ),
                    )
                    if (allOrders.isNotEmpty()) {
                        Text(
                            text = "${allOrders.size} заказов",
                            style = TextStyle(fontSize = 12.sp, color = colors.ink3),
                            modifier = Modifier.padding(top = 1.dp),
                        )
                    }
                }
            }
        }

        // ── Tabs ──
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(tabs.size) { i ->
                    val active = selectedTab == i
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (active) colors.ink else colors.surface)
                            .border(1.dp, if (active) colors.ink else colors.line, RoundedCornerShape(999.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { selectedTab = i }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = tabs[i],
                            style = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (active) colors.bg else colors.ink2,
                            ),
                        )
                    }
                }
            }
        }

        // ── Empty state ──
        if (shown.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(colors.surface2),
                        contentAlignment = Alignment.Center,
                    ) { BagIcon(tint = colors.ink3) }
                    Text(
                        text = if (selectedTab == 1) "Нет активных заказов" else "Заказов пока нет",
                        style = TextStyle(
                            fontFamily = FrauncesFontFamily,
                            fontSize = 20.sp, fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.02).em, color = colors.ink,
                        ),
                        modifier = Modifier.padding(top = 16.dp),
                    )
                    Text(
                        text = if (selectedTab == 1) "Все ваши заказы уже доставлены"
                        else "Оформите первый заказ — доставим\nсвежие продукты за 40 минут",
                        style = TextStyle(
                            fontSize = 14.sp, color = colors.ink3,
                            lineHeight = (14 * 1.5).sp,
                        ),
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        }

        // ── Active orders section ──
        val activeOrders = shown.filter { isActive(it.status) }
        val historyOrders = shown.filter { !isActive(it.status) }

        if (activeOrders.isNotEmpty()) {
            if (selectedTab == 0) {
                item {
                    Text(
                        text = "АКТИВНЫЕ",
                        style = TextStyle(
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.08.em, color = colors.ink3,
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 8.dp),
                    )
                }
            }
            items(activeOrders) { order ->
                ActiveOrderCard(
                    order = order,
                    colors = colors,
                    onTracking = onTracking,
                )
            }
        }

        // ── History section ──
        if (historyOrders.isNotEmpty()) {
            if (selectedTab == 0 && activeOrders.isNotEmpty()) {
                item {
                    Text(
                        text = "ИСТОРИЯ",
                        style = TextStyle(
                            fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.08.em, color = colors.ink3,
                        ),
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 12.dp, bottom = 8.dp),
                    )
                }
            }
            items(historyOrders) { order ->
                HistoryOrderCard(order = order, colors = colors)
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp).navigationBarsPadding()) }
    }
}

@Composable
private fun ActiveOrderCard(
    order: Order,
    colors: AppColors,
    onTracking: () -> Unit,
) {
    val stage = stageIndex(order.status)
    val hues = remember(order.id) {
        val seed = order.id.hashCode()
        listOf(
            ((seed * 137) % 360).let { if (it < 0) it + 360 else it }.toFloat(),
            ((seed * 211) % 360).let { if (it < 0) it + 360 else it }.toFloat(),
            ((seed * 317) % 360).let { if (it < 0) it + 360 else it }.toFloat(),
            ((seed * 431) % 360).let { if (it < 0) it + 360 else it }.toFloat(),
        )
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(1.dp, colors.line, RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

            // Top row: number + status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = order.number,
                        style = TextStyle(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.ink,
                        ),
                    )
                    Text(
                        text = order.date,
                        style = TextStyle(fontSize = 12.sp, color = colors.ink3),
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                StatusBadge(status = order.status, colors = colors)
            }

            // Product color chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                hues.take(4).forEach { hue ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(hueColor(hue, 0.28f, 0.93f)),
                    )
                }
                if (order.items > 4) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(colors.surface2),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "+${order.items - 4}",
                            style = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = colors.ink3,
                            ),
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${order.total} ₽",
                    style = TextStyle(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = colors.ink,
                    ),
                )
            }

            // Stage track
            OrderStageTrack(currentStage = stage, colors = colors)

            // ETA strip
            if (order.status == "В пути") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(11.dp))
                        .background(colors.accentSoft)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DeliveryIcon(tint = colors.accentDeep, size = 20.dp)
                    Text(
                        text = "Прибудет примерно через 31 мин",
                        style = TextStyle(
                            fontSize = 13.sp, fontWeight = FontWeight.Medium,
                            color = colors.accentDeep,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            if (order.status == "Собирается") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(11.dp))
                        .background(colors.surface2)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    RepeatIcon(tint = colors.ink3, size = 18.dp)
                    Text(
                        text = "Курьер заберёт заказ в течение 15 мин",
                        style = TextStyle(fontSize = 13.sp, color = colors.ink2),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Action button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.ink)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onTracking,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DeliveryIcon(tint = colors.bg, size = 18.dp)
                    Text(
                        text = "Отследить заказ",
                        style = TextStyle(
                            fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.bg,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryOrderCard(
    order: Order,
    colors: AppColors,
) {
    val delivered = order.status == "Доставлен"
    val hues = remember(order.id) {
        val seed = order.id.hashCode()
        listOf(
            ((seed * 137) % 360).let { if (it < 0) it + 360 else it }.toFloat(),
            ((seed * 211) % 360).let { if (it < 0) it + 360 else it }.toFloat(),
            ((seed * 317) % 360).let { if (it < 0) it + 360 else it }.toFloat(),
        )
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.line, RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            // Top row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Small hue chips
                    hues.take(3).forEach { hue ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(hueColor(hue, 0.28f, if (delivered) 0.93f else 0.88f)),
                        )
                    }
                    if (order.items > 3) {
                        Text(
                            text = "+${order.items - 3}",
                            style = TextStyle(fontSize = 12.sp, color = colors.ink3),
                        )
                    }
                }
                StatusBadge(status = order.status, colors = colors)
            }

            // Info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        text = order.number,
                        style = TextStyle(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.ink,
                        ),
                    )
                    Text(
                        text = "${order.date} · ${order.items} товаров",
                        style = TextStyle(fontSize = 12.sp, color = colors.ink3),
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                Text(
                    text = "${order.total} ₽",
                    style = TextStyle(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = colors.ink,
                    ),
                )
            }

            // Repeat button (for delivered orders)
            if (delivered) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, colors.line, RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {}
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RepeatIcon(tint = colors.ink2, size = 16.dp)
                    Text(
                        text = "Повторить заказ",
                        style = TextStyle(
                            fontSize = 13.sp, fontWeight = FontWeight.Medium, color = colors.ink2,
                        ),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ChevronIcon(tint = colors.ink3, size = 16.dp)
                }
            }
        }
    }
}

@Composable
private fun OrderStageTrack(currentStage: Int, colors: AppColors) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ORDER_STAGES.forEachIndexed { i, _ ->
                val done = i <= currentStage
                val active = i == currentStage

                Box(
                    modifier = Modifier
                        .size(if (active) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                active -> colors.accent
                                done   -> colors.accent
                                else   -> colors.line2
                            }
                        ),
                )
                if (i < ORDER_STAGES.size - 1) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(
                                if (i < currentStage) colors.accent else colors.line2,
                            ),
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            ORDER_STAGES.forEachIndexed { i, label ->
                Text(
                    text = label,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = if (i == currentStage) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (i <= currentStage) colors.accent else colors.ink3,
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = when (i) {
                        0                        -> androidx.compose.ui.text.style.TextAlign.Start
                        ORDER_STAGES.size - 1    -> androidx.compose.ui.text.style.TextAlign.End
                        else                     -> androidx.compose.ui.text.style.TextAlign.Center
                    },
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String, colors: AppColors) {
    val (bg, fg) = when (status) {
        "В пути"      -> colors.accentSoft to colors.accentDeep
        "Собирается"  -> Color(0xFFFFF3D6) to Color(0xFF7A5A00)
        "Отменён"     -> Color(0xFFFFEDE8) to colors.danger
        else          -> colors.surface2 to colors.ink3
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (status == "Доставлен") {
            CheckIcon(tint = fg, size = 13.dp)
        } else if (isActive(status)) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(fg),
            )
        }
        Text(
            text = status,
            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = fg),
        )
    }
}
