import com.soywiz.korma.geom.*

// Input

sealed class Action {
    data class Place(val chip: Chip, val column: Int) : Action()
}

// Output

data class BoardResult(val newBoard: Board, val transitionsLists: List<List<Transition>>)

sealed class Transition {
    data class Place(val row: Int, val column: Int, val chip: Chip) : Transition()
    data class Finished(val winner: WinnerResult) : Transition()
}

data class WinnerResult(val winner: Chip, val positions: List<Point>)

enum class Chip(val c: Char) {
    EMPTY('.'), YELLOW('y'), RED('r');

    companion object {
        val map: Map<Char, Chip> = values().associateBy { it.c }
    }
}

data class Board(
        val rows: List<String>
) {
    val nrows get() = rows.size
    val ncols get() = rows[0].length

    constructor(vararg rows: String) : this(rows.toList())

    companion object {
        val EMPTY = Board(
                ".......",
                ".......",
                ".......",
                ".......",
                ".......",
                "......."
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun with(row: Int, col: Int, chip: Chip): Board {
        return Board(rows.mapIndexed { crow: Int, string: String ->
            if (crow == row) {
                val data = string.toCharArray()
                data[col] = chip.c
                String(data)
            } else {
                string
            }
        })
    }

    fun get(row: Int, col: Int): Chip {
        if (row !in 0 until nrows) return Chip.EMPTY
        if (col !in 0 until ncols) return Chip.EMPTY
        val r = rows[row]
        val c = r[col]
        return Chip.map[c]!!
    }

    fun height(col: Int): Int {
        for (n in 0 until nrows) {
            val row = nrows - 1 - n
            if (get(row, col) == Chip.EMPTY) return n
        }
        return nrows
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getAvailableActions(chip: Chip): List<Action> {
        return buildList<Action> {
            for (col in 0 until ncols) {
                val action = Action.Place(chip, col)
                if (apply(action) != null) {
                    add(action)
                }
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun apply(action: Action): BoardResult? {
        when (action) {
            is Action.Place -> {
                val col = action.column
                val row = 5 - height(col)
                val chip = action.chip
                if (row < 0) return null
                val newBoard = with(row, col, chip)
                return BoardResult(
                        newBoard,
                        ArrayList<List<Transition>>().apply {
                            add(listOf(Transition.Place(row, col, chip)))
                            val winner = newBoard.winner()
                            if (winner != null) {
                                add(listOf(Transition.Finished(winner)))
                            }
                        }
                )
            }
        }
    }

    override fun toString(): String {
        return rows.joinToString("\n")
    }

    internal fun checkWinner(row: Int, col: Int, dx: Int, dy: Int): WinnerResult? {
        val firstChip = get(row, col)
        if (firstChip == Chip.EMPTY) return null
        val list = arrayListOf<Point>()
        list.add(Point(col, row))
        for (n in 0 until 4) {
            val r = row + (dy * n)
            val c = col + (dx * n)
            if (get(r, c) != firstChip) {
                return null
            }
            list.add(Point(c, r))
        }
        return WinnerResult(firstChip, list)
    }

    fun winner(): WinnerResult? {
        for (col in 0 until ncols) {
            for (row in 0 until nrows) {
                val chip1 = checkWinner(row, col, +1, 0)
                val chip2 = checkWinner(row, col, 0, +1)
                val chip3 = checkWinner(row, col, -1, -1)
                val chip4 = checkWinner(row, col, -1, +1)
                if (chip1 != null) return chip1
                if (chip2 != null) return chip2
                if (chip3 != null) return chip3
                if (chip4 != null) return chip4
            }
        }
        return null
    }
}

