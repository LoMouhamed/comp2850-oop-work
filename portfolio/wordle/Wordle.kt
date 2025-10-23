import java.io.File
import java.io.FileNotFoundException
import kotlin.random.Random
import java.util.NoSuchElementException

fun isValid(word: String): Boolean {
    return word.length == 5 && word.all { it.isLetter() }
}

fun readWordList(filename: String): MutableList<String> {
    return try {
        File(filename)
            .readLines()
            .filter { isValid(it) } 
            .map { it.uppercase() } 
            .toMutableList()
    } catch (e: FileNotFoundException) {
        println("Error: Word list file '$filename' not found.")
        mutableListOf() 
    }
}

fun pickRandomWord(words: MutableList<String>): String {
    if (words.isEmpty()) {
        throw NoSuchElementException("Cannot pick a word from an empty list.")
    }
    return words.removeAt(Random.nextInt(words.size))
}

fun obtainGuess(attempt: Int): String {
    while (true) {
        print("Attempt $attempt: ")
        // readln() reads a line from standard input
        val guess = readln()

        if (isValid(guess)) {
            // Normalize to uppercase for consistent comparison
            return guess.uppercase()
        } else {
            println("Invalid guess. Must be exactly 5 letters.")
        }
    }
}

fun evaluateGuess(guess: String, target: String): List<Int> {
    // mapIndexed gives us the index and the character at that index
    return guess.mapIndexed { index, char ->
        if (char == target[index]) {
            1 // Match
        } else {
            0 // No match
        }
    }
}

fun displayGuess(guess: String, matches: List<Int>) {
    // Create the output string by mapping each character
    val output = guess.mapIndexed { index, char ->
        if (matches[index] == 1) {
            char // Keep the character if it's a match
        } else {
            '?' // Replace with '?' if no match
        }
    }.joinToString("") // Join the list of characters back into a String

    println(output)
}