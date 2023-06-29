package com.kidor.vigik.ui.restapi

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from REST API view.
 */
sealed class RestApiViewAction : ViewAction {

    /**
     * Action to refresh tracker's data.
     */
    object RefreshData : RestApiViewAction()
}
