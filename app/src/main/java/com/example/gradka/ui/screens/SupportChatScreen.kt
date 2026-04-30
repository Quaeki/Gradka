package com.example.gradka.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*
import kotlinx.coroutines.delay

private data class ChatMsg(
    val id: Int,
    val text: String,
    val fromUser: Boolean,
    val time: String,
)

@Composable
fun SupportChatScreen(
    onBack: () -> Unit,
) {
    val colors = LocalAppColors.current

    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMsg(
                    id = 1,
                    text = "Здравствуйте! Я Аня, оператор поддержки Грядки. Чем могу помочь?",
                    fromUser = false,
                    time = "сейчас",
                ),
            )
        )
    }
    var input by remember { mutableStateOf("") }
    var inputFocused by remember { mutableStateOf(false) }
    var operatorTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val quickReplies = listOf(
        "Где мой заказ?",
        "Заменить товар",
        "Не работает оплата",
        "Вернуть товар",
    )

    fun send(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val nextId = (messages.maxOfOrNull { it.id } ?: 0) + 1
        messages = messages + ChatMsg(
            id = nextId,
            text = trimmed,
            fromUser = true,
            time = "сейчас",
        )
        input = ""
        operatorTyping = true
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(operatorTyping) {
        if (operatorTyping) {
            delay(1400)
            val nextId = (messages.maxOfOrNull { it.id } ?: 0) + 1
            val last = messages.lastOrNull { it.fromUser }?.text.orEmpty()
            val reply = autoReply(last)
            messages = messages + ChatMsg(
                id = nextId,
                text = reply,
                fromUser = false,
                time = "сейчас",
            )
            operatorTyping = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bg)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp)
                    .padding(top = 8.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
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

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.accentSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "А",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accentDeep,
                        ),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Поддержка",
                        style = TextStyle(
                            fontFamily = FrauncesFontFamily, fontSize = 18.sp,
                            fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.ink,
                        ),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(colors.accent),
                        )
                        Text(
                            text = if (operatorTyping) "печатает…" else "онлайн · обычно отвечает быстро",
                            style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.surface2)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {},
                    contentAlignment = Alignment.Center,
                ) { PhoneCallIcon(tint = colors.ink2, size = 18.dp) }
            }

            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.line))

            // ── Messages ──
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    DateChip(text = "Сегодня", colors = colors)
                    Spacer(modifier = Modifier.height(6.dp))
                }
                items(messages.size) { index ->
                    val msg = messages[index]
                    val prev = messages.getOrNull(index - 1)
                    val grouped = prev?.fromUser == msg.fromUser
                    Spacer(modifier = Modifier.height(if (grouped) 0.dp else 4.dp))
                    ChatBubble(msg = msg, colors = colors, grouped = grouped)
                }
                if (operatorTyping) {
                    item { TypingBubble(colors = colors) }
                }
            }

            // ── Quick replies ──
            AnimatedVisibility(
                visible = messages.size <= 1,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(150)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    quickReplies.forEach { q ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(colors.surface)
                                .border(1.dp, colors.line, RoundedCornerShape(999.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { send(q) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = q,
                                style = TextStyle(fontSize = 12.sp, color = colors.ink2),
                            )
                        }
                    }
                }
            }

            // ── Input ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bg)
                    .border(width = 1.dp, color = colors.line, shape = RoundedCornerShape(0.dp))
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(colors.surface2)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {},
                        contentAlignment = Alignment.Center,
                    ) { PlusIcon(tint = colors.ink2, size = 18.dp) }

                    BasicTextField(
                        value = input,
                        onValueChange = { input = it },
                        textStyle = TextStyle(fontSize = 15.sp, color = colors.ink),
                        cursorBrush = SolidColor(colors.ink),
                        decorationBox = { inner ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 40.dp, max = 120.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(colors.surface)
                                    .border(
                                        1.dp,
                                        if (inputFocused) colors.ink else colors.line,
                                        RoundedCornerShape(20.dp),
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    if (input.isEmpty()) {
                                        Text(
                                            text = "Сообщение…",
                                            style = TextStyle(fontSize = 15.sp, color = colors.ink3),
                                        )
                                    }
                                    inner()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { inputFocused = it.isFocused },
                    )

                    val canSend = input.trim().isNotEmpty()
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (canSend) colors.ink else colors.surface2)
                            .clickable(
                                enabled = canSend,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { send(input) },
                        contentAlignment = Alignment.Center,
                    ) {
                        SendIcon(
                            tint = if (canSend) colors.bg else colors.ink3,
                            size = 18.dp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateChip(text: String, colors: AppColors) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(colors.surface2)
                .padding(horizontal = 12.dp, vertical = 5.dp),
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.ink3,
                    letterSpacing = 0.04.em,
                ),
            )
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMsg, colors: AppColors, grouped: Boolean) {
    val alignment = if (msg.fromUser) Alignment.CenterEnd else Alignment.CenterStart
    val bg = if (msg.fromUser) colors.ink else colors.surface
    val textColor = if (msg.fromUser) colors.bg else colors.ink
    val timeColor = if (msg.fromUser) colors.bg.copy(alpha = 0.55f) else colors.ink3
    val shape = if (msg.fromUser) {
        RoundedCornerShape(
            topStart = 18.dp, topEnd = 18.dp,
            bottomStart = 18.dp,
            bottomEnd = if (grouped) 18.dp else 4.dp,
        )
    } else {
        RoundedCornerShape(
            topStart = 18.dp, topEnd = 18.dp,
            bottomStart = if (grouped) 18.dp else 4.dp,
            bottomEnd = 18.dp,
        )
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(
            horizontalAlignment = if (msg.fromUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(bg)
                    .then(
                        if (!msg.fromUser) Modifier.border(1.dp, colors.line, shape)
                        else Modifier
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    text = msg.text,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = textColor,
                        lineHeight = (14 * 1.4).sp,
                    ),
                )
            }
            Text(
                text = msg.time,
                style = TextStyle(fontSize = 10.sp, color = timeColor),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            )
        }
    }
}

