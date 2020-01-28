package pro.apir.tko.data.framework.network.model.response.data

import pro.apir.tko.domain.model.ImageModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-20
 * Project: tko-android
 */
data class ImageData(val side: String,
                     val image: Int,
                     val url: String) {

    //TODO EXTRACT MAPPERS FROM MODEL TO MAPPER CLASS
    fun toModel() = ImageModel(side, image, url)

}