import java.io.File
import java.io.FileNotFoundException
import kotlin.random.Random
import java.util.NoSuchElementException

fun isValid(word: String): Boolean {
    // check if the word length is exactly 5
    return word.length == 5
    }

fun readWordList(filename: String): MutableList<String> {
// reads the file, filters valid words, and makes them uppercase
    return try {
        File(filename)
            .readLines()
            .filter { isValid(it) } 
            .map { it.uppercase() } 
            .toMutableList()
    } catch (e: FileNotFoundException) {
        // return an empty list if the file is not found
        println("Error: Word list file '$filename' not found.")
        mutableListOf() 
    }
}
// Picks and removes a random word from the list to avoid repeats
fun pickRandomWord(words: MutableList<String>): String {
    if (words.isEmpty()) {
        throw NoSuchElementException("Cannot pick a word from an empty list.")
    }
    // removeAt removes the word from the list and returns it at the random index
    return words.removeAt(Random.nextInt(words.size))
}
// keeps prompting the user until a valid guess is entered
fun obtainGuess(attempt: Int): String {
    while (true) {
        print("Attempt $attempt: ")
        val guess = readln()

        if (isValid(guess)) {
            // return the guess in uppercase to match the target word format
            return guess.uppercase()
        } else {
            println("Invalid guess. Must be exactly 5 letters.")
        }
    }
}

fun evaluateGuess(guess: String, target: String): List<Int> {
    // Compare each character in the guess to the target word
    return guess.mapIndexed { index, char ->
        if (char == target[index]) {
            1 // Match
        } else {
            0 // No match
        }
    }
}

fun displayGuess(guess: String, matches: List<Int>) {
    // prints the guess but replaces non-matching letters with '?'
    val output = guess.mapIndexed { index, char ->
        if (matches[index] == 1) {
            char // Keep the character if it's a match
        } else {
            '?' // Replace with '?' if no match
        }
    }.joinToString("") // Join the list of characters back into a String

    println(output)
}