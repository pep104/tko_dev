package pro.apir.tko.data.framework.source.inventory

import pro.apir.tko.data.framework.network.api.InventoryApi
import pro.apir.tko.data.framework.network.model.request.ContainerAreaDetailedRequest
import pro.apir.tko.data.framework.network.model.response.ContainerAreaDetailedResponse
import pro.apir.tko.data.framework.network.model.response.ContainerAreasResponse
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

    override suspend fun getContainerAreas(page: Int, pageSize: Int, location: String): Response<ContainerAreasResponse> {
        return api.getContainerAreas(page, pageSize, location)
    }

    override suspend fun getContainerArea(id: Long): Response<ContainerAreaDetailedResponse> {
        return api.getContainerArea(id)
    }

    override suspend fun updateContainerArea(id: Long, containerArea: ContainerAreaDetailedRequest): Response<ContainerAreaDetailedResponse> {
        return api.updateContainerArea(id, containerArea)
    }
}