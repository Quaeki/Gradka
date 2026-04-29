package com.example.gradka.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.gradka.ui.components.*
import com.example.gradka.ui.theme.*

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val colors = LocalAppColors.current
    var step by remember { mutableIntStateOf(0) }

    data class Step(val badge: String, val title: String, val body: String, val hue: Float)

    val steps = listOf(
        Step("01", "С грядки — прямо на стол",  "32 фермера средней полосы. Никаких посредников и холодных складов.", 125f),
        Step("02", "Доставим за 40 минут",       "Курьер привезёт заказ в удобное время — даже если это сегодня вечером.", 95f),
        Step("03", "Любимое — в пару тапов",     "Сохраняйте списки, повторяйте заказ и подписывайтесь на регулярные продукты.", 48f),
    )
    val s = steps[step]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 24.dp)
            .padding(top = 10.dp, bottom = 24.dp)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Logo(size = 22.dp, colors = colors)
            Text(
                text = "Пропустить",
                style = TextStyle(fontSize = 13.sp, color = colors.ink3),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onFinish() },
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            IllustrationTile(hue = s.hue)
        }

        Column {
            Text(
                text = "Шаг ${s.badge}",
                style = TextStyle(
                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    letterSpacing = 0.12.em, color = colors.accentDeep,
                ),
            )
            Text(
                text = s.title,
                style = TextStyle(
                    fontFamily = FrauncesFontFamily,
                    fontSize = 34.sp, fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.025).em, lineHeight = (34 * 1.05).sp,
                    color = colors.ink,
                ),
                modifier = Modifier.padding(top = 10.dp),
            )
            Text(
                text = s.body,
                style = TextStyle(
                    fontSize = 15.sp, color = colors.ink2, lineHeight = (15 * 1.55).sp,
                ),
                modifier = Modifier.padding(top = 14.dp),
            )

            Row(
                modifier = Modifier.padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                steps.forEachIndexed { i, _ ->
                    Box(
                        modifier = Modifier
                            .height(3.dp)
                            .weight(if (i == step) 2f else 1f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (i == step) colors.ink else colors.line2),
                    )
                }
            }

            Row(
                modifier = Modifier.padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (step > 0) {
                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, colors.line2, RoundedCornerShape(16.dp))
                            .background(Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { step-- }
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Назад",
                            style = TextStyle(fontSize = 15.sp, color = colors.ink),
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.ink)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { if (step < 2) step++ else onFinish() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (step < 2) "Дальше" else "Начать покупки",
                        style = TextStyle(
                            fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.bg,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun IllustrationTile(hue: Float) {
    val bg = hueColor(hue, 0.28f, 0.93f)
    val ring = hueColor(hue, 0.25f, 0.5f)
    val center = hueColor(hue, 0.3f, 0.55f)

    Box(
        modifier = Modifier
            .size(240.dp)
            .clip(RoundedCornerShape(36.dp)),
        contentAlignment = Alignment.Center,
    ) {
        ProductPlaceholder(hue = hue, size = 240.dp)
        Canvas(modifier = Modifier.size(120.dp)) {
            val cx = this.size.width / 2
            val cy = this.size.height / 2
            drawCircle(color = ring, radius = this.size.minDimension * 0.43f, center = Offset(cx, cy), style = Stroke(width = 1.dp.toPx()))
            drawCircle(color = ring, radius = this.size.minDimension * 0.283f, center = Offset(cx, cy), style = Stroke(width = 1.dp.toPx()))
            drawCircle(color = center, radius = this.size.minDimension * 0.133f, center = Offset(cx, cy))
        }
    }
}