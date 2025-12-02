import java.io.File
import kotlin.random.Random

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
	} catch (e: Exception) {
		// If file read fails, return an empty list
		mutableListOf()
	}
}

fun pickRandomWord(words: MutableList<String>): String? {
	if (words.isEmpty()) return null
	return words.removeAt(Random.nextInt(words.size))
}

fun obtainGuess(attempt: Int): String {
	while (true) {
		print("Attempt $attempt: ")
		val guess = readlnOrNull() ?: ""
		if (isValid(guess)) return guess.uppercase()
		println("Invalid guess. Must be exactly 5 letters.")
	}
}

fun evaluateGuess(guess: String, target: String): List<Int> {
	val result = MutableList(5) { 0 }
	val targetChars = target.toMutableList()
	val guessChars = guess.toMutableList()

	// Pass 1: perfect matches (2)
	for (i in 0 until 5) {
		if (guessChars[i] == targetChars[i]) {
			result[i] = 2
			targetChars[i] = '\u0000'
			guessChars[i] = '\u0001'
		}
	}

	// Pass 2: partial matches (1)
	for (i in 0 until 5) {
		if (result[i] != 2) {
			val ch = guessChars[i]
			val idx = targetChars.indexOf(ch)
			if (idx != -1) {
				result[i] = 1
				targetChars[idx] = '\u0000'
			}
		}
	}
	return result
}

fun displayGuess(guess: String, matches: List<Int>) {
	val output = guess.mapIndexed { index, char ->
		when (matches[index]) {
			2 -> char.toString()
			1 -> char.toString().lowercase()
			else -> "?"
		}
	}.joinToString(" ")
	println("Hint: $output")
}
