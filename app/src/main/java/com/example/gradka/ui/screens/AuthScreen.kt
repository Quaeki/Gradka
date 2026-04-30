package com.example.gradka.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradka.*
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*
import kotlinx.coroutines.delay

private fun formatPhone(digits: String): String {
    var s = ""
    if (digits.isNotEmpty()) s += "(${digits.take(3)}"
    if (digits.length >= 3) s += ") "
    if (digits.length > 3) s += digits.substring(3, minOf(6, digits.length))
    if (digits.length >= 6) s += "-"
    if (digits.length > 6) s += digits.substring(6, minOf(8, digits.length))
    if (digits.length >= 8) s += "-"
    if (digits.length > 8) s += digits.substring(8, minOf(10, digits.length))
    return s
}

@Composable
fun AuthScreen(onAuthDone: () -> Unit) {
    val context = LocalContext.current
    val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(context.applicationContext as Application))
    val state by vm.state.collectAsState()
    val colors = LocalAppColors.current

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onAuthDone()
    }

    LaunchedEffect(Unit) {
        delay(2000L)
        if (vm.state.value.screen == AuthStep.SPLASH) {
            vm.onEvent(AuthEvent.GoToWelcome)
        }
    }

    AnimatedContent(
        targetState = state.screen,
        transitionSpec = {
            (fadeIn(tween(280)) + slideInVertically { it / 12 }).togetherWith(fadeOut(tween(200)))
        },
        label = "auth_screen",
    ) { screen ->
        when (screen) {
            AuthStep.SPLASH -> SplashContent(colors)
            AuthStep.WELCOME -> WelcomeContent(state, vm::onEvent, colors)
            AuthStep.PHONE -> PhoneContent(state, vm::onEvent, colors)
            AuthStep.OTP -> OtpContent(state, vm::onEvent, colors)
            AuthStep.NAME -> NameContent(state, vm::onEvent, colors)
            AuthStep.SUCCESS -> AuthSuccessContent(state, onAuthDone, colors)
            AuthStep.RECOVERY -> RecoveryContent(state, vm::onEvent, colors)
        }
    }
}

// ── 01 Splash ──────────────────────────────────────────────────────────────

@Composable
private fun SplashContent(colors: AppColors) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 250f),
        label = "splash_scale",
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "splash_alpha",
    )
    val blink = rememberInfiniteTransition(label = "blink")
    val b1 by blink.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse), label = "b1")
    val b2 by blink.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600, 200, LinearEasing), RepeatMode.Reverse), label = "b2")
    val b3 by blink.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600, 400, LinearEasing), RepeatMode.Reverse), label = "b3")

    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier.fillMaxSize().background(colors.ink),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width + 100.dp.toPx()
            val cy = size.height + 100.dp.toPx()
            drawCircle(Color.White.copy(alpha = 0.06f), 220.dp.toPx(), Offset(cx, cy), style = Stroke(1.dp.toPx()))
            drawCircle(Color.White.copy(alpha = 0.08f), 140.dp.toPx(), Offset(cx, cy), style = Stroke(1.dp.toPx()))
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha },
        ) {
            Logo(size = 36.dp, color = colors.bg, colors = colors)
            Spacer(Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(b1, b2, b3).forEach { a ->
                    Box(Modifier.size(6.dp).clip(CircleShape).background(colors.bg.copy(alpha = a)))
                }
            }
        }
    }
}

// ── 02 Welcome ─────────────────────────────────────────────────────────────

