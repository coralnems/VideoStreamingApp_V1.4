package io.github.jitinsharma.bottomnavbar

import android.view.animation.Interpolator

class BounceInterpolator(private var amplitude: Double, private var frequency: Double) : Interpolator {

    override fun getInterpolation(input: Float): Float =
            (-1 * Math.pow(Math.E, -input/amplitude) * Math.cos(frequency * input) + 1).toFloat()

}