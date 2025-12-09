import java.io.File
import kotlin.random.Random

fun main() {
    // Reads the list of words from the specific file path
    val words = readWordList("data/words.txt")

    if (words.isEmpty()) {
        println("Error: Could not read words from data/words.txt. Please check the file exists.")
        return
    }

    // Picks a random word to be the answer
    val target = pickRandomWord(words) ?: return 

    // Sets the maximum number of attempts allowed
    val maxAttempts = 10
    var hasWon = false

    println("Welcome to Wordle! You have $maxAttempts attempts.")

    // Loops through the allowed number of attempts
    for (attempt in 1..maxAttempts) {
        var guess = ""
        
        // Keeps asking for input until the user provides a valid 5 letter word
        while (true) {
            guess = obtainGuess(attempt)
            if (isValid(guess)) {
                break
            } else {
                println("Invalid guess. Please enter exactly 5 letters.")
            }
        }

        // Checks the guess against the target word
        val results = evaluateGuess(guess, target)
        
        // Shows the user how they did
        displayGuess(guess, results)

        // Checks if the user won by seeing if all results are 1 so a match
        if (results.all { it == 1 }) {
            println("Congratulations! You guessed the word correctly.")
            hasWon = true
            break
        }
    }

    // If the loop finishes and they haven't won, let them know the word
    if (!hasWon) {
        println("Game over! You ran out of guesses.")
        println("The word was: $target")
    }
}

// Checks if the word has exactly 5 letters
fun isValid(word: String): Boolean {
    return word.length == 5
}

// Reads the file line by line, keeps only the valid words, and makes them uppercase
fun readWordList(filename: String): MutableList<String> {
    val words = mutableListOf<String>()
    try {
        File(filename).forEachLine { line ->
            if (isValid(line)) {
                words.add(line.uppercase())
            }
        }
    } catch (e: Exception) {
        // Returns an empty list if there is an error reading the file
        return mutableListOf()
    }
    return words
}

// Selects a random word from the list and removes it to prevent repeats
fun pickRandomWord(words: MutableList<String>): String? {
    if (words.isEmpty()) return null
    val randomIndex = Random.nextInt(words.size)
    val word = words[randomIndex]
    words.removeAt(randomIndex)
    return word
}

// Prompts the user for a guess and cleans up the input
fun obtainGuess(attempt: Int): String {
    print("Attempt $attempt: ")
    return readlnOrNull()?.trim()?.uppercase() ?: ""
}

// Compares the guess to the target. Returns 1 for a match and 0 for no match
fun evaluateGuess(guess: String, target: String): List<Int> {
    return guess.mapIndexed { index, char ->
        if (char == target[index]) {
            1 
        } else {
            0 
        }
    }
}

// Prints the guess, replacing incorrect letters with a '?'
fun displayGuess(guess: String, matches: List<Int>) {
    val sb = StringBuilder()
    for (i in guess.indices) {
        if (matches[i] == 1) {
            sb.append(guess[i]) 
        } else {
            sb.append("?") 
        }
    }
    println(sb.toString())
}