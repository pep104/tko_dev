package pro.apir.tko.data.framework.network.model.response.data

import com.google.gson.annotations.SerializedName
import pro.apir.tko.domain.model.SuggestionModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
data class SuggestionData(
        val value: String,
        @SerializedName("unrestricted_value")
        val unrestrictedValue: String,
        val data: SuggestionAddressData
){

    fun toModel() = SuggestionModel(value, unrestrictedValue, data.lat, data.lng)

}