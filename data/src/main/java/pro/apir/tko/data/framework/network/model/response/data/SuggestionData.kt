package pro.apir.tko.data.framework.network.model.response.data

import com.google.gson.annotations.SerializedName
import pro.apir.tko.core.constant.extension.round
import pro.apir.tko.domain.model.AddressModel

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
    fun toModel() = AddressModel(value, unrestrictedValue, data.lat?.round(6), data.lng?.round(6))

}