@Composable
private fun WelcomeContent(state: AuthState, onEvent: (AuthEvent) -> Unit, colors: AppColors) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.62f).background(colors.ink),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                drawCircle(Color.White.copy(0.08f), 160.dp.toPx(), Offset(cx, cy), style = Stroke(1.dp.toPx()))
                drawCircle(Color.White.copy(0.08f), 110.dp.toPx(), Offset(cx, cy), style = Stroke(1.dp.toPx()))
                drawCircle(Color(0xFF5A8848).copy(0.18f), 65.dp.toPx(), Offset(cx, cy))
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 32.dp).padding(bottom = 40.dp),
            ) {
                Logo(size = 28.dp, color = colors.bg, colors = colors)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Свежее — прямо\nс грядки",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = FrauncesFontFamily,
                        fontSize = 32.sp, fontWeight = FontWeight.Medium,
                        letterSpacing = (-0.025).em, lineHeight = (32 * 1.05).sp, color = colors.bg,
                    ),
                )
                Text(
                    text = "Фермерские продукты за 40 минут",
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 14.sp, color = colors.bg.copy(0.6f), lineHeight = (14 * 1.55).sp),
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(colors.bg)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp)
                .navigationBarsPadding(),
        ) {
            Box(
                Modifier.size(36.dp, 4.dp).clip(RoundedCornerShape(2.dp))
                    .background(colors.line2).align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(24.dp))
            AuthButton(text = "Войти", bg = colors.ink, textColor = colors.bg) {
                onEvent(AuthEvent.SelectMode(AuthMode.LOGIN))
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth().height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, colors.line2, RoundedCornerShape(16.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        onEvent(AuthEvent.SelectMode(AuthMode.REGISTER))
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("Создать аккаунт", style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.ink))
            }
            Spacer(Modifier.height(20.dp))
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = colors.ink3)) { append("Уже есть аккаунт? ") }
                    withStyle(SpanStyle(color = colors.ink, textDecoration = TextDecoration.Underline)) {
                        append("Забыли доступ?")
                    }
                },
                style = TextStyle(fontSize = 12.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                        onEvent(AuthEvent.GoToRecovery)
                    },
            )
        }
    }
}

// ── 03 Phone ───────────────────────────────────────────────────────────────

@Composable
private fun PhoneContent(
    state: AuthState,
    onEvent: (AuthEvent) -> Unit,
    colors: AppColors,
    isRecovery: Boolean = false,
) {
    val badgeText = when {
        isRecovery -> "ВОССТАНОВЛЕНИЕ"
        state.mode == AuthMode.REGISTER -> "РЕГИСТРАЦИЯ"
        else -> "ВХОД"
    }
    val badgeColor = if (isRecovery) colors.danger else colors.accentDeep
    val blink = rememberInfiniteTransition(label = "phone_cursor")
    val cursorAlpha by blink.animateFloat(
        1f, 0f, infiniteRepeatable(keyframes { durationMillis = 1000; 1f at 500 }, RepeatMode.Restart), label = "ca",
    )

    Column(
        modifier = Modifier.fillMaxSize().background(colors.bg).statusBarsPadding(),
    ) {
        BackBtn(modifier = Modifier.padding(start = 20.dp, top = 14.dp), colors = colors) { onEvent(AuthEvent.Back) }

        Column(modifier = Modifier.padding(top = 22.dp, start = 24.dp, end = 24.dp)) {
            AuthBadge(badgeText, badgeColor)
            Text(
                "Введите номер\nтелефона",
                style = TextStyle(
                    fontFamily = FrauncesFontFamily, fontSize = 30.sp, fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.025).em, lineHeight = (30 * 1.1).sp, color = colors.ink,
                ),
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                "Пришлём код подтверждения",
                style = TextStyle(fontSize = 14.sp, color = colors.ink2, lineHeight = (14 * 1.5).sp),
                modifier = Modifier.padding(top = 8.dp),
            )
            Spacer(Modifier.height(24.dp))

            val borderColor = when {
                state.phoneError.isNotEmpty() -> colors.danger
                state.phone.isNotEmpty() -> colors.ink
                else -> colors.line
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth().height(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                    .background(colors.surface),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("🇷🇺", fontSize = 20.sp)
                    Text("+7", style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.ink))
                }
                Spacer(Modifier.width(1.dp).fillMaxHeight(0.55f).background(colors.line))
                Box(
                    modifier = Modifier.weight(1f).padding(start = 14.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (state.phone.isEmpty()) {
                        Box(Modifier.width(2.dp).height(34.dp).clip(RoundedCornerShape(1.dp)).background(colors.ink.copy(alpha = cursorAlpha)))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                formatPhone(state.phone),
                                style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = colors.ink),
                            )
                            if (state.phone.length < 10) {
                                Box(
                                    Modifier.padding(start = 2.dp).width(2.dp).height(34.dp)
                                        .clip(RoundedCornerShape(1.dp)).background(colors.ink.copy(alpha = cursorAlpha)),
                                )
                            }
                        }
                    }
                }
            }

            if (state.phoneError.isNotEmpty()) {
                Text(
                    state.phoneError,
                    style = TextStyle(fontSize = 13.sp, color = colors.danger),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        Spacer(Modifier.weight(1f))
        NumPad(
            onDigit = { onEvent(AuthEvent.PhoneDigit(it)) },
            onDelete = { onEvent(AuthEvent.PhoneDelete) },
            onDone = { onEvent(AuthEvent.PhoneSubmit) },
            doneEnabled = state.phone.length == 10 && state.phoneError.isEmpty(),
            colors = colors,
        )
    }
}

