package com.kidor.vigik.ui.compose

/**
 * Metadata of each screen of the application.
 */
interface AppDestination {
    val name: String
    val route: String
}

object AppNavigation {

    object CheckScreen : AppDestination {
        override val name: String = "WIP: Check"
        override val route: String = "check"
    }

    object HubScreen : AppDestination {
        override val name: String = "WIP: Hub"
        override val route = "hub"
    }

    object ScanScreen : AppDestination {
        override val name: String = "WIP: Scan"
        override val route = "scan"
    }

    object HistoryScreen : AppDestination {
        override val name: String = "WIP: History"
        override val route = "history"
    }

    object EmulateScreen : AppDestination {
        override val name: String = "WIP: Emulate"
        override val route = "emulate"
    }

    private val allScreens = listOf(
        CheckScreen,
        HubScreen,
        ScanScreen,
        HistoryScreen,
        EmulateScreen
    )

    /**
     * Returns the screen matching given [route] if exists.
     */
    fun getScreen(route: String?): AppDestination? = allScreens.find { it.route == route }
}
