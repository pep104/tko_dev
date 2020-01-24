package pro.apir.tko.data.repository.address

import pro.apir.tko.core.exception.Failure
import pro.apir.tko.core.functional.Either
import pro.apir.tko.domain.model.AddressModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-23
 * Project: tko-android
 */
interface AddressRepository {

    suspend fun getAddressSuggestions(query: String): Either<Failure, List<AddressModel>>

    suspend fun getAddressDetailed(query: String): Either<Failure, List<AddressModel>>

}