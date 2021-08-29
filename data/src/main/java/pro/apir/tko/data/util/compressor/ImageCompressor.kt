package pro.apir.tko.data.util.compressor

import android.content.Context
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import java.io.File
import javax.inject.Inject

/**
 * Created by antonsarmatin
 * Date: 29/08/2021
 * Project: tko
 */
interface ImageCompressor {

    suspend fun forAttachent(file: File): File

}


class CompressorImpl @Inject constructor(val context: Context) : ImageCompressor {

    override suspend fun forAttachent(file: File): File {
        return Compressor.compress(context, file) {
            quality(75)
        }
    }


}