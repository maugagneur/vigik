package com.kidor.vigik.extensions

import com.kidor.vigik.db.model.RoomTag
import com.kidor.vigik.nfc.model.Tag

/**
 * Converts a [Tag] into [RoomTag].
 */
fun Tag.convert(): RoomTag = RoomTag(
    timestamp = timestamp,
    uid = uid,
    techList = techList,
    data = data,
    id = id
)

/**
 * Converts a [RoomTag] into [Tag].
 */
fun RoomTag.convert(): Tag = Tag(
    timestamp = timestamp,
    uid = uid,
    techList = techList,
    data = data,
    id = id
)

/**
 * Converts a list of [RoomTag] into a list of [Tag].
 */
fun List<RoomTag>.convert(): List<Tag> = this.map { it.convert() }