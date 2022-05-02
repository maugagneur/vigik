package com.kidor.vigik.extensions

import com.kidor.vigik.db.model.RoomTag
import com.kidor.vigik.nfc.model.Tag

data class TagMapperDataSet(val tag: Tag, val roomTag: RoomTag)
