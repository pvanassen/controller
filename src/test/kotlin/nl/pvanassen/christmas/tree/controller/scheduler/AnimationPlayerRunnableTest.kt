package nl.pvanassen.christmas.tree.controller.scheduler

import assertk.assert
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class AnimationPlayerRunnableTest {
    @Test
    fun testGetWaitTimes() {
        val result = AnimationPlayerRunnable.getWaitTimes(16666666, 13391)
        assert(result.first).isEqualTo(16)
        assert(result.second).isEqualTo(653275)
    }
}