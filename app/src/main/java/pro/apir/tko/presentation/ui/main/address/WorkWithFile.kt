package pro.apir.tko.presentation.ui.main.address

import java.io.File

class WorkWithFile {
    var fileName = "Data.txt"
    val file = File(fileName)

    fun writeToFile(lat: String, lon: String) {
        file.appendText("$lat $lon")
    }

    fun readFromFile(){
        file.forEachLine {

        }
    }

}