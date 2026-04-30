package com.example.gradka.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.gradka.domain.Product
import com.example.gradka.ui.theme.*

// ── helpers ──
fun hueColor(hue: Float, saturation: Float, lightness: Float): Color {
    val h = hue / 360f
    val q = if (lightness < 0.5f) lightness * (1 + saturation) else lightness + saturation - lightness * saturation
    val p = 2 * lightness - q
    fun hue2rgb(t: Float): Float {
        val tt = if (t < 0) t + 1 else if (t > 1) t - 1 else t
        return when {
            tt < 1f / 6 -> p + (q - p) * 6 * tt
            tt < 1f / 2 -> q
            tt < 2f / 3 -> p + (q - p) * (2f / 3 - tt) * 6
            else -> p
        }
    }
    return Color(hue2rgb(h + 1f / 3), hue2rgb(h), hue2rgb(h - 1f / 3))
}

// ── ProductPlaceholder ──
@Composable
fun ProductPlaceholder(hue: Float, size: Dp, label: String = "") {
    val bg = hueColor(hue, 0.28f, 0.93f)
    val stripe = hueColor(hue, 0.30f, 0.87f)
    val labelColor = hueColor(hue, 0.22f, 0.42f)

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(bg),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 8.dp.toPx()
            var y = step / 2
            while (y < this.size.height) {
                drawLine(
                    color = stripe,
                    start = Offset(0f, y),
                    end = Offset(this.size.width, y),
                    strokeWidth = 1.dp.toPx(),
                )
                y += step
            }
        }
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 9.sp,
                    color = labelColor,
                    letterSpacing = 0.04.em,
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 10.dp, bottom = 8.dp),
                maxLines = 1,
            )
        }
    }
}

// ── Stepper ──
@Composable
fun Stepper(
    qty: Int,
    onAdd: () -> Unit,
    onSub: () -> Unit,
    compact: Boolean = false,
    colors: AppColors = LocalAppColors.current,
) {
    val h = if (compact) 32.dp else 36.dp
    val btnSize = if (compact) 24.dp else 28.dp
    val iconSize = if (compact) 14.dp else 16.dp

    if (qty == 0) {
        Box(
            modifier = Modifier
                .size(h)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.ink)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAdd,
                ),
            contentAlignment = Alignment.Center,
        ) {
            PlusIcon(tint = colors.bg, size = iconSize)
        }
    } else {
        Row(
            modifier = Modifier
                .height(h)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.ink)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(btnSize)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSub,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                MinusIcon(tint = colors.bg, size = iconSize)
            }
            Text(
                text = qty.toString(),
                style = TextStyle(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.bg,
                ),
                modifier = Modifier.widthIn(min = if (compact) 22.dp else 26.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Box(
                modifier = Modifier
                    .size(btnSize)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onAdd,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                PlusIcon(tint = colors.bg, size = iconSize)
            }
        }
    }
}

// ── Chip ──
@Composable
fun Chip(active: Boolean, onClick: () -> Unit, colors: AppColors = LocalAppColors.current, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (active) colors.ink else colors.surface)
            .border(1.dp, if (active) colors.ink else colors.line, RoundedCornerShape(999.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        content()
    }
}

// ── SectionTitle ──
@Composable
fun SectionTitle(
    title: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    colors: AppColors = LocalAppColors.current,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = FrauncesFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.02).em,
                color = colors.ink,
            ),
        )
        if (action != null && onAction != null) {
            Row(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAction,
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = action,
                    style = TextStyle(fontSize = 13.sp, color = colors.ink2),
                )
                ChevronIcon(tint = colors.ink2, size = 14.dp)
            }
        }
    }
}

