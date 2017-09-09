package mastermind

import java.util.*

fun playMastermind(
        secret: String = generateSecret(),
        player: Player = RealPlayer()
) {
    var complete: Boolean
    do {
        val guess = player.guess()
        val secretList = secret.toMutableList()
        val guessList = guess.toMutableList()
        var fullMatch = 0
        var partialMatch = 0
        for (i in 3 downTo 0) {
            if (guess[i] == secret[i]) {
                fullMatch++
                secretList.removeAt(i)
                guessList.removeAt(i)
            }
        }
        for (i in secretList.size - 1 downTo 0) {
            for (j in 0 until guessList.size) {
                if (secretList[i] == guessList[j]) {
                    partialMatch++
                    secretList.removeAt(i)
                    guessList.removeAt(j)
                    break
                }
            }
        }
        complete = fullMatch == 4
        player.receiveEvaluation(complete, fullMatch, partialMatch)
    } while (!complete)
}

fun generateSecret(
        differentLetters: Boolean = true
): String {
    val random = Random()
    val list = mutableListOf('A', 'B', 'C', 'D', 'E', 'F')
    return buildString {
        for (i in 0..3) {
            val pos = random.nextInt(6 - i)
            append(list[pos])
            if (differentLetters) list.removeAt(pos)
        }
    }
}