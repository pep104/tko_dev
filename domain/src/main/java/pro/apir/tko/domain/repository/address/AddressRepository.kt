package pro.apir.tko.domain.repository.address

import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.AddressModel
import pro.apir.tko.domain.model.LocationModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
interface AddressRepository {

    suspend fun getAddressSuggestions(query: String, locationModel: LocationModel? = null): Resource<List<AddressModel>>

    suspend fun getAddressDetailed(query: String): Resource<List<AddressModel>>

    suspend fun getAddressByLocation(locationModel: LocationModel, radius: Int): Resource<List<AddressModel>>

}