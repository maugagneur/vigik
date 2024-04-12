package com.kidor.vigik.ui.home

/**
 * Navigation destination from Home screen.
 */
sealed class HomeNavigation {
    /**
     * Navigation action to animation section.
     */
    data object NavigateToAnimations : HomeNavigation()

    /**
     * Navigation action to biometric section.
     */
    data object NavigateToBiometric : HomeNavigation()

    /**
     * Navigation action to Bluetooth section.
     */
    data object NavigateToBluetooth : HomeNavigation()

    /**
     * Navigation action to bottom sheet section.
     */
    data object NavigateToBottomSheet : HomeNavigation()

    /**
     * Navigation action to camera section.
     */
    data object NavigateToCamera : HomeNavigation()

    /**
     * Navigation action to emoji section.
     */
    data object NavigateToEmoji : HomeNavigation()

    /**
     * Navigation action to NFC section.
     */
    data object NavigateToNfc : HomeNavigation()

    /**
     * Navigation action to notification section.
     */
    data object NavigateToNotification : HomeNavigation()

    /**
     * Navigation action to paging section.
     */
    data object NavigateToPaging : HomeNavigation()

    /**
     * Navigation action to REST API section.
     */
    data object NavigateToRestApi : HomeNavigation()

    /**
     * Navigation action to telephony section.
     */
    data object NavigateToTelephony : HomeNavigation()
}
