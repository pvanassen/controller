package nl.pvanassen.led.animation

import kotlinx.serialization.Serializable

/**
 * Registration data
 *
 * @param name Name of the animation (if name is already present on remote, it will be replaced)
 * @param type What type of animation?
 * @param cron Cron on when to run, required (and only parsed) when type is TIMED.
 *               See <a href="https://github.com/Scogun/kcron-common">KCron</a> for syntax
 */
@Serializable
data class Registration(val name: String,
                        val type: AnimationType = AnimationType.NORMAL,
                        val cron: String = "")

