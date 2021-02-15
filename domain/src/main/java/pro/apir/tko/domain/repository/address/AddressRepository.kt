package pro.apir.tko.domain.repository.address

import pro.apir.tko.core.data.Resource
import pro.apir.tko.domain.model.AddressModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
interface AddressRepository {

    suspend fun getAddressSuggestions(query: String): Resource<List<AddressModel>>

    suspend fun getAddressDetailed(query: String): Resource<List<AddressModel>>

}