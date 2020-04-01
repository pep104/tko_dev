package pro.apir.tko.data.mapper

import pro.apir.tko.domain.failure.TrackingFailureCode

/**
 * Created by antonsarmatin
 * Date: 2020-04-01
 * Project: tko-android
 */
interface TrackingFailureCodeMapper {

    fun getEnumFromCode(str: String): TrackingFailureCode
}