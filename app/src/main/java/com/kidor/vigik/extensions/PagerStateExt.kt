package com.kidor.vigik.extensions

import androidx.compose.foundation.pager.PagerState

/**
 * Returns the actual offset of the given page.
 *
 * @param page The page from which the offset will be calculated.
 */
fun PagerState.offsetForPage(page: Int): Float = currentPage - page + currentPageOffsetFraction

