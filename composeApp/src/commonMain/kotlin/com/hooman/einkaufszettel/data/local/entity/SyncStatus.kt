package com.hooman.einkaufszettel.data.local.entity

import com.hooman.einkaufszettel.core.util.KeepForFirebase
import kotlinx.serialization.Serializable

/***
 * RSF -> Remote Sync Fail
 * RSL -> Remote Sync Loading
 * LSL -> Local Sync Loading
 * LSS -> Local Sync Success
 */

@Serializable
@KeepForFirebase
enum class SyncStatus {
    FAIL, RSF, SUCCESS, RSL, LSL, LSS
}