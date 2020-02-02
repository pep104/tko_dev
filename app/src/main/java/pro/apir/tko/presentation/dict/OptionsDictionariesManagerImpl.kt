package pro.apir.tko.presentation.dict

import android.content.Context
import pro.apir.tko.core.types.Dictionary
import pro.apir.tko.core.types.dictOf
import javax.inject.Inject

//Strings to resources
class OptionsDictionariesManagerImpl @Inject constructor(private val context: Context) : OptionsDictionariesManager {

    private val accessOptions by lazy {
        dictOf(
                "FREE" to "Подъездные пути свободны",
                "NOT_FREE" to "Подъездные пути не свободны"
        )
    }

    private val fenceOptions by lazy {
        dictOf(
                "CORRESPONDS" to "Соответствует",
                "NOT_CORRESPONDS" to "Не соответствуе",
                "NEEDS_REPAIR" to "Требует ремонта"
        )
    }

    private val coverageOptions by lazy {
        dictOf(
                "ASPHALT" to "Асфальт",
                "CONCRETE" to "Бетон",
                "SOIL" to "Грунт"
        )
    }

    private val kgoOptions by lazy {
        dictOf(
                "PERSENT" to "Есть",
                "ABSENT" to "Нет",
                "NOT_PROVIDED" to "Не предусмотрена"
        )
    }

    override fun getAccessOptionDictionary(): Dictionary = accessOptions
    override fun getFenceOptionsDictionary(): Dictionary = fenceOptions
    override fun getCoverageOptionsDictionary(): Dictionary = coverageOptions
    override fun getKGOOptionsDictionary(): Dictionary = kgoOptions

}