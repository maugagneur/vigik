package com.kidor.vigik.extensions

import com.kidor.vigik.data.tag.model.RoomTag
import com.kidor.vigik.data.nfc.model.Tag

/**
 * Converts a [Tag] into [RoomTag].
 */
fun Tag.toRoomTag(): RoomTag = RoomTag(
    timestamp = timestamp,
    uid = uid,
    techList = techList,
    data = data,
    id = id
)

/**
 * Converts a [RoomTag] into [Tag].
 */
fun RoomTag.toTag(): Tag = Tag(
    timestamp = timestamp,
    uid = uid,
    techList = techList,
    data = data,
    id = id
)

/**
 * Converts a list of [RoomTag] into a list of [Tag].
 */
fun List<RoomTag>.toTagList(): List<Tag> = this.map { it.toTag() }
