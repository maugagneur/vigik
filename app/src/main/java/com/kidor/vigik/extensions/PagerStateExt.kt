package com.kidor.vigik.extensions

import androidx.compose.foundation.pager.PagerState

/**
 * Returns the actual offset of the given page.
 *
 * @param page The page from which the offset will be calculated.
 */
fun PagerState.offsetForPage(page: Int): Float = currentPage - page + currentPageOffsetFraction

/**
 * Returns the offset from the left of the given page.
 *
 * @param page The page from which the offset will be calculated.
 */
fun PagerState.startOffsetForPage(page: Int): Float = offsetForPage(page).coerceAtLeast(0f)

/**
 * Returns the offset from the right of the given page.
 *
 * @param page The page from which the offset will be calculated.
 */
fun PagerState.endOffsetForPage(page: Int): Float = offsetForPage(page).coerceAtMost(0f)
