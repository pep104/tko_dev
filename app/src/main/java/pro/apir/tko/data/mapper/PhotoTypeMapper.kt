package pro.apir.tko.data.mapper

import pro.apir.tko.data.framework.room.entity.PhotoEntity
import pro.apir.tko.domain.model.PhotoModel

/**
 * Created by Антон Сарматин
 * Date: 07.03.2020
 * Project: tko-android
 */
interface PhotoTypeMapper {

    fun toModel(entity: PhotoEntity): PhotoModel


}