package pro.apir.tko.data.framework.network.model.response

import pro.apir.tko.domain.model.UserModel

/**
 * Created by Антон Сарматин
 * Date: 22.02.2020
 * Project: tko-android
 */
data class UserInfoResponse(val id: Int) {

    fun toModel() = UserModel(id)
}