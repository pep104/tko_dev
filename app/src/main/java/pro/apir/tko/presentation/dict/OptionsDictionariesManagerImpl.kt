package pro.apir.tko.presentation.dict

import pro.apir.tko.core.types.Dictionary
import pro.apir.tko.core.types.dictOf

class OptionsDictionariesManagerImpl : OptionsDictionariesManager {

    private val accessOptions by lazy {
        dictOf(
                "FREE" to "Подъездные пути свободны",
                "" to ""
        )
    }

    private val basementOptions by lazy {
        dictOf(
                "" to "",
                "" to "",
                "" to "",
                "" to ""
        )
    }

    private val fenceOptions by lazy {
        dictOf(
                "" to "",
                "" to "",
                "" to ""
        )
    }

    private val remotenessOptions by lazy {
        dictOf(
                "" to "",
                "" to "",
                "" to "",
                "" to ""
        )
    }

    private val coverageOptions by lazy {
        dictOf(
                "" to "",
                "" to "",
                "" to ""
        )
    }

    private val kgoOptions by lazy {
        dictOf(
                "" to "",
                "" to "",
                "" to ""
        )
    }

    override fun getAccessOptionDictionary(): Dictionary = accessOptions
    override fun getBasementOptionsDictionary(): Dictionary = basementOptions
    override fun getFenceOptionsDictionary(): Dictionary = fenceOptions
    override fun getRemotenessOptionsDictionary(): Dictionary = remotenessOptions
    override fun getCoverageOptionsDictionary(): Dictionary = coverageOptions
    override fun getKGOOptionsDictionary(): Dictionary = kgoOptions

}