// ── 04 OTP ─────────────────────────────────────────────────────────────────

@Composable
private fun OtpContent(
    state: AuthState,
    onEvent: (AuthEvent) -> Unit,
    colors: AppColors,
    isRecovery: Boolean = false,
) {
    val badgeColor = if (isRecovery) colors.danger else colors.accentDeep
    val badgeText = when {
        isRecovery -> "ВОССТАНОВЛЕНИЕ"
        state.mode == AuthMode.REGISTER -> "РЕГИСТРАЦИЯ"
        else -> "ВХОД"
    }
    val blink = rememberInfiniteTransition(label = "otp_cursor")
    val cursorAlpha by blink.animateFloat(
        1f, 0f, infiniteRepeatable(keyframes { durationMillis = 1000; 1f at 500 }, RepeatMode.Restart), label = "ca",
    )
    val spinnerRotation by rememberInfiniteTransition(label = "spin").animateFloat(
        0f, 360f, infiniteRepeatable(tween(800, easing = LinearEasing)), label = "rot",
    )

    Column(
        modifier = Modifier.fillMaxSize().background(colors.bg).statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackBtn(colors = colors) { onEvent(AuthEvent.Back) }
            Row(
                modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    onEvent(AuthEvent.EditPhone)
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    "+7 ${formatPhone(state.phone)}",
                    style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.ink),
                )
                EditIcon(tint = colors.ink3, size = 14.dp)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            AuthBadge(badgeText, badgeColor)
            Text(
                "Введите код\nподтверждения",
                style = TextStyle(
                    fontFamily = FrauncesFontFamily, fontSize = 30.sp, fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.025).em, lineHeight = (30 * 1.1).sp, color = colors.ink,
                ),
                modifier = Modifier.padding(top = 8.dp),
            )
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                repeat(6) { i ->
                    OtpCell(
                        modifier = Modifier.weight(1f),
                        digit = state.otp.getOrNull(i)?.toString() ?: "",
                        isActive = i == state.otp.length && !state.otpError && !state.otpChecking,
                        isError = state.otpError,
                        cursorAlpha = cursorAlpha,
                        colors = colors,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                when {
                    state.otpChecking -> {
                        Canvas(modifier = Modifier.size(20.dp).graphicsLayer { rotationZ = spinnerRotation }) {
                            drawArc(
                                color = colors.accent,
                                startAngle = 0f, sweepAngle = 270f, useCenter = false,
                                style = Stroke(2.dp.toPx(), cap = StrokeCap.Round),
                            )
                        }
                    }
                    state.otpError -> Text(
                        "Неверный код. Попробуйте ещё раз",
                        style = TextStyle(fontSize = 13.sp, color = colors.danger),
                    )
                    state.otpCountdown > 0 -> Text(
                        "Повторить через ${state.otpCountdown} с",
                        style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.ink3),
                    )
                    else -> Row(
                        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            onEvent(AuthEvent.OtpResend)
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        RepeatIcon(tint = colors.ink2, size = 14.dp)
                        Text("Отправить снова", style = TextStyle(fontSize = 13.sp, color = colors.ink2))
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        NumPad(
            onDigit = { if (!state.otpChecking) onEvent(AuthEvent.OtpDigit(it)) },
            onDelete = { if (!state.otpChecking) onEvent(AuthEvent.OtpDelete) },
            onDone = {},
            doneEnabled = false,
            colors = colors,
        )
    }
}

@Composable
private fun OtpCell(
    modifier: Modifier = Modifier,
    digit: String,
    isActive: Boolean,
    isError: Boolean,
    cursorAlpha: Float,
    colors: AppColors,
) {
    val borderColor = when {
        isError -> colors.danger
        digit.isNotEmpty() || isActive -> colors.ink
        else -> colors.line
    }
    val bgColor = if (isError) Color(0xFFFFF0EE) else colors.surface

    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        when {
            digit.isNotEmpty() -> Text(digit, style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 28.sp, fontWeight = FontWeight.SemiBold, color = colors.ink))
            isActive -> Box(Modifier.width(2.dp).height(34.dp).clip(RoundedCornerShape(1.dp)).background(colors.ink.copy(alpha = cursorAlpha)))
        }
    }
}

