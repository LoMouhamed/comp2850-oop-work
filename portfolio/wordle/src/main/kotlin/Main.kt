fun main() {
	val wordFile = "data/words.txt"
	val words = readWordList(wordFile)

	if (words.isEmpty()) {
		println("No words available to play. Check '$wordFile' exists and contains 5-letter words.")
		return
	}

	val target = pickRandomWord(words) ?: run {
		println("Failed to pick a target word.")
		return
	}

	println("Welcome to Wordle (simple CLI). You have 6 attempts.")

	val maxAttempts = 6
	for (attempt in 1..maxAttempts) {
		val guess = obtainGuess(attempt)
		val matches = evaluateGuess(guess, target)
		displayGuess(guess, matches)

		if (matches.all { it == 2 }) {
			println("Congratulations — you guessed the word: $target")
			return
		}
	}

	println("Out of attempts. The word was: $target")
}