@Composable
private fun TypingBubble(colors: AppColors) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp))
                .background(colors.surface)
                .border(
                    1.dp, colors.line,
                    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp),
                )
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(colors.ink3),
                )
            }
        }
    }
}

@Composable
private fun SendIcon(tint: androidx.compose.ui.graphics.Color, size: androidx.compose.ui.unit.Dp = 18.dp) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(s * 0.10f, s * 0.50f)
            lineTo(s * 0.90f, s * 0.10f)
            lineTo(s * 0.65f, s * 0.50f)
            lineTo(s * 0.90f, s * 0.90f)
            close()
        }
        drawPath(
            path = path,
            color = tint,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1.6.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round,
            ),
        )
        drawPath(path = path, color = tint)
    }
}

private fun autoReply(question: String): String {
    val q = question.lowercase()
    return when {
        q.contains("заказ") && (q.contains("где") || q.contains("статус")) ->
            "Сейчас уточню по вашему заказу. Курьер обычно выходит на связь за 10 минут до доставки."
        q.contains("замен") -> "Если товара не окажется, курьер предложит замену прямо в чате. Хотите указать предпочтения?"
        q.contains("оплат") -> "Подскажите, какой способ оплаты выбран? Иногда помогает попробовать другую карту или СБП."
        q.contains("верн") || q.contains("возврат") ->
            "Конечно, оформим возврат. Опишите, что не так с товаром, и при возможности приложите фото."
        q.contains("спасибо") || q.contains("ок") -> "Рада помочь! Если что-то ещё — пишите."
        else -> "Поняла вас. Сейчас передам коллегам и вернусь с ответом в течение нескольких минут."
    }
}
