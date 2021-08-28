package pro.apir.tko.domain.interactors.map

import kotlinx.coroutines.flow.Flow
import pro.apir.tko.domain.model.BBoxModel
import pro.apir.tko.domain.model.map.MapPointModel
import pro.apir.tko.domain.repository.map.MapPointRepository
import javax.inject.Inject


class MapPointInteractorImpl @Inject constructor(
    private val mapPointRepository: MapPointRepository
): MapPointInteractor {

    override fun getMapPoints(): Flow<List<MapPointModel>> = mapPointRepository.getMapPoints()

    override suspend fun fetch(bbox: BBoxModel) = mapPointRepository.fetch(bbox)

}