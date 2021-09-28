package sorting

import java.io.File
import java.io.FileReader
import java.util.*


fun main(args: Array<String>) {
    var sortingType = "natural"
    if ("-sortingType" in args) {
        try {
            sortingType = args[args.indexOf("-sortingType") + 1]
        } catch (e: java.lang.Exception) {
            println("No sorting type defined!")
        }
    }
    var dataType = "word"
    if ("-dataType" in args) {
        try {
            dataType = args[args.indexOf("-dataType") + 1]
        } catch (e: Exception) {
            println("No data type defined!")
        }
    }
    val validArgs = setOf("-sortingType", "natural", "byCount", "-dataType", "word", "long", "line", "-inputFile", "-outputFile")
    args.forEach {
//        if ((it !in validArgs) || (!it.endsWith(".txt")) || (!it.endsWith(".dat"))) {
        if (it !in validArgs) {
            println("$it is not a valid parameter. It will be skipped.")
        }
    }

    val outputFile = if ("-outputFile" in args) {
        File(args[args.indexOf("-outputFile") + 1])
    } else {
        null
    }

    fun printOrWriteLine(text: String) {
        when (outputFile) {
            null -> println(text)
            else -> outputFile.appendText(text + "\n")
        }
    }

    fun printOrWrite(text: String) {
        when (outputFile) {
            null -> print(text)
            else -> outputFile.appendText(text)
        }
    }

    fun naturalSort(list: MutableList<String>) {
        printOrWrite("Sorted data:")
        for (element in list.sorted()) printOrWrite(" $element")
    }

    fun naturalSortNum(list: MutableList<Long>) {
        printOrWrite("Sorted data:")
        for (element in list.sorted()) printOrWrite(" $element")
    }

    fun naturalSortWhen(list: MutableList<String>, dataType: String): MutableList<String> {
        return when (dataType) {
            "long" -> list.sortedWith(compareBy { it.toLongOrNull() }).toMutableList()
            else -> list.sorted().toMutableList()
        }
    }

    fun sortByCount(list: MutableList<String>, dataType: String) {
        val occurrences = mutableMapOf<String, Int>()
        val set = list.toSet()
        set.forEach { unique ->
            occurrences[unique] = list.count { it == unique }
        }
        val setOfValues = occurrences.values.toSet()
        val map = mutableMapOf<Int, MutableList<String>>()
        for (value in setOfValues) map[value] = mutableListOf()

        occurrences.forEach {
            val (string, int) = it
            if (int in map.keys) {
                val temp = map[int]!!.toMutableList()
                temp += string
                map[int] = naturalSortWhen(temp, dataType)
            } else {
                map[int] = mutableListOf(string)
            }
        }
        map.toSortedMap().forEach {
            for (value in it.value) {
                printOrWriteLine(
                    "$value: ${it.key} time(s), ${(it.key.toDouble() / list.size.toDouble() * 100).toInt()}%"
                )
            }
        }
    }

    fun sortMy(list: MutableList<String>) =
        if (sortingType == "natural") naturalSort(list) else sortByCount(list, dataType)

    val scanner = when {
        "-inputFile" in args -> Scanner(FileReader(args[args.indexOf("-inputFile") + 1]))
        else -> Scanner(System.`in`)
    }

    when (dataType) {
        "word" -> {
            val words = mutableListOf<String>()
            while (scanner.hasNext()) words += scanner.next()
            scanner.close()
            val message = "Total words: ${words.count()}."
            printOrWriteLine(message)
            sortMy(words)
        }
        "long" -> {
            val strings = mutableListOf<String>()
            val longs = mutableListOf<Long>()
            val notLongs = mutableListOf<String>()
            while (scanner.hasNext()) strings += scanner.next()
            for (string in strings) {
                try {
                    longs.add(string.toLong())
                } catch (e: Exception) {
                    notLongs.add(string)
                }
            }
            if (notLongs.isNotEmpty()) {
                notLongs.forEach { println("$it is not a long. It will be skipped.") }
            }
            while (scanner.hasNext()) longs += scanner.nextLong()
            scanner.close()
            printOrWriteLine("Total numbers: ${longs.count()}.")
            if (sortingType == "natural") {
                naturalSortNum(longs)
            } else {
                sortByCount(longs.map { it.toString() }.toMutableList(), dataType)
            }
        }
        "line" -> {
            val lines = mutableListOf<String>()
            while (scanner.hasNext()) lines += scanner.nextLine()
            scanner.close()
            printOrWriteLine("Total lines: ${lines.count()}.")
            sortMy(lines)
        }
    }
}
