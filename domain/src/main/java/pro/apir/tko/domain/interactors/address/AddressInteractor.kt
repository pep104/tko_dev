package pro.apir.tko.domain.interactors.address

import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.AddressModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
interface AddressInteractor {

    suspend fun getAddressSuggestions(query: String, useLocation: Boolean = true): Resource<List<AddressModel>>

    suspend fun getAddressDetailed(query: String): Resource<List<AddressModel>>

    suspend fun getAddressDetailed(addressModel: AddressModel): Resource<AddressModel>

}