// ── Logo ──
@Composable
fun Logo(size: Dp = 20.dp, color: Color? = null, colors: AppColors = LocalAppColors.current) {
    val c = color ?: colors.ink
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(modifier = Modifier.size(size)) {
            val cx = this.size.width / 2
            val cy = this.size.height / 2
            val r = this.size.minDimension / 2 - 1.dp.toPx()
            drawCircle(color = c, radius = r, center = Offset(cx, cy), style = Stroke(width = 1.6.dp.toPx()))
            val leafPath = Path().apply {
                moveTo(cx - r * 0.33f, cy + r * 0.25f)
                cubicTo(cx - r * 0.33f, cy - r * 0.9f, cx + r * 0.8f, cy - r * 0.9f, cx + r * 0.5f, cy - r * 0.3f)
                cubicTo(cx + r * 0.2f, cy + r * 0.3f, cx - r * 0.33f, cy + r * 0.25f, cx - r * 0.33f, cy + r * 0.25f)
                close()
            }
            drawPath(path = leafPath, color = c)
        }
        Text(
            text = "грядка",
            style = TextStyle(
                fontFamily = FrauncesFontFamily,
                fontSize = (size.value * 0.95f).sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.02).em,
                color = c,
            ),
        )
    }
}

// ── ProductCard ──
@Composable
fun ProductCard(
    product: Product,
    qty: Int,
    onAdd: () -> Unit,
    onSub: () -> Unit,
    onOpen: () -> Unit,
    onFav: () -> Unit,
    isFav: Boolean,
    colors: AppColors = LocalAppColors.current,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colors.surface)
            .border(1.dp, colors.line, RoundedCornerShape(18.dp))
            .padding(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box {
                Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, onClick = onOpen,
                )) {
                    ProductPlaceholder(hue = product.hue, size = 120.dp, label = product.cat)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null, onClick = onFav,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    HeartIcon(
                        tint = if (isFav) colors.danger else colors.ink2,
                        filled = isFav,
                        size = 18.dp,
                    )
                }
                if (product.badge != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (product.badge.startsWith("-")) colors.danger else colors.ink)
                            .padding(horizontal = 7.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = product.badge.uppercase(),
                            style = TextStyle(
                                fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                letterSpacing = 0.02.em, color = Color.White,
                            ),
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, onClick = onOpen,
                ),
            ) {
                Text(
                    text = product.name,
                    style = TextStyle(
                        fontSize = 13.sp, fontWeight = FontWeight.Medium,
                        lineHeight = (13 * 1.25).sp, color = colors.ink,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = product.subtitle,
                    style = TextStyle(fontSize = 11.sp, color = colors.ink3),
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        text = "${product.price} ₽",
                        style = TextStyle(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = colors.ink,
                        ),
                    )
                    Text(text = product.unit, style = TextStyle(fontSize = 10.sp, color = colors.ink3))
                }
                Stepper(qty = qty, onAdd = onAdd, onSub = onSub, compact = true, colors = colors)
            }
        }
    }
}

// ── ProductRow ──
@Composable
fun ProductRow(
    product: Product,
    qty: Int,
    onAdd: () -> Unit,
    onSub: () -> Unit,
    onOpen: () -> Unit,
    colors: AppColors = LocalAppColors.current,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.line, RoundedCornerShape(16.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null, onClick = onOpen,
        )) {
            ProductPlaceholder(hue = product.hue, size = 72.dp)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, onClick = onOpen,
                ),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = product.name, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = colors.ink))
            Text(text = "${product.subtitle} · ${product.unit}", style = TextStyle(fontSize = 12.sp, color = colors.ink3))
            Text(
                text = "${product.price} ₽",
                style = TextStyle(fontFamily = JetBrainsMonoFontFamily, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = colors.ink),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Stepper(qty = qty, onAdd = onAdd, onSub = onSub, compact = true, colors = colors)
    }
}

