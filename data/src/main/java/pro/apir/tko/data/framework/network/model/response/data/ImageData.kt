package pro.apir.tko.data.framework.network.model.response.data

import pro.apir.tko.domain.model.ImageModel

/**
 * Created by antonsarmatin
 * Date: 2020-01-20
 * Project: tko-android
 */
data class ImageData(val image: Int,
                     val url: String) {

    fun toModel() = ImageModel( image, url)

}