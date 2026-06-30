package com.blankrunner

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import kotlin.math.sqrt

/**
 * Bakes small composited shapes into a Texture once at startup. Shapes are
 * drawn hard-edged at [SS]x the final resolution, then downsampled with
 * bilinear filtering -- a cheap way to get anti-aliased edges and smooth
 * gradients out of Pixmap's otherwise blocky fill primitives.
 */
object PixelArt {
    private const val SS = 4

    fun bake(size: Int, draw: (Pixmap, Float) -> Unit): Texture {
        val big = size * SS
        val canvas = Pixmap(big, big, Pixmap.Format.RGBA8888)
        canvas.setBlending(Pixmap.Blending.SourceOver)
        draw(canvas, big.toFloat())

        canvas.setFilter(Pixmap.Filter.BiLinear)
        val small = Pixmap(size, size, Pixmap.Format.RGBA8888)
        small.setBlending(Pixmap.Blending.SourceOver)
        small.drawPixmap(canvas, 0, 0, big, big, 0, 0, size, size)
        canvas.dispose()

        val texture = Texture(small)
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        small.dispose()
        return texture
    }

    fun gradientCircle(pm: Pixmap, cx: Float, cy: Float, r: Float, top: Color, bottom: Color) {
        val cxi = cx.toInt(); val cyi = cy.toInt(); val ri = r.toInt()
        if (ri <= 0) return
        for (y in (cyi - ri)..(cyi + ri)) {
            val dy = y - cyi
            val span = ri * ri - dy * dy
            if (span < 0) continue
            val dx = sqrt(span.toFloat()).toInt()
            val t = ((y - (cyi - ri)).toFloat() / (2f * ri)).coerceIn(0f, 1f)
            pm.setColor(top.cpy().lerp(bottom, t))
            pm.drawLine(cxi - dx, y, cxi + dx, y)
        }
    }

    fun flatCircle(pm: Pixmap, cx: Float, cy: Float, r: Float, color: Color) {
        pm.setColor(color)
        pm.fillCircle(cx.toInt(), cy.toInt(), r.toInt())
    }

    fun flatEllipse(pm: Pixmap, cx: Float, cy: Float, rx: Float, ry: Float, color: Color) {
        val cxi = cx.toInt(); val cyi = cy.toInt(); val ryi = ry.toInt()
        if (ryi <= 0 || rx <= 0f) return
        pm.setColor(color)
        for (y in -ryi..ryi) {
            val t = y.toFloat() / ryi
            val span = 1f - t * t
            if (span < 0f) continue
            val dx = (rx * sqrt(span)).toInt()
            pm.drawLine(cxi - dx, cyi + y, cxi + dx, cyi + y)
        }
    }

    fun flatTriangle(pm: Pixmap, x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float, color: Color) {
        pm.setColor(color)
        pm.fillTriangle(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt(), x3.toInt(), y3.toInt())
    }

    fun flatRect(pm: Pixmap, x: Float, y: Float, w: Float, h: Float, color: Color) {
        pm.setColor(color)
        pm.fillRectangle(x.toInt(), y.toInt(), w.toInt(), h.toInt())
    }

    fun diamond(pm: Pixmap, cx: Float, cy: Float, halfW: Float, halfH: Float, color: Color) {
        flatTriangle(pm, cx - halfW, cy, cx, cy - halfH, cx + halfW, cy, color)
        flatTriangle(pm, cx - halfW, cy, cx, cy + halfH, cx + halfW, cy, color)
    }

    fun line(pm: Pixmap, x1: Float, y1: Float, x2: Float, y2: Float, color: Color) {
        pm.setColor(color)
        pm.drawLine(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
    }
}