// ── BottomNavBar ──
@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    cartCount: Int,
    colors: AppColors = LocalAppColors.current,
) {
    val items = listOf(
        Triple("home",    "Главная",   0),
        Triple("catalog", "Каталог",   1),
        Triple("fav",     "Избранное", 2),
        Triple("cart",    "Корзина",   3),
        Triple("profile", "Профиль",   4),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(width = 1.dp, color = colors.line, shape = RoundedCornerShape(0.dp))
            .padding(top = 8.dp, bottom = 6.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        items.forEach { (route, label, iconIndex) ->
            val active = currentRoute == route
            val tint = if (active) colors.ink else colors.ink3
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onNavigate(route) },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Box {
                        when (iconIndex) {
                            0 -> HomeIcon(tint = tint)
                            1 -> GridIcon(tint = tint)
                            2 -> HeartIcon(tint = tint, filled = false, size = 24.dp)
                            3 -> BagIcon(tint = tint)
                            4 -> UserIcon(tint = tint)
                        }
                        if (iconIndex == 3 && cartCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-4).dp)
                                    .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.accent)
                                    .padding(horizontal = 4.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = cartCount.toString(),
                                    style = TextStyle(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                        color = colors.accentInk,
                                    ),
                                )
                            }
                        }
                    }
                    Text(
                        text = label,
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                            color = tint,
                            letterSpacing = (-0.01).em,
                        ),
                    )
                }
            }
        }
    }
}

// ── Inline SVG icons via Canvas ──

@Composable
fun HomeIcon(tint: Color, size: Dp = 24.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.125f, s * 0.4375f)
            lineTo(s * 0.5f, s * 0.125f)
            lineTo(s * 0.875f, s * 0.4375f)
            lineTo(s * 0.875f, s * 0.833f)
            cubicTo(s * 0.875f, s * 0.875f, s * 0.833f, s * 0.875f, s * 0.792f, s * 0.875f)
            lineTo(s * 0.625f, s * 0.875f)
            lineTo(s * 0.625f, s * 0.625f)
            lineTo(s * 0.375f, s * 0.625f)
            lineTo(s * 0.375f, s * 0.875f)
            lineTo(s * 0.167f, s * 0.875f)
            cubicTo(s * 0.125f, s * 0.875f, s * 0.125f, s * 0.833f, s * 0.125f, s * 0.833f)
            close()
        }
        drawPath(path = path, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun GridIcon(tint: Color, size: Dp = 24.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val r = s * 0.08f
        listOf(
            Pair(s * 0.146f, s * 0.146f),
            Pair(s * 0.563f, s * 0.146f),
            Pair(s * 0.146f, s * 0.563f),
            Pair(s * 0.563f, s * 0.563f),
        ).forEach { (x, y) ->
            val rectSize = s * 0.292f
            val path = Path().apply {
                addRoundRect(
                    androidx.compose.ui.geometry.RoundRect(
                        left = x, top = y,
                        right = x + rectSize, bottom = y + rectSize,
                        radiusX = r, radiusY = r,
                    )
                )
            }
            drawPath(path = path, color = tint, style = Stroke(width = 1.6.dp.toPx()))
        }
    }
}

