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
import com.example.gradka.SupportChatViewModel
import com.example.gradka.domain.SupportMessage
import com.example.gradka.domain.SupportMessageAuthor
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SupportChatScreen(
    vm: SupportChatViewModel,
    onBack: () -> Unit,
) {
    val colors = LocalAppColors.current
    val state by vm.uiState.collectAsState()
    var inputFocused by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val quickReplies = remember {
        listOf(
            "Где мой заказ?",
            "Заменить товар",
            "Не работает оплата",
            "Вернуть товар",
        )
    }

    LaunchedEffect(state.messages.size, state.operatorTyping) {
        val lastIndex = state.messages.lastIndex + if (state.operatorTyping) 1 else 0
        if (lastIndex >= 0) {
            listState.animateScrollToItem(lastIndex)
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
                            text = if (state.operatorTyping) "печатает…" else "онлайн · обычно отвечает быстро",
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
                items(state.messages.size) { index ->
                    val msg = state.messages[index]
                    val prev = state.messages.getOrNull(index - 1)
                    val grouped = prev?.author == msg.author
                    Spacer(modifier = Modifier.height(if (grouped) 0.dp else 4.dp))
                    ChatBubble(msg = msg, colors = colors, grouped = grouped)
                }
                if (state.operatorTyping) {
                    item { TypingBubble(colors = colors) }
                }
            }

            // ── Quick replies ──
            AnimatedVisibility(
                visible = state.messages.size <= 1,
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
	                                ) { vm.sendQuickReply(q) }
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
	                        value = state.input,
	                        onValueChange = vm::onInputChange,
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
	                                    if (state.input.isEmpty()) {
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

	                    val canSend = state.input.trim().isNotEmpty()
	                    Box(
	                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (canSend) colors.ink else colors.surface2)
                            .clickable(
	                                enabled = canSend,
	                                interactionSource = remember { MutableInteractionSource() },
	                                indication = null,
	                            ) { vm.sendInput() },
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
private fun ChatBubble(msg: SupportMessage, colors: AppColors, grouped: Boolean) {
    val fromUser = msg.author == SupportMessageAuthor.USER
    val time = remember(msg.createdAtMillis) { formatMessageTime(msg.createdAtMillis) }
    val alignment = if (fromUser) Alignment.CenterEnd else Alignment.CenterStart
    val bg = if (fromUser) colors.ink else colors.surface
    val textColor = if (fromUser) colors.bg else colors.ink
    val timeColor = if (fromUser) colors.bg.copy(alpha = 0.55f) else colors.ink3
    val shape = if (fromUser) {
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
            horizontalAlignment = if (fromUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(bg)
                    .then(
                        if (!fromUser) Modifier.border(1.dp, colors.line, shape)
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
                text = time,
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

private fun formatMessageTime(createdAtMillis: Long): String {
    val now = Calendar.getInstance()
    val messageDate = Calendar.getInstance().apply { timeInMillis = createdAtMillis }
    val pattern = if (
        now.get(Calendar.YEAR) == messageDate.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == messageDate.get(Calendar.DAY_OF_YEAR)
    ) {
        "HH:mm"
    } else {
        "dd.MM HH:mm"
    }
    return SimpleDateFormat(pattern, Locale.forLanguageTag("ru-RU")).format(Date(createdAtMillis))
}