// ── 05 Name ────────────────────────────────────────────────────────────────

@Composable
private fun NameContent(state: AuthState, onEvent: (AuthEvent) -> Unit, colors: AppColors) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val avatarBg by animateColorAsState(
        if (state.name.isNotBlank()) colors.accentSoft else colors.surface2,
        label = "avatar_bg",
    )

    Column(
        modifier = Modifier.fillMaxSize().background(colors.bg).statusBarsPadding(),
    ) {
        BackBtn(modifier = Modifier.padding(start = 20.dp, top = 14.dp), colors = colors) { onEvent(AuthEvent.Back) }

        Column(
            modifier = Modifier.padding(top = 22.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(avatarBg).border(2.dp, colors.line, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (state.name.isNotBlank()) {
                    Text(
                        state.name.first().uppercaseChar().toString(),
                        style = TextStyle(fontFamily = FrauncesFontFamily, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colors.accentDeep),
                    )
                } else {
                    UserIcon(tint = colors.ink3, size = 28.dp)
                }
            }

            Spacer(Modifier.height(28.dp))
            AuthBadge("РЕГИСТРАЦИЯ", colors.accentDeep)
            Text(
                "Как вас зовут?",
                style = TextStyle(
                    fontFamily = FrauncesFontFamily, fontSize = 30.sp, fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.025).em, lineHeight = (30 * 1.1).sp, color = colors.ink,
                ),
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(Modifier.height(24.dp))

            BasicTextField(
                value = state.name,
                onValueChange = { onEvent(AuthEvent.NameInput(it)) },
                textStyle = TextStyle(fontSize = 18.sp, color = colors.ink),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (state.name.isNotBlank()) onEvent(AuthEvent.NameSubmit) }),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                decorationBox = { inner ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth().height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.surface)
                            .border(1.5.dp, if (state.name.isNotEmpty()) colors.ink else colors.line, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.fillMaxWidth()) {
                            if (state.name.isEmpty()) {
                                Text("Например, Анна", style = TextStyle(fontSize = 18.sp, color = colors.ink3))
                            }
                            inner()
                        }
                    }
                },
            )

            Spacer(Modifier.height(16.dp))
            val hasName = state.name.isNotBlank()
            AuthButton(
                text = "Продолжить",
                bg = if (hasName) colors.ink else colors.surface2,
                textColor = if (hasName) colors.bg else colors.ink3,
                enabled = hasName,
            ) { onEvent(AuthEvent.NameSubmit) }
        }
    }
}

// ── 06 Success ─────────────────────────────────────────────────────────────

