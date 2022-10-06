package com.daniellemarsh.ujianbro.extension

import androidx.annotation.FloatRange

fun Float.fraction(@FloatRange(from = 0.0, to = 1.0) f: Float): Float {
	return times(f)
}

fun Int.fraction(@FloatRange(from = 0.0, to = 1.0) f: Float): Float {
	return times(f)
}
