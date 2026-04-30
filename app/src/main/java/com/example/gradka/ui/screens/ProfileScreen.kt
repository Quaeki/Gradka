package com.example.gradka.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gradka.AppViewModel
import com.example.gradka.AuthEvent
import com.example.gradka.AuthViewModel
import com.example.gradka.AuthViewModelFactory
import com.example.gradka.data.PRODUCTS
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

@Composable
fun ProfileScreen(
    vm: AppViewModel,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit = {},
    authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current.applicationContext as Application)),
) {
    val colors = LocalAppColors.current
    val authState by authVm.state.collectAsState()
    val subscriptions by vm.subscriptions.collectAsState()

    val rawPhone = authState.phone
    val phoneFormatted = if (rawPhone.length == 10)
        "+7 (${rawPhone.take(3)}) ${rawPhone.substring(3, 6)}-${rawPhone.substring(6, 8)}-${rawPhone.substring(8, 10)}"
    else rawPhone

    val displayName = authState.name.ifEmpty { "Пользователь" }
    val initials = displayName.trim().split(" ")
        .filter { it.isNotEmpty() }.take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(colors.bg).statusBarsPadding(),
    ) {
        item {
            Text(
                text = "Профиль",
                style = TextStyle(
                    fontFamily = FrauncesFontFamily, fontSize = 24.sp,
                    fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.ink,
                ),
                modifier = Modifier.padding(horizontal = 20.dp).padding(top = 14.dp, bottom = 10.dp),
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // User card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.surface)
                        .border(1.dp, colors.line, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(colors.accentSoft),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = initials, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.accentDeep))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = displayName, style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium, color = colors.ink))
                        Text(text = phoneFormatted, style = TextStyle(fontSize = 12.sp, color = colors.ink3), modifier = Modifier.padding(top = 2.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(1.dp, colors.line, CircleShape)
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
                        contentAlignment = Alignment.Center,
                    ) { EditIcon(tint = colors.ink2) }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Loyalty card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(colors.ink)
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column {
                            Text(
                                text = "Уровень · Серебро",
                                style = TextStyle(
                                    fontSize = 11.sp, color = colors.bg.copy(alpha = 0.55f),
                                    letterSpacing = 0.08.em, fontWeight = FontWeight.SemiBold,
                                ),
                            )
                            Text(
                                text = "1 248 баллов",
                                style = TextStyle(
                                    fontFamily = FrauncesFontFamily, fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium, letterSpacing = (-0.02).em, color = colors.bg,
                                ),
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(colors.bg.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(text = "Кешбек 3%", style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = colors.bg))
                        }
                    }
                    Column(modifier = Modifier.padding(top = 14.dp)) {
                        Text(text = "До золота: 752 балла", style = TextStyle(fontSize = 11.sp, color = colors.bg.copy(alpha = 0.6f)))
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(colors.bg.copy(alpha = 0.15f)),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.62f)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(colors.accent),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Menu groups
                val subsCount = subscriptions.size
                val activeSubs = vm.subscriptionsActiveCount
                val firstActiveSub = subscriptions.firstOrNull { it.active }
                val firstActiveProduct = firstActiveSub?.let { s -> PRODUCTS.find { it.id == s.productId } }
                val subsSubtitle = when {
                    subsCount == 0 -> "Подключить со скидкой −5%"
                    firstActiveProduct != null && activeSubs > 1 ->
                        "${firstActiveProduct.name.split(' ').first()} и ещё ${activeSubs - 1}"
                    firstActiveProduct != null -> firstActiveProduct.name.split(' ').first() + " · еженедельно"
                    else -> "$subsCount на паузе"
                }
                val groups = listOf(
                    listOf(
                        Triple("Мои заказы",  "3 активных",         "orders"),
                        Triple("Адреса",       "2 сохранённых",      "address"),
                        Triple("Подписки",     subsSubtitle,         "subscriptions"),
                    ),
                    listOf(
                        Triple("Промокоды",    "2 активных",         null),
                        Triple("Уведомления",  "Вкл",                null),
                        Triple("Поддержка",    "Написать в чат",     "support"),
                    ),
                )

                groups.forEach { group ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(colors.surface)
                            .border(1.dp, colors.line, RoundedCornerShape(16.dp)),
                    ) {
                        group.forEachIndexed { i, (label, sub, route) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (i < group.size - 1)
                                            Modifier.border(width = 0.dp, color = colors.line, shape = RoundedCornerShape(0.dp))
                                        else Modifier
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) { route?.let { onNavigate(it) } }
                                    .padding(horizontal = 16.dp, vertical = 13.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                Box(
                                    modifier = Modifier.size(22.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    when (label) {
                                        "Мои заказы"  -> BagIcon(tint = colors.ink2, size = 22.dp)
                                        "Адреса"       -> PinIcon(tint = colors.ink2, size = 22.dp)
                                        "Подписки"     -> RepeatIcon(tint = colors.ink2, size = 22.dp)
                                        "Промокоды"    -> TicketIcon(tint = colors.ink2, size = 22.dp)
                                        "Уведомления"  -> BellIcon(tint = colors.ink2, size = 22.dp)
                                        "Поддержка"    -> ChatIcon(tint = colors.ink2, size = 22.dp)
                                        else -> {}
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = label, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink))
                                    Text(text = sub, style = TextStyle(fontSize = 11.sp, color = colors.ink3), modifier = Modifier.padding(top = 1.dp))
                                }
                                ChevronIcon(tint = colors.ink3, size = 16.dp)
                            }
                            if (i < group.size - 1) {
                                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(colors.line).padding(horizontal = 16.dp))
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            authVm.onEvent(AuthEvent.Logout)
                            onLogout()
                        }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "Выйти из аккаунта", style = TextStyle(fontSize = 13.sp, color = colors.danger))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
