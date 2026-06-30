package com.blankrunner

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture

/** Bakes the player, every enemy archetype, and every fruit type into small textures once at startup. */
object SpriteFactory {
    private const val SIZE = 128

    class Sprites(
        val player: Texture,
        val enemies: Map<EnemyType, Texture>,
        val fruits: Map<FruitType, Texture>
    ) {
        fun dispose() {
            player.dispose()
            enemies.values.forEach { it.dispose() }
            fruits.values.forEach { it.dispose() }
        }
    }

    fun build(): Sprites = Sprites(
        player = bakePlayer(),
        enemies = EnemyType.values().associateWith { bakeEnemy(it) },
        fruits = FruitType.values().associateWith { bakeFruit(it) }
    )

    private fun outline() = Color(0.13f, 0.16f, 0.2f, 1f)

    private fun bakePlayer(): Texture = PixelArt.bake(SIZE) { pm, big ->
        val cx = big / 2f
        val cy = big / 2f
        val r = big * 0.30f

        PixelArt.flatEllipse(pm, cx, cy + r * 0.95f, r * 0.85f, r * 0.24f, Color(0f, 0f, 0f, 0.18f))

        // stub tail
        PixelArt.flatCircle(pm, cx + r * 0.78f, cy + r * 0.6f, r * 0.24f, Color(0.88f, 0.91f, 0.97f, 1f))

        // ears
        PixelArt.flatCircle(pm, cx - r * 0.55f, cy - r * 0.85f, r * 0.32f, Color(0.93f, 0.94f, 0.99f, 1f))
        PixelArt.flatCircle(pm, cx + r * 0.55f, cy - r * 0.85f, r * 0.32f, Color(0.93f, 0.94f, 0.99f, 1f))
        PixelArt.flatCircle(pm, cx - r * 0.55f, cy - r * 0.85f, r * 0.15f, Color(0.62f, 0.82f, 0.96f, 1f))
        PixelArt.flatCircle(pm, cx + r * 0.55f, cy - r * 0.85f, r * 0.15f, Color(0.62f, 0.82f, 0.96f, 1f))

        // feet
        PixelArt.flatEllipse(pm, cx - r * 0.42f, cy + r * 0.85f, r * 0.22f, r * 0.16f, Color(0.62f, 0.82f, 0.96f, 1f))
        PixelArt.flatEllipse(pm, cx + r * 0.42f, cy + r * 0.85f, r * 0.22f, r * 0.16f, Color(0.62f, 0.82f, 0.96f, 1f))

        // body
        PixelArt.flatCircle(pm, cx, cy, r * 1.08f, outline())
        PixelArt.gradientCircle(pm, cx, cy, r, Color(1f, 1f, 1f, 1f), Color(0.85f, 0.88f, 0.95f, 1f))
        PixelArt.gradientCircle(pm, cx, cy + r * 0.1f, r * 0.7f, Color(0.78f, 0.91f, 1f, 1f), Color(0.5f, 0.74f, 0.92f, 1f))
    }

    private fun bakeFruit(type: FruitType): Texture {
        val main = when (type) {
            FruitType.BERRY -> Color(0.85f, 0.20f, 0.28f, 1f)
            FruitType.GRAPE -> Color(0.55f, 0.30f, 0.78f, 1f)
            FruitType.MELON -> Color(0.36f, 0.72f, 0.38f, 1f)
        }
        val light = main.cpy().lerp(Color.WHITE, 0.45f)
        val dark = main.cpy().lerp(Color.BLACK, 0.22f)

        return PixelArt.bake(SIZE) { pm, big ->
            val cx = big / 2f
            val cy = big / 2f
            val r = big * 0.30f

            PixelArt.flatEllipse(pm, cx, cy + r * 0.95f, r * 0.85f, r * 0.22f, Color(0f, 0f, 0f, 0.16f))
            PixelArt.flatRect(pm, cx - r * 0.08f, cy - r * 1.18f, r * 0.16f, r * 0.32f, Color(0.30f, 0.55f, 0.30f, 1f))
            PixelArt.flatTriangle(
                pm,
                cx + r * 0.06f, cy - r * 1.0f,
                cx + r * 0.34f, cy - r * 1.22f,
                cx + r * 0.1f, cy - r * 1.3f,
                Color(0.42f, 0.70f, 0.40f, 1f)
            )
            PixelArt.flatCircle(pm, cx, cy, r * 1.08f, outline())
            PixelArt.gradientCircle(pm, cx, cy, r, light, dark)
            PixelArt.flatCircle(pm, cx - r * 0.35f, cy - r * 0.35f, r * 0.22f, Color(1f, 1f, 1f, 0.6f))
        }
    }

