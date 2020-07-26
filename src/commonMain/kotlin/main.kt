import com.soywiz.kds.*
import com.soywiz.klock.*
import com.soywiz.korge.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.input.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korio.async.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*

suspend fun main() = Korge(width = 448, height = 384, bgcolor = Colors["#2b2b2b"]) {
	var board = Board.EMPTY
	val ncols = board.ncols
	val nrows = board.nrows
	val bitmapChipRed = Bitmap32(64, 64).context2d { fill(Colors.RED) { circle(32.0, 32.0, 24.0) } }
	val bitmapChipYellow = Bitmap32(64, 64).context2d { fill(Colors.YELLOW) { circle(32.0, 32.0, 24.0) } }
	val bitmapChipEmpty = Bitmap32(64, 64)
	val bitmapBoard = Bitmap32(64 * ncols, 64 * nrows).context2d {
		fill(Colors.BLUE) {
			rect(0.0, 0.0, 64 * ncols, 64 * nrows)
			for (row in 0 until nrows) {
				for (col in 0 until ncols) {
					circle(32.0 + col * 64, 32.0 + row * 64, 20.0)
				}
			}
		}
	}

	fun getPositionInPixels(col: Int, row: Int): Point {
		return Point(col * 64, row * 64)
	}

	val boardContainer = container {  }
	image(bitmapBoard)

	var turn = Chip.RED

	fun getBitmapFromChip(chip: Chip): Bitmap32 = when (chip) {
		Chip.EMPTY -> bitmapChipEmpty
		Chip.YELLOW -> bitmapChipYellow
		Chip.RED -> bitmapChipRed
	}

	val boardView = Array2<View>(ncols, nrows) { DummyView() }

	suspend fun performAction(action: Action) {
		val result = board.apply(action)!!
		board = result.newBoard

		animate {
			sequence {
				for (transitions in result.transitionsLists) {
					parallel {
						for (transition in transitions) {
							when (transition) {
								is Transition.Place -> {
									val col = transition.column
									val startPoint = getPositionInPixels(col, -1)
									val row = transition.row
									val endPoint = getPositionInPixels(col, row)
									val view = boardContainer.image(getBitmapFromChip(transition.chip))
											.position(startPoint)

									boardView[col, row] = view

									view.moveTo(endPoint.x, endPoint.y)
								}
								is Transition.Finished -> {
									for (pos in transition.winner.positions) {
										val view = boardView[pos.x.toInt(), pos.y.toInt()]
										println(view)
										view.hide()
									}
								}
							}
						}
					}
				}
			}
		}
	}

	//boardContainer.image(bitmapChipRed).position(64, 64)
	for (col in 0 until ncols) {
		val rect = solidRect(64.0, 64.0 * nrows, Colors.TRANSPARENT_BLACK)

		fun updateHighlight() {
			rect.color = when (turn) {
				Chip.RED -> Colors.RED.withAd(0.2)
				else -> Colors.YELLOW.withAd(0.2)
			}
		}

		rect.position(64.0 * col, 0.0).mouse {
			over {
				updateHighlight()
			}
			out {
				rect.color = Colors.TRANSPARENT_BLACK
			}
			click {
				println("CLICKED on $col")
				launchImmediately {
					if (board.winner() == null) {
						val currentTurn = turn
						val action = Action.Place(currentTurn, col)
						if (board.apply(action) != null) {
							turn = if (turn == Chip.RED) Chip.YELLOW else Chip.RED
							updateHighlight()
							performAction(action)
						}
					} else {
						board = Board.EMPTY
						boardContainer.removeChildren()
					}
				}
			}
		}
	}
}