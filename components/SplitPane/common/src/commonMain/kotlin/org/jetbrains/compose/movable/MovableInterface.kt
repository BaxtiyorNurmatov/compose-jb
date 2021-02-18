package org.jetbrains.compose.movable

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf

interface SingleDirectionMoveScope {
    fun moveBy(pixels: Float)
}

internal class SingleDirectionMovable(
    val onMoveDelta: (Float) -> Unit
) {

    private val singleDirectionMoveScope = object : SingleDirectionMoveScope {
        override fun moveBy(pixels: Float) = onMoveDelta(pixels)
    }

    private val moveMutex = MutatorMutex()

    private val isMovingState = mutableStateOf(false)

    suspend fun move(
        movePriority: MutatePriority,
        block: suspend SingleDirectionMoveScope.() -> Unit
    ) {
        moveMutex.mutateWith(singleDirectionMoveScope, movePriority) {
            isMovingState.value = true
            block()
            isMovingState.value = false
        }
    }

    val isMoveInProgress: Boolean
        get() = isMovingState.value

    fun dispatchRawMovement(delta: Float) = onMoveDelta(delta)

}