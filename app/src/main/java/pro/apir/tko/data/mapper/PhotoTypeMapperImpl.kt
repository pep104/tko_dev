package pro.apir.tko.data.mapper

import pro.apir.tko.data.framework.room.entity.PhotoEntity
import pro.apir.tko.domain.model.PhotoModel

class PhotoTypeMapperImpl : PhotoTypeMapper {

    override fun toModel(entity: PhotoEntity): PhotoModel {
        return when (entity.type) {
            PhotoModel.Type.LOCAL.name -> PhotoModel(entity.id, PhotoModel.Type.LOCAL, entity.path)
            PhotoModel.Type.REMOTE.name -> PhotoModel(entity.id, PhotoModel.Type.REMOTE, entity.path)
            else -> throw ClassCastException("Missing type identifier")
        }
    }

}