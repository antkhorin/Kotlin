package _2Board

fun createSquareBoard(width: Int): SquareBoard = BoardImpl<Nothing>(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = BoardImpl<T>(width)

private class CellImpl<T>(override val i: Int, override val j: Int, var item: T?) : Cell {}

private class BoardImpl<T>(override val width: Int, val cells: List<List<CellImpl<T>>> = (0 until width).map { first -> (0 until width).map { CellImpl<T>(first + 1, it + 1, null) } }) : SquareBoard, GameBoard<T> {

    override fun getCell(i: Int, j: Int) = getCellOrNull(i, j) ?: throw IllegalArgumentException("Please, enter correct coordinates")

    override fun getCellOrNull(i: Int, j: Int) = if (i in 1..width && j in 1..width) cells[i - 1][j - 1] else null

    override fun getAllCells() = cells.reduce { list1, list2 -> list1 + list2 }

    override fun getRow(i: Int, jRange: IntProgression) = jRange.filter { it in 1..width }.map { getCell(i, it) }

    override fun getColumn(iRange: IntProgression, j: Int) = iRange.filter { it in 1..width }.map { getCell(it, j) }

    override fun Cell.getNeighbour(direction: Direction) = when (direction) {
        Direction.UP -> getCellOrNull(i - 1, j)
        Direction.RIGHT -> getCellOrNull(i, j + 1)
        Direction.DOWN -> getCellOrNull(i + 1, j)
        else -> getCellOrNull(i, j - 1)
    }

    override fun get(cell: Cell) = get(cell.i, cell.j)

    override fun set(cell: Cell, value: T?) = set(cell.i, cell.j, value)

    override fun get(i: Int, j: Int) = getCellOrNull(i, j)?.item

    override fun set(i: Int, j: Int, value: T?) {
        getCellOrNull(i, j)?.item = value
    }

    override fun contains(value: T) = getAllCells().filter { it.item == value }.any()

    override fun filter(predicate: (T?) -> Boolean) = getAllCells().filter { predicate(it.item) }

    override fun any(predicate: (T?) -> Boolean) = getAllCells().any { predicate(it.item) }

    override fun all(predicate: (T?) -> Boolean) = getAllCells().all { predicate(it.item) }

}