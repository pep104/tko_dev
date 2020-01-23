package pro.apir.tko.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
data class SuggestionModel(val value: String,
                           val unrestrictedValue: String,
                           val lat: Double?,
                           val lng: Double?)