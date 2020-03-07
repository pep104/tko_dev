package pro.apir.tko.data.mapper

import pro.apir.tko.data.framework.room.entity.PhotoEntity
import pro.apir.tko.domain.model.PhotoModel

class PhotoTypeMapperImpl : PhotoTypeMapper {

    override fun toModel(entity: PhotoEntity): PhotoModel {
        return when(entity.type){
            "local" -> PhotoModel.LocalFile(entity.id, entity.path)
            "remote" -> PhotoModel.RemoteFile(entity.id, entity.path)
            else -> throw ClassCastException("Missing type identifier")
        }
    }

}