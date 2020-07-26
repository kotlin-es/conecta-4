import kotlin.test.*

class ModelTest {
    @Test
    fun testGet() {
        val board = Board(
            "r.",
            ".y"
        )

        assertEquals(Chip.RED, board.get(0, 0))
        assertEquals(Chip.EMPTY, board.get(0, 1))
        assertEquals(Chip.EMPTY, board.get(1, 0))
        assertEquals(Chip.YELLOW, board.get(1, 1))
    }

    @Test
    fun testHeight() {
        val board = Board(
                "...r",
                "..rr",
                ".ryr"
        )

        assertEquals(0, board.height(0))
        assertEquals(1, board.height(1))
        assertEquals(2, board.height(2))
        assertEquals(3, board.height(3))
   }

    @Test
    fun testPlaceChipInEmptyBoard() {
        assertEquals(
            Board(
                ".......",
                ".......",
                ".......",
                ".......",
                ".......",
                "......y"
            ),
            Board(
                ".......",
                ".......",
                ".......",
                ".......",
                ".......",
                "......."
            ).apply(Action.Place(Chip.YELLOW, 6)).newBoard
        )
    }

    @Test
    fun testPlaceChipInBoardWithAChip() {
        assertEquals(
            BoardResult(Board(
                ".......",
                ".......",
                ".......",
                ".......",
                "......y",
                "......r"
            ), listOf(listOf(Transition.Place(4, 6, Chip.YELLOW)))),
            Board(
                ".......",
                ".......",
                ".......",
                ".......",
                ".......",
                "......r"
            ).apply(Action.Place(Chip.YELLOW, 6))
        )
    }

    @Test
    fun testWinner() {
        assertEquals(
                Chip.RED,
                Board(
                    ".......",
                    ".......",
                    ".......",
                    ".......",
                    ".......",
                    "...rrrr"
                ).winner()?.winner
        )
    }

    @Test
    fun testCheckWinner() {
        val board = Board(
                ".......",
                ".......",
                ".......",
                ".......",
                ".......",
                "...rrrr"
        )
        assertEquals(
                Chip.RED,
                board.checkWinner(5, 3, +1, 0)?.winner
        )
    }
}
