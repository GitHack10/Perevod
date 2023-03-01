package com.perevod.perevodkassa.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.common.BitMatrix

fun createQrBitmap(matrix: BitMatrix): Bitmap? {
    val width = matrix.width
    val height = matrix.height
    val pixels = IntArray(width * height)
    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] =
                if (matrix[x, y]) Color.BLACK else Color.WHITE
        }
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}