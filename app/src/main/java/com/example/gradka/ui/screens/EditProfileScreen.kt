package com.example.gradka.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradka.AppViewModel
import com.example.gradka.AuthViewModel
import com.example.gradka.AuthViewModelFactory
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

private val GENDERS = listOf("Мужской", "Женский", "Не указан")

@Composable
fun EditProfileScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current.applicationContext as Application)),
) {
    val colors = LocalAppColors.current
    val authState by authVm.state.collectAsState()

    var name by remember { mutableStateOf(authState.name) }
    var email by remember { mutableStateOf(vm.profileEmail) }
    var birthday by remember { mutableStateOf(vm.profileBirthday) }
    var gender by remember { mutableStateOf(vm.profileGender.ifEmpty { GENDERS.last() }) }

    val rawPhone = authState.phone
    val phoneFormatted = if (rawPhone.length == 10)
        "+7 (${rawPhone.take(3)}) ${rawPhone.substring(3, 6)}-${rawPhone.substring(6, 8)}-${rawPhone.substring(8, 10)}"
    else if (rawPhone.isNotEmpty()) rawPhone
    else "Не указан"

    val emailValid = email.isEmpty() ||
        (email.contains('@') && email.substringAfter('@').contains('.'))
    val birthdayValid = birthday.isEmpty() || isFullDate(birthday)

    val canSave = name.trim().isNotEmpty() && emailValid && birthdayValid

    val initials = remember(name) {
        name.trim().split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifEmpty { "Г" }
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 110.dp),
        ) {

            // ── Header ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
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
                Text(
                    text = "Редактирование",
                    style = TextStyle(
                        fontFamily = FrauncesFontFamily, fontSize = 22.sp,
                        fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em,
                        color = colors.ink,
                    ),
                )
            }

            // ── Avatar ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(colors.accentSoft),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = initials,
                            style = TextStyle(
                                fontFamily = FrauncesFontFamily,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.accentDeep,
                            ),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = 4.dp, y = 4.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colors.ink)
                            .border(2.dp, colors.bg, CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {},
                        contentAlignment = Alignment.Center,
                    ) { EditIcon(tint = colors.bg, size = 14.dp) }
                }
                Text(
                    text = "Сменить фото",
                    style = TextStyle(fontSize = 12.sp, color = colors.ink3),
                    modifier = Modifier.padding(top = 10.dp),
                )
            }

            // ── Form ──
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                FormSection(label = "ИМЯ", colors = colors) {
                    EditField(
                        value = name,
                        onValueChange = { name = it.take(60) },
                        placeholder = "Например, Анна Лесная",
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words,
                        colors = colors,
                    )
                }

                FormSection(
                    label = "EMAIL",
                    colors = colors,
                    error = email.isNotEmpty() && !emailValid,
                    helper = if (email.isNotEmpty() && !emailValid) "Введите корректный email" else null,
                ) {
                    EditField(
                        value = email,
                        onValueChange = { email = it.trim().take(80) },
                        placeholder = "name@example.com",
                        keyboardType = KeyboardType.Email,
                        capitalization = KeyboardCapitalization.None,
                        colors = colors,
                        error = email.isNotEmpty() && !emailValid,
                    )
                }

                FormSection(
                    label = "ДАТА РОЖДЕНИЯ",
                    colors = colors,
                    error = birthday.isNotEmpty() && !birthdayValid,
                    helper = "ДД.ММ.ГГГГ — например, 14.05.1995",
                ) {
                    EditField(
                        value = birthday,
                        onValueChange = { v ->
                            birthday = formatDateInput(v)
                        },
                        placeholder = "ДД.ММ.ГГГГ",
                        keyboardType = KeyboardType.Number,
                        capitalization = KeyboardCapitalization.None,
                        colors = colors,
                        error = birthday.isNotEmpty() && !birthdayValid,
                        monospace = true,
                    )
                }

                FormSection(label = "ПОЛ", colors = colors) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        GENDERS.forEach { g ->
                            val active = gender == g
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (active) colors.ink else colors.surface)
                                    .border(
                                        width = if (active) 1.5.dp else 1.dp,
                                        color = if (active) colors.ink else colors.line,
                                        shape = RoundedCornerShape(12.dp),
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) { gender = g },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = g,
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (active) colors.bg else colors.ink,
                                    ),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Phone (read-only)
                Column {
                    SectionLabelText("ТЕЛЕФОН", colors)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surface2)
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        PhoneCallIcon(tint = colors.ink2, size = 18.dp)
                        Text(
                            text = phoneFormatted,
                            style = TextStyle(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.ink2,
                                letterSpacing = 0.02.em,
                            ),
                            modifier = Modifier.weight(1f),
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(colors.surface3)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = "Подтверждён",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.ink3,
                                    letterSpacing = 0.04.em,
                                ),
                            )
                        }
                    }
                    Text(
                        text = "Чтобы изменить — напишите в поддержку",
                        style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                    )
                }
            }
        }

        // ── Bottom CTA ──
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
                    .background(if (canSave) colors.ink else colors.surface2)
                    .clickable(
                        enabled = canSave,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        if (canSave) {
                            authVm.updateName(name.trim())
                            vm.updateProfileExtras(email, birthday, gender)
                            onSaved()
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Сохранить",
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

@Composable
private fun FormSection(
    label: String,
    colors: AppColors,
    error: Boolean = false,
    helper: String? = null,
    content: @Composable () -> Unit,
) {
    Column {
        SectionLabelText(label, colors, error = error)
        content()
        if (helper != null) {
            Text(
                text = helper,
                style = TextStyle(
                    fontSize = 11.sp,
                    color = if (error) colors.danger else colors.ink3,
                ),
                modifier = Modifier.padding(top = 6.dp, start = 4.dp),
            )
        }
    }
}

@Composable
private fun SectionLabelText(text: String, colors: AppColors, error: Boolean = false) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.08.em,
            color = if (error) colors.danger else colors.ink3,
        ),
        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp),
    )
}

@Composable
private fun EditField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    capitalization: KeyboardCapitalization,
    colors: AppColors,
    error: Boolean = false,
    monospace: Boolean = false,
) {
    var focused by remember { mutableStateOf(false) }
    val borderColor = when {
        error -> colors.danger
        focused -> colors.ink
        else -> colors.line
    }
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
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization,
            imeAction = ImeAction.Done,
        ),
        decorationBox = { inner ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surface)
                    .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
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
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused },
    )
}

private fun formatDateInput(raw: String): String {
    val digits = raw.filter { it.isDigit() }.take(8)
    val sb = StringBuilder()
    digits.forEachIndexed { i, c ->
        if (i == 2 || i == 4) sb.append('.')
        sb.append(c)
    }
    return sb.toString()
}

private fun isFullDate(value: String): Boolean {
    val parts = value.split('.')
    if (parts.size != 3) return false
    val d = parts[0].toIntOrNull() ?: return false
    val m = parts[1].toIntOrNull() ?: return false
    val y = parts[2].toIntOrNull() ?: return false
    if (d !in 1..31) return false
    if (m !in 1..12) return false
    if (y !in 1900..2100) return false
    if (parts[0].length != 2 || parts[1].length != 2 || parts[2].length != 4) return false
    return true
}