    private fun bakeEnemy(type: EnemyType): Texture {
        val main = when (type) {
            EnemyType.PATROL -> Color(0.95f, 0.60f, 0.15f, 1f)
            EnemyType.WANDERER -> Color(0.93f, 0.83f, 0.22f, 1f)
            EnemyType.ICE_CRUSHER -> Color(0.42f, 0.48f, 0.95f, 1f)
            EnemyType.CHASER -> Color(0.90f, 0.27f, 0.52f, 1f)
        }
        val light = main.cpy().lerp(Color.WHITE, 0.35f)
        val dark = main.cpy().lerp(Color.BLACK, 0.22f)
        val shade = main.cpy().mul(0.76f, 0.76f, 0.76f, 1f)

        return PixelArt.bake(SIZE) { pm, big ->
            val cx = big / 2f
            val cy = big / 2f
            val r = big * 0.30f

            PixelArt.flatEllipse(pm, cx, cy + r * 0.95f, r * 0.85f, r * 0.24f, Color(0f, 0f, 0f, 0.18f))

            when (type) {
                EnemyType.PATROL -> {
                    PixelArt.flatTriangle(pm, cx - r * 0.55f, cy - r * 0.35f, cx - r * 0.85f, cy - r * 1.15f, cx - r * 0.1f, cy - r * 0.75f, shade)
                    PixelArt.flatTriangle(pm, cx + r * 0.55f, cy - r * 0.35f, cx + r * 0.85f, cy - r * 1.15f, cx + r * 0.1f, cy - r * 0.75f, shade)
                    PixelArt.flatCircle(pm, cx, cy, r * 1.08f, outline())
                    PixelArt.gradientCircle(pm, cx, cy, r, light, dark)
                    PixelArt.flatTriangle(pm, cx - r * 0.18f, cy + r * 0.5f, cx + r * 0.18f, cy + r * 0.5f, cx, cy + r * 0.95f, shade)
                }
                EnemyType.WANDERER -> {
                    PixelArt.flatRect(pm, cx - r * 0.05f, cy - r * 1.3f, r * 0.1f, r * 0.45f, main)
                    PixelArt.flatCircle(pm, cx, cy - r * 1.35f, r * 0.16f, main)
                    PixelArt.flatTriangle(pm, cx - r * 0.95f, cy + r * 0.1f, cx - r * 0.55f, cy + r * 0.65f, cx - r * 0.25f, cy + r * 0.05f, shade)
                    PixelArt.flatTriangle(pm, cx + r * 0.95f, cy + r * 0.1f, cx + r * 0.55f, cy + r * 0.65f, cx + r * 0.25f, cy + r * 0.05f, shade)
                    PixelArt.flatCircle(pm, cx, cy, r * 1.08f, outline())
                    PixelArt.gradientCircle(pm, cx, cy, r, light, dark)
                }
                EnemyType.ICE_CRUSHER -> {
                    PixelArt.diamond(pm, cx, cy, r * 1.1f, r * 1.1f, outline())
                    PixelArt.diamond(pm, cx, cy, r * 0.92f, r * 0.92f, main)
                    PixelArt.flatTriangle(pm, cx - r * 0.3f, cy - r * 0.2f, cx, cy - r * 0.6f, cx + r * 0.1f, cy - r * 0.1f, light)
                    PixelArt.flatTriangle(pm, cx - r * 0.2f, cy + r * 0.15f, cx + r * 0.35f, cy + r * 0.5f, cx + r * 0.05f, cy + r * 0.6f, dark)
                }
                EnemyType.CHASER -> {
                    PixelArt.flatTriangle(pm, cx - r * 0.65f, cy - r * 0.5f, cx - r * 0.95f, cy - r * 1.25f, cx - r * 0.2f, cy - r * 0.85f, shade)
                    PixelArt.flatTriangle(pm, cx + r * 0.65f, cy - r * 0.5f, cx + r * 0.95f, cy - r * 1.25f, cx + r * 0.2f, cy - r * 0.85f, shade)
                    PixelArt.flatCircle(pm, cx, cy, r * 1.08f, outline())
                    PixelArt.gradientCircle(pm, cx, cy, r, light, dark)
                    PixelArt.flatTriangle(pm, cx - r * 0.12f, cy + r * 0.75f, cx + r * 0.12f, cy + r * 0.75f, cx, cy + r * 1.05f, Color(0.98f, 0.98f, 1f, 1f))
                }
            }

            PixelArt.flatCircle(pm, cx - r * 0.35f, cy - r * 0.15f, r * 0.26f, Color.WHITE)
            PixelArt.flatCircle(pm, cx + r * 0.35f, cy - r * 0.15f, r * 0.26f, Color.WHITE)
            PixelArt.flatCircle(pm, cx - r * 0.35f, cy - r * 0.15f, r * 0.12f, Color(0.10f, 0.10f, 0.12f, 1f))
            PixelArt.flatCircle(pm, cx + r * 0.35f, cy - r * 0.15f, r * 0.12f, Color(0.10f, 0.10f, 0.12f, 1f))
        }
    }
}
