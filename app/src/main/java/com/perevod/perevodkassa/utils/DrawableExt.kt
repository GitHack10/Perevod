package com.perevod.perevodkassa.utils

import android.content.res.ColorStateList
import android.graphics.CornerPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

fun createRoundedRippleDrawable(
    @ColorInt color: Int,
    cornersDp: Float,
    content: Drawable? = null
): Drawable = RippleDrawable(
    ColorStateList.valueOf(color),
    content,
    ShapeDrawable(RectShape()).apply {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.pathEffect = CornerPathEffect(cornersDp.dpToPx)
    }
)

fun createRoundedRippleDrawable(
    @ColorInt rippleColor: Int,
    cornersPx: Float,
    bgColor: Int?
): Drawable {
    val rippleDrawable = RippleDrawable(
        ColorStateList.valueOf(rippleColor),
        null,
        ShapeDrawable(RectShape()).apply {
            paint.isAntiAlias = true
            paint.style = Paint.Style.FILL
            paint.pathEffect = CornerPathEffect(cornersPx)
        }
    )
    return LayerDrawable(
        if (bgColor == null) {
            arrayOf(rippleDrawable)
        } else {
            val mainShape = createRoundedDrawable(
                cornersPx, bgColor
            )
            arrayOf(mainShape, rippleDrawable)
        }
    )
}

fun createRoundedDrawable(
    cornersDp: Float,
    @ColorInt
    bgColor: Int,
    strokeWidthDp: Float? = null,
    strokeColor: ColorStateList? = null
): Drawable = createRoundedDrawable(
    cornersDp,
    ColorStateList.valueOf(bgColor),
    strokeWidthDp,
    strokeColor
)

fun createRoundedDrawable(
    cornersDp: Float,
    bgColor: ColorStateList,
    strokeWidthDp: Float? = null,
    strokeColor: ColorStateList? = null
): Drawable = MaterialShapeDrawable(
    ShapeAppearanceModel.builder()
        .setAllCornerSizes(cornersDp.dpToPx)
        .build()
).apply {
    fillColor = bgColor
    strokeWidthDp?.let {
        this.strokeWidth = it.dpToPx
    }
    strokeColor?.let {
        this.strokeColor = it
    }
}

fun createCircleDrawable(@ColorInt color: Int): Drawable =
    ShapeDrawable(OvalShape()).apply {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = color
    }

fun createCircleRippleDrawable(
    @ColorInt color: Int,
    content: Drawable? = null
): Drawable = RippleDrawable(
    ColorStateList.valueOf(color),
    content,
    ShapeDrawable(OvalShape()).apply {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
    }
)

fun createHorizontalGradient(
    @ColorInt startColor: Int,
    @ColorInt endColor: Int
): Drawable = PaintDrawable().apply {
    shape = RectShape()
    shaderFactory = object : ShapeDrawable.ShaderFactory() {

        override fun resize(width: Int, height: Int): Shader =
            LinearGradient(
                0F,
                0F,
                width.toFloat(),
                0F,
                intArrayOf(startColor, endColor),
                floatArrayOf(0F, 1F),
                Shader.TileMode.CLAMP
            )
    }
}

fun View.setVerticalGradientBackground(
    @ColorRes
    startColor: Int,
    @ColorRes
    endColor: Int
) {
    background = PaintDrawable().apply {
        shape = RectShape()
        shaderFactory = object : ShapeDrawable.ShaderFactory() {

            override fun resize(width: Int, height: Int): Shader =
                LinearGradient(
                    0F,
                    0F,
                    0F,
                    height.toFloat(),
                    intArrayOf(
                        context.resColor(startColor),
                        context.resColor(endColor)
                    ),
                    floatArrayOf(0F, 1F),
                    Shader.TileMode.CLAMP
                )
        }
    }
}

fun View.setHorizontalGradientBackground(
    @ColorRes
    startColor: Int,
    @ColorRes
    endColor: Int
) {
    background = PaintDrawable().apply {
        shape = RectShape()
        shaderFactory = object : ShapeDrawable.ShaderFactory() {

            override fun resize(width: Int, height: Int): Shader =
                LinearGradient(
                    0F,
                    0F,
                    width.toFloat(),
                    0F,
                    intArrayOf(
                        context.resColor(startColor),
                        context.resColor(endColor)
                    ),
                    floatArrayOf(0F, 1F),
                    Shader.TileMode.CLAMP
                )
        }
    }
}