@Composable
fun HeartIcon(tint: Color, filled: Boolean, size: Dp = 24.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.5f, s * 0.833f)
            cubicTo(s * 0.5f, s * 0.833f, s * 0.125f, s * 0.652f, s * 0.125f, s * 0.417f)
            cubicTo(s * 0.125f, s * 0.25f, s * 0.25f, s * 0.167f, s * 0.5f, s * 0.277f)
            cubicTo(s * 0.75f, s * 0.167f, s * 0.875f, s * 0.25f, s * 0.875f, s * 0.417f)
            cubicTo(s * 0.875f, s * 0.652f, s * 0.5f, s * 0.833f, s * 0.5f, s * 0.833f)
            close()
        }
        if (filled) drawPath(path = path, color = tint)
        drawPath(path = path, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun BagIcon(tint: Color, size: Dp = 24.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val body = Path().apply {
            moveTo(s * 0.208f, s * 0.333f)
            lineTo(s * 0.792f, s * 0.333f)
            lineTo(s * 0.75f, s * 0.792f)
            cubicTo(s * 0.75f, s * 0.875f, s * 0.708f, s * 0.875f, s * 0.667f, s * 0.875f)
            lineTo(s * 0.333f, s * 0.875f)
            cubicTo(s * 0.292f, s * 0.875f, s * 0.25f, s * 0.875f, s * 0.25f, s * 0.792f)
            close()
        }
        drawPath(path = body, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
        val handle = Path().apply {
            moveTo(s * 0.375f, s * 0.333f)
            lineTo(s * 0.375f, s * 0.25f)
            cubicTo(s * 0.375f, s * 0.125f, s * 0.625f, s * 0.125f, s * 0.625f, s * 0.25f)
            lineTo(s * 0.625f, s * 0.333f)
        }
        drawPath(path = handle, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun UserIcon(tint: Color, size: Dp = 24.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        drawCircle(color = tint, radius = s * 0.167f, center = Offset(s * 0.5f, s * 0.333f), style = Stroke(width = 1.6.dp.toPx()))
        val arc = Path().apply {
            moveTo(s * 0.167f, s * 0.875f)
            cubicTo(s * 0.229f, s * 0.708f, s * 0.354f, s * 0.625f, s * 0.5f, s * 0.625f)
            cubicTo(s * 0.646f, s * 0.625f, s * 0.771f, s * 0.708f, s * 0.833f, s * 0.875f)
        }
        drawPath(path = arc, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun SearchIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        drawCircle(color = tint, radius = s * 0.292f, center = Offset(s * 0.458f, s * 0.458f), style = Stroke(width = 1.8.dp.toPx()))
        drawLine(color = tint, start = Offset(s * 0.708f, s * 0.708f), end = Offset(s * 0.875f, s * 0.875f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun PlusIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        drawLine(color = tint, start = Offset(s * 0.5f, s * 0.208f), end = Offset(s * 0.5f, s * 0.792f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(s * 0.208f, s * 0.5f), end = Offset(s * 0.792f, s * 0.5f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun MinusIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        drawLine(color = tint, start = Offset(s * 0.208f, s * 0.5f), end = Offset(s * 0.792f, s * 0.5f), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun BackIcon(tint: Color, size: Dp = 24.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.625f, s * 0.792f)
            lineTo(s * 0.333f, s * 0.5f)
            lineTo(s * 0.625f, s * 0.208f)
        }
        drawPath(path = path, color = tint, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun ChevronIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.375f, s * 0.25f)
            lineTo(s * 0.625f, s * 0.5f)
            lineTo(s * 0.375f, s * 0.75f)
        }
        drawPath(path = path, color = tint, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun PinIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.5f, s * 0.917f)
            cubicTo(s * 0.5f, s * 0.917f, s * 0.125f, s * 0.604f, s * 0.125f, s * 0.375f)
            cubicTo(s * 0.125f, s * 0.167f, s * 0.292f, s * 0f, s * 0.5f, s * 0f)
            cubicTo(s * 0.708f, s * 0f, s * 0.875f, s * 0.167f, s * 0.875f, s * 0.375f)
            cubicTo(s * 0.875f, s * 0.604f, s * 0.5f, s * 0.917f, s * 0.5f, s * 0.917f)
            close()
        }
        drawPath(path = path, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
        drawCircle(color = tint, radius = s * 0.104f, center = Offset(s * 0.5f, s * 0.375f), style = Stroke(width = 1.6.dp.toPx()))
    }
}

@Composable
fun LeafIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path1 = Path().apply {
            moveTo(s * 0.833f, s * 0.167f)
            cubicTo(s * 0.833f, s * 0.167f, s * 0.25f, s * 0.375f, s * 0.25f, s * 0.833f)
            cubicTo(s * 0.25f, s * 0.917f, s * 0.292f, s * 1f, s * 0.333f, s * 1f)
            cubicTo(s * 0.917f, s * 1f, s * 1.333f, s * 0.5f, s * 0.833f, s * 0.167f)
            close()
        }
        drawPath(path = path1, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
        val path2 = Path().apply {
            moveTo(s * 0.25f, s * 0.833f)
            cubicTo(s * 0.375f, s * 0.583f, s * 0.542f, s * 0.417f, s * 0.833f, s * 0.167f)
        }
        drawPath(path = path2, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun ClockIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        drawCircle(color = tint, radius = s * 0.375f, center = Offset(s * 0.5f, s * 0.5f), style = Stroke(width = 1.6.dp.toPx()))
        val path = Path().apply {
            moveTo(s * 0.5f, s * 0.292f)
            lineTo(s * 0.5f, s * 0.5f)
            lineTo(s * 0.625f, s * 0.583f)
        }
        drawPath(path = path, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun CheckIcon(tint: Color, size: Dp = 20.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.208f, s * 0.5f)
            lineTo(s * 0.375f, s * 0.667f)
            lineTo(s * 0.792f, s * 0.25f)
        }
        drawPath(path = path, color = tint, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun DeliveryIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val body = Path().apply {
            moveTo(s * 0.125f, s * 0.292f)
            lineTo(s * 0.583f, s * 0.292f)
            lineTo(s * 0.583f, s * 0.708f)
            lineTo(s * 0.125f, s * 0.708f)
            close()
        }
        drawPath(path = body, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
        val truck = Path().apply {
            moveTo(s * 0.583f, s * 0.417f)
            lineTo(s * 0.75f, s * 0.417f)
            lineTo(s * 0.875f, s * 0.542f)
            lineTo(s * 0.875f, s * 0.708f)
            lineTo(s * 0.583f, s * 0.708f)
        }
        drawPath(path = truck, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
        drawCircle(color = tint, radius = s * 0.083f, center = Offset(s * 0.292f, s * 0.75f), style = Stroke(width = 1.6.dp.toPx()))
        drawCircle(color = tint, radius = s * 0.083f, center = Offset(s * 0.708f, s * 0.75f), style = Stroke(width = 1.6.dp.toPx()))
    }
}

@Composable
fun RepeatIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val p1 = Path().apply {
            moveTo(s * 0.167f, s * 0.375f)
            lineTo(s * 0.167f, s * 0.292f)
            cubicTo(s * 0.167f, s * 0.208f, s * 0.25f, s * 0.125f, s * 0.333f, s * 0.125f)
            lineTo(s * 0.833f, s * 0.125f)
            lineTo(s * 0.708f, s * 0f)
        }
        drawPath(path = p1, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
        val p2 = Path().apply {
            moveTo(s * 0.833f, s * 0.625f)
            lineTo(s * 0.833f, s * 0.708f)
            cubicTo(s * 0.833f, s * 0.792f, s * 0.75f, s * 0.875f, s * 0.667f, s * 0.875f)
            lineTo(s * 0.167f, s * 0.875f)
            lineTo(s * 0.292f, s * 1f)
        }
        drawPath(path = p2, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun CloseIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        drawLine(color = tint, start = Offset(s * 0.25f, s * 0.25f), end = Offset(s * 0.75f, s * 0.75f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
        drawLine(color = tint, start = Offset(s * 0.75f, s * 0.25f), end = Offset(s * 0.25f, s * 0.75f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun MicIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val mic = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = s * 0.375f, top = s * 0.125f,
                    right = s * 0.625f, bottom = s * 0.625f,
                    radiusX = s * 0.125f, radiusY = s * 0.125f,
                )
            )
        }
        drawPath(path = mic, color = tint, style = Stroke(width = 1.6.dp.toPx()))
        val arc = Path().apply {
            moveTo(s * 0.208f, s * 0.458f)
            cubicTo(s * 0.208f, s * 0.75f, s * 0.792f, s * 0.75f, s * 0.792f, s * 0.458f)
        }
        drawPath(path = arc, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
        drawLine(color = tint, start = Offset(s * 0.5f, s * 0.75f), end = Offset(s * 0.5f, s * 0.875f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun MoreIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        listOf(0.208f, 0.5f, 0.792f).forEach { x ->
            drawCircle(color = tint, radius = s * 0.067f, center = Offset(s * x, s * 0.5f))
        }
    }
}

@Composable
fun FilterIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        listOf(Pair(s * 0.25f, s * 0.75f), Pair(s * 0.5f, s * 0.5f), Pair(s * 0.75f, s * 0.25f)).forEach { (x, _) ->
            drawLine(color = tint, start = Offset(s * 0.167f, x), end = Offset(s * 0.833f, x), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
        }
    }
}

@Composable
fun EditIcon(tint: Color, size: Dp = 15.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.167f, s * 0.833f)
            lineTo(s * 0.333f, s * 0.833f)
            lineTo(s * 0.792f, s * 0.375f)
            lineTo(s * 0.625f, s * 0.208f)
            lineTo(s * 0.167f, s * 0.667f)
            close()
        }
        drawPath(path = path, color = tint, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun PhoneCallIcon(tint: Color, size: Dp = 18.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.125f, s * 0.208f)
            cubicTo(s * 0.125f, s * 0.125f, s * 0.25f, s * 0.125f, s * 0.25f, s * 0.125f)
            lineTo(s * 0.375f, s * 0.125f)
            lineTo(s * 0.458f, s * 0.333f)
            lineTo(s * 0.333f, s * 0.417f)
            cubicTo(s * 0.458f, s * 0.625f, s * 0.583f, s * 0.75f, s * 0.583f, s * 0.667f)
            lineTo(s * 0.667f, s * 0.542f)
            lineTo(s * 0.875f, s * 0.625f)
            lineTo(s * 0.875f, s * 0.75f)
            cubicTo(s * 0.875f, s * 0.875f, s * 0.625f, s * 1f, s * 0.375f, s * 0.833f)
            close()
        }
        drawPath(path = path, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun BellIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.25f, s * 0.333f)
            cubicTo(s * 0.25f, s * 0.146f, s * 0.375f, s * 0.042f, s * 0.5f, s * 0.042f)
            cubicTo(s * 0.625f, s * 0.042f, s * 0.75f, s * 0.146f, s * 0.75f, s * 0.333f)
            cubicTo(s * 0.75f, s * 0.625f, s * 0.875f, s * 0.708f, s * 0.875f, s * 0.708f)
            lineTo(s * 0.125f, s * 0.708f)
            cubicTo(s * 0.125f, s * 0.708f, s * 0.25f, s * 0.625f, s * 0.25f, s * 0.333f)
            close()
        }
        drawPath(path = path, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
        drawLine(color = tint, start = Offset(s * 0.417f, s * 0.875f), end = Offset(s * 0.583f, s * 0.875f), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun ChatIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.875f, s * 0.625f)
            cubicTo(s * 0.875f, s * 0.708f, s * 0.792f, s * 0.792f, s * 0.708f, s * 0.792f)
            lineTo(s * 0.292f, s * 0.792f)
            lineTo(s * 0.125f, s * 0.958f)
            lineTo(s * 0.125f, s * 0.208f)
            cubicTo(s * 0.125f, s * 0.125f, s * 0.208f, s * 0.042f, s * 0.292f, s * 0.042f)
            lineTo(s * 0.708f, s * 0.042f)
            cubicTo(s * 0.792f, s * 0.042f, s * 0.875f, s * 0.125f, s * 0.875f, s * 0.208f)
            close()
        }
        drawPath(path = path, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun TicketIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.833f, s * 0.5f)
            cubicTo(s * 0.833f, s * 0.417f, s * 0.833f, s * 0.167f, s * 0.833f, s * 0.167f)
            lineTo(s * 0.167f, s * 0.167f)
            lineTo(s * 0.167f, s * 0.417f)
            cubicTo(s * 0.25f, s * 0.417f, s * 0.25f, s * 0.583f, s * 0.167f, s * 0.583f)
            lineTo(s * 0.167f, s * 0.833f)
            lineTo(s * 0.833f, s * 0.833f)
            lineTo(s * 0.833f, s * 0.583f)
            cubicTo(s * 0.75f, s * 0.583f, s * 0.75f, s * 0.417f, s * 0.833f, s * 0.417f)
            close()
        }
        drawPath(path = path, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
    }
}

// ── BackspaceIcon ──
@Composable
fun BackspaceIcon(tint: Color, size: Dp = 22.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val s = this.size.minDimension
        val path = Path().apply {
            moveTo(s * 0.38f, s * 0.20f)
            lineTo(s * 0.85f, s * 0.20f)
            lineTo(s * 0.85f, s * 0.80f)
            lineTo(s * 0.38f, s * 0.80f)
            lineTo(s * 0.15f, s * 0.50f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
        drawLine(tint, Offset(s * 0.52f, s * 0.37f), Offset(s * 0.72f, s * 0.63f), 1.6.dp.toPx(), StrokeCap.Round)
        drawLine(tint, Offset(s * 0.72f, s * 0.37f), Offset(s * 0.52f, s * 0.63f), 1.6.dp.toPx(), StrokeCap.Round)
    }
}

// ── NumPad ──
private val numPadRows = listOf(
    listOf("1" to "", "2" to "ABC", "3" to "DEF"),
    listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
    listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ"),
)

@Composable
fun NumPad(
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
    onDone: () -> Unit,
    doneEnabled: Boolean,
    colors: AppColors = LocalAppColors.current,
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        numPadRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { (digit, sub) ->
                    NumPadDigitKey(
                        modifier = Modifier.weight(1f),
                        digit = digit, sub = sub,
                        onClick = { onDigit(digit) },
                        colors = colors,
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            NumPadActionKey(modifier = Modifier.weight(1f), enabled = doneEnabled, onClick = onDone, colors = colors) {
                Text("Далее", style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.ink2))
            }
            NumPadDigitKey(modifier = Modifier.weight(1f), digit = "0", sub = "", onClick = { onDigit("0") }, colors = colors)
            NumPadActionKey(modifier = Modifier.weight(1f), onClick = onDelete, colors = colors) {
                BackspaceIcon(tint = colors.ink, size = 22.dp)
            }
        }
    }
}

@Composable
private fun NumPadDigitKey(
    modifier: Modifier = Modifier,
    digit: String,
    sub: String,
    onClick: () -> Unit,
    colors: AppColors,
) {
    var pressed by remember { mutableStateOf(false) }
    val bg by animateColorAsState(if (pressed) colors.surface2 else colors.surface, tween(70), label = "key_bg")
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, tween(70), label = "key_scale")

    Box(
        modifier = modifier
            .height(60.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .pointerInput(onClick) {
                detectTapGestures(onPress = {
                    pressed = true
                    tryAwaitRelease()
                    pressed = false
                    onClick()
                })
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(digit, style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Medium, color = colors.ink))
            if (sub.isNotEmpty()) {
                Text(sub, style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.12.em, color = colors.ink3))
            }
        }
    }
}

@Composable
private fun NumPadActionKey(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    colors: AppColors,
    content: @Composable () -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }
    val bg by animateColorAsState(if (pressed) colors.surface3 else colors.surface2, tween(70), label = "action_bg")
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, tween(70), label = "action_scale")

    Box(
        modifier = modifier
            .height(60.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = if (enabled) 1f else 0.35f }
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .pointerInput(enabled, onClick) {
                if (enabled) {
                    detectTapGestures(onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                        onClick()
                    })
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}