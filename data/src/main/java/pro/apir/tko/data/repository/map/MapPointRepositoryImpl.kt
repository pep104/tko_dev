package pro.apir.tko.data.repository.map

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pro.apir.tko.core.data.onSuccess
import pro.apir.tko.data.framework.network.api.MapApi
import pro.apir.tko.data.framework.network.model.response.data.MapPointData
import pro.apir.tko.domain.model.BBoxModel
import pro.apir.tko.domain.model.map.MapPointModel
import pro.apir.tko.domain.repository.map.MapPointRepository
import javax.inject.Inject

class MapPointRepositoryImpl @Inject constructor(
    private val mapApi: MapApi,
) : MapPointRepository {

    private val mapPoints = MutableStateFlow<List<MapPointModel>>(emptyList())

    override fun getMapPoints() = mapPoints

    override suspend fun fetch(bbox: BBoxModel) {
        val newPointsResponse = mapApi.getPoints(
            lngMin = bbox.lngMin.toString(),
            latMin = bbox.latMin.toString(),
            lngMax = bbox.lngMax.toString(),
            latMax = bbox.latMax.toString()
        )

        newPointsResponse
            .toResult { it.wasteAreaPoints.map(MapPointData::toModel) }
            .onSuccess { points ->
                CoroutineScope(Dispatchers.IO).launch {
                    differAndEmit(points)
                }
            }
    }


    private suspend fun differAndEmit(list: List<MapPointModel>) {
        val currentPoints = mapPoints.value
        val difference = list.filter { !currentPoints.contains(it) }
        mapPoints.emit(currentPoints + difference)
    }

}