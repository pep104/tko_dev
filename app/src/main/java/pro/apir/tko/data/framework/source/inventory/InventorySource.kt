package pro.apir.tko.data.framework.source.inventory

import pro.apir.tko.data.framework.network.api.InventoryApi
import pro.apir.tko.data.framework.network.model.response.ContainersResponse
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by Антон Сарматин
 * Date: 18.01.2020
 * Project: tko-android
 */
class InventorySource @Inject constructor(retrofit: Retrofit) : InventoryApi {

    private val api by lazy { retrofit.create(InventoryApi::class.java) }

    override suspend fun getContainers(page: Int, pageSize: Int, location: String): Response<ContainersResponse> {
        return api.getContainers(page, pageSize, location)
    }
}