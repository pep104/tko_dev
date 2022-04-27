package pro.apir.tko.domain.interactors.address

import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.model.LocationModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
interface AddressInteractor {

    suspend fun getAddressSuggestions(query: String, useLocation: Boolean = true): Resource<List<AddressModel>>

    suspend fun getAddressDetailed(query: String): Resource<List<AddressModel>>

    suspend fun getAddressDetailed(addressModel: AddressModel): Resource<AddressModel>

    suspend fun getAddressByLocation(locationModel: LocationModel): Resource<AddressModel>

    suspend fun getAddressByUser(): Resource<AddressModel>


}