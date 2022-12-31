package nl.pvanassen.led.animation

enum class AnimationType {
    /**
     * Normal animation, runs in the loop
     */
    NORMAL,

    /**
     * Only run this animation on startup
     */
    ON_STARTUP,

    /**
     * Only run this animation on shutdown
     */
    ON_SHUTDOWN,

    /**
     * Only run at specific times. Requires a cron spec
     */
    TIMED,

    /**
     * Special new year type. Don't tell!
     */
    FIREWORKS
}