@Composable
private fun AuthSuccessContent(state: AuthState, onAuthDone: () -> Unit, colors: AppColors) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (visible) 1f else 0.6f,
        spring(dampingRatio = 0.4f, stiffness = 300f),
        label = "success_scale",
    )
    LaunchedEffect(Unit) { visible = true }

    val isNew = state.mode == AuthMode.REGISTER
    val title = if (isNew) "Привет, ${state.name.ifBlank { "" }}!" else "С возвращением!"
    val body = if (isNew) "Аккаунт создан. Готовы делать первый заказ?" else "Вы успешно вошли в аккаунт."

    data class Feature(val emoji: String, val title: String, val sub: String)
    val features = listOf(
        Feature("🥕", "Всегда свежее", "Доставка день в день с фермы"),
        Feature("⏱", "40 минут", "Быстрая доставка по городу"),
        Feature("❤️", "Избранное", "Сохраняйте любимые продукты"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize().background(colors.bg).statusBarsPadding().navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier.size(100.dp).graphicsLayer { scaleX = scale; scaleY = scale },
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(100.dp).clip(CircleShape).background(colors.accentSoft))
            Box(Modifier.size(62.dp).clip(CircleShape).background(colors.accent), contentAlignment = Alignment.Center) {
                CheckIcon(tint = Color.White, size = 30.dp)
            }
        }

        Spacer(Modifier.height(28.dp))
        Text(
            title,
            style = TextStyle(
                fontFamily = FrauncesFontFamily, fontSize = 30.sp, fontWeight = FontWeight.Medium,
                letterSpacing = (-0.025).em, color = colors.ink,
            ),
            textAlign = TextAlign.Center,
        )
        Text(
            body,
            style = TextStyle(fontSize = 15.sp, color = colors.ink2, lineHeight = (15 * 1.5).sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp),
        )

        if (isNew) {
            Spacer(Modifier.height(24.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                features.forEach { f ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(colors.surface)
                            .border(1.dp, colors.line, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(f.emoji, fontSize = 22.sp)
                        Column {
                            Text(f.title, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink))
                            Text(f.sub, style = TextStyle(fontSize = 12.sp, color = colors.ink3))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        AuthButton(
            text = if (isNew) "Начать покупки" else "Перейти к покупкам",
            bg = colors.ink,
            textColor = colors.bg,
        ) { onAuthDone() }
    }
}

// ── 07 Recovery ────────────────────────────────────────────────────────────

@Composable
private fun RecoveryContent(state: AuthState, onEvent: (AuthEvent) -> Unit, colors: AppColors) {
    AnimatedContent(
        targetState = state.recoveryStep,
        transitionSpec = { (fadeIn(tween(280)) + slideInVertically { it / 12 }).togetherWith(fadeOut(tween(200))) },
        label = "recovery_step",
    ) { step ->
        when (step) {
            0 -> PhoneContent(state, onEvent, colors, isRecovery = true)
            1 -> OtpContent(state, onEvent, colors, isRecovery = true)
            else -> RecoveryDoneContent(colors, onEvent)
        }
    }
}

@Composable
private fun RecoveryDoneContent(colors: AppColors, onEvent: (AuthEvent) -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (visible) 1f else 0.6f, spring(0.4f, 300f), label = "rdone_scale")
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize().background(colors.bg).statusBarsPadding().navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(80.dp).graphicsLayer { scaleX = scale; scaleY = scale },
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(80.dp).clip(CircleShape).background(colors.accentSoft))
            Box(Modifier.size(50.dp).clip(CircleShape).background(colors.accent), contentAlignment = Alignment.Center) {
                CheckIcon(tint = Color.White, size = 24.dp)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Готово!",
            style = TextStyle(
                fontFamily = FrauncesFontFamily, fontSize = 26.sp, fontWeight = FontWeight.Medium,
                letterSpacing = (-0.025).em, color = colors.ink,
            ),
        )
        Text(
            "Доступ к аккаунту восстановлен.\nВойдите с новым кодом.",
            style = TextStyle(fontSize = 14.sp, color = colors.ink2, lineHeight = (14 * 1.55).sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp),
        )
        Spacer(Modifier.height(32.dp))
        AuthButton("Войти", colors.ink, colors.bg) { onEvent(AuthEvent.SelectMode(AuthMode.LOGIN)) }
    }
}

// ── Shared UI helpers ──────────────────────────────────────────────────────

@Composable
private fun BackBtn(modifier: Modifier = Modifier, colors: AppColors, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .border(1.dp, colors.line, CircleShape)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        BackIcon(tint = colors.ink, size = 20.dp)
    }
}

@Composable
private fun AuthBadge(text: String, color: Color) {
    Text(text, style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.1.em, color = color))
}

@Composable
private fun AuthButton(
    text: String,
    bg: Color,
    textColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth().height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = textColor))
    }
}