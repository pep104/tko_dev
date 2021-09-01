package pro.apir.tko.data.framework.network.model.response

import com.google.gson.annotations.SerializedName
import pro.apir.tko.data.framework.network.model.BaseResponse
import pro.apir.tko.domain.model.AccessTokenModel

/**
 * Created by Антон Сарматин
 * Date: 17.01.2020
 * Project: tko-android
 */
data class RefreshAccessTokenResponse(
    @SerializedName("access") val accessRefreshed: String,
    @SerializedName("refresh") val refresh: String? = null,
) :
    BaseResponse() {

    fun toModel() = AccessTokenModel(accessRefreshed)

}