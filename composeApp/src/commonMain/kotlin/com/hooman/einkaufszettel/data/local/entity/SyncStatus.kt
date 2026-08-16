package com.hooman.einkaufszettel.data.local.entity

/***
 * RSF -> Remote Sync Fail
 * RSL -> Remote Sync Loading
 * LSL -> Local Sync Loading
 * LSS -> Local Sync Success
 */
enum class SyncStatus {
    FAIL, RSF, SUCCESS, RSL, LSL, LSS
}