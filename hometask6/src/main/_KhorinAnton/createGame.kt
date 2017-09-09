package _KhorinAnton

import bloxorz.Direction
import bloxorz.Game
import bridges.*
import java.util.*
import bridges.BridgeState.*
import org.apache.commons.codec.net.QCodec
import java.time.Year

// Your solution should live in this folder/package only (rename _SurnameName accordingly.)
// You may add as many subpackages as you need, but the function 'createGame' below should live in the root _SurnameName package.
// Please DON'T copy the interface 'Game' here.

fun createGame(board: String, bridgesInfo: BridgesInfo? = null): Game {
    val lines = board.split("\n")
    val width = lines.maxBy { it.length }!!.length
    val str = " ".repeat(width + 8)
    val strings = mutableListOf(str, str)
    strings.addAll(lines.map { s -> "    " + s + " ".repeat(width - s.length + 4) })
    strings.addAll(listOf(str, str))
    val positions = HashMap<Pair<Int, Int>, Char>()
    val cells = strings.mapIndexed { x, s ->
        s.filterIndexed { y, c -> y % 2 == 0 }.mapIndexed { y, c ->
            {
                if (c.isLetter()) positions.put(x to y, c)
                Cell(x, y, charToType(c))
            }.invoke()
        }
    }
    return GameImpl(Board(cells), BridgesAndSwitches(bridgesInfo ?: BridgesInfo(HashMap(), HashMap()), positions))
}

class Cell(val x: Int, val y: Int, val type: CellType) {

}

enum class CellType {
    REGULAR, LIGHT, START, TARGET, SWITCH, BRIDGE, EMPTY
}

fun charToType(c: Char?) = when (c) {
    '*' -> CellType.REGULAR
    '.' -> CellType.LIGHT
    'S' -> CellType.START
    'T' -> CellType.TARGET
    in 'A'..'Z' -> CellType.SWITCH
    in 'a'..'z' -> CellType.BRIDGE
    else -> CellType.EMPTY
}

fun typeToChar(t: CellType) = when (t) {
    CellType.REGULAR -> '*'
    CellType.LIGHT -> '.'
    CellType.START -> 'S'
    CellType.TARGET -> 'T'
    else -> null
}

class Block(var x: Int, var y: Int, var rotation: Rotation) {

    fun move(direction: Direction) = when (direction) {
        Direction.UP -> {
            x--
            rotation = when (rotation) {
                Rotation.X -> Rotation.Z
                Rotation.Y -> Rotation.Y
                Rotation.Z -> {
                    x--
                    Rotation.X
                }
            }
        }
        Direction.RIGHT -> {
            y++
            rotation = when (rotation) {
                Rotation.X -> Rotation.X
                Rotation.Y -> {
                    y++
                    Rotation.Z
                }
                Rotation.Z -> Rotation.Y
            }
        }
        Direction.DOWN -> {
            x++
            rotation = when (rotation) {
                Rotation.X -> {
                    x++
                    Rotation.Z
                }
                Rotation.Y -> Rotation.Y
                Rotation.Z -> Rotation.X
            }
        }
        Direction.LEFT -> {
            y--
            rotation = when (rotation) {
                Rotation.X -> Rotation.X
                Rotation.Y -> Rotation.Z
                Rotation.Z -> {
                    y--
                    Rotation.Y
                }
            }
        }
    }

    fun secondPiece() = when (rotation) {
        Rotation.X -> x + 1 to y
        Rotation.Y -> x to y + 1
        Rotation.Z -> null
    }
}

enum class Rotation {
    X, Y, Z
}

class BridgesAndSwitches(val bridgesInfo: BridgesInfo, val positions: Map<Pair<Int, Int>, Char>) {
    val bridgesState = HashMap<Char, BridgeState>(bridgesInfo.bridges.mapValues { it.value.initialState })

    fun bridgeState(x: Int, y: Int) = bridgesState[positions[x to y]]

    fun toChar(x: Int, y: Int) = if (bridgeState(x, y) == CLOSED) null else positions[x to y]

    fun onSwitch(cell: Cell, full: Boolean) {
        val switch = bridgesInfo.switches[positions[cell.x to cell.y]]
        if (switch != null && (full || switch.type == SwitchType.HALF_BLOCK)) {
            when (switch.action) {
                SwitchAction.OPEN -> bridgesState[switch.bridge.name] = OPENED
                SwitchAction.CLOSE -> bridgesState[switch.bridge.name] = CLOSED
                SwitchAction.CHANGE -> if (bridgesState[switch.bridge.name] == OPENED) bridgesState[switch.bridge.name] = CLOSED
                else bridgesState[switch.bridge.name] = OPENED
            }
        }
    }
}

class Board(val cells: List<List<Cell>>) {

    val height = cells.size - 4
    val width = cells[0].size - 4
    val start = cells.first { it.any { it.type == CellType.START } }.first { it.type == CellType.START }
    val target = cells.find { it.any { it.type == CellType.TARGET } }?.find { it.type == CellType.TARGET }
}

class Snapshot(val curr: Long, val prev: Snapshot?, val step: Direction) {
    override fun hashCode(): Int = curr.hashCode()

    override fun equals(other: Any?): Boolean = other is Snapshot && other.curr == curr
}

class GameImpl(val board: Board, val bridgesAndSwitches: BridgesAndSwitches) : Game {

    override val height = board.height
    override val width = board.width
    val block = Block(board.start.x, board.start.y, Rotation.Z)


    override fun getCellValue(i: Int, j: Int): Char? {
        val x = i + 1
        val y = j + 1
        return if (block.x == x && block.y == y || block.secondPiece()?.first == x && block.secondPiece()?.second == y) 'x'
        else bridgesAndSwitches.toChar(x, y) ?: typeToChar(getCell(x, y).type)
    }

    override fun processMove(direction: Direction) {
        block.move(direction)
        if (block.rotation == Rotation.Z) {
            if (fallBlockOnCell(getCell(block.x, block.y))) init()
        } else {
            val pair = block.secondPiece()!!
            if (fallHalfBlockOnCell(getCell(block.x, block.y)) || fallHalfBlockOnCell(getCell(pair.first, pair.second))) init()
        }
    }

    override fun hasWon(): Boolean = block.x == board.target?.x && block.y == board.target?.y && block.rotation == Rotation.Z

    override fun toString(): String = board.cells.map {
        it.map { getCellValue(it.x - 1, it.y - 1) ?: ' ' }.drop(2).dropLast(2).joinToString(" ")
    }.drop(2).dropLast(2).joinToString("\n")

    override fun suggestMoves(): List<Direction>? {
        val visited: MutableSet<Snapshot> = HashSet()
        val queue: Queue<Snapshot> = LinkedList<Snapshot>()
        queue.add(Snapshot(encode(), null, Direction.UP))
        visited.add(queue.element())
        var curr: Snapshot
        while (!queue.isEmpty()) {
            curr = queue.remove()
            decode(curr)
            if (hasWon()) {
                val ans = ArrayList<Direction>()
                while (curr.prev != null) {
                    ans.add(curr.step)
                    curr = curr.prev!!
                }
                decode(curr)
                return ans.reversed()
            } else {
                makeMove(curr, visited, queue, Direction.UP)
                makeMove(curr, visited, queue, Direction.RIGHT)
                makeMove(curr, visited, queue, Direction.DOWN)
                makeMove(curr, visited, queue, Direction.LEFT)
            }
        }
        return null
    }

    fun getCell(x: Int, y: Int) = board.cells[x][y]

    fun init() {
        bridgesAndSwitches.bridgesState.clear()
        bridgesAndSwitches.bridgesState.putAll(bridgesAndSwitches.bridgesInfo.bridges.mapValues { it.value.initialState })
        block.x = board.start.x
        block.y = board.start.y
        block.rotation = Rotation.Z
    }

    fun fallBlockOnCell(cell: Cell): Boolean {
        if (cell.type == CellType.EMPTY || cell.type == CellType.LIGHT || bridgesAndSwitches.bridgeState(cell.x, cell.y) == CLOSED) {
            return true;
        }
        bridgesAndSwitches.onSwitch(cell, true)
        return false
    }

    fun fallHalfBlockOnCell(cell: Cell): Boolean {
        if (cell.type == CellType.EMPTY || bridgesAndSwitches.bridgeState(cell.x, cell.y) == CLOSED) {
            return true;
        }
        bridgesAndSwitches.onSwitch(cell, false)
        return false
    }

    fun encode(): Long {
        var code: Long = 0
        val one: Long = 1
        bridgesAndSwitches.bridgesState.forEach { c, state -> if (state == OPENED) code += one.shl(c - 'a') }
        code += ((block.x - 2) * width + (block.y - 2) + width * height * when (block.rotation) {
            Rotation.X -> 0
            Rotation.Y -> 1
            Rotation.Z -> 2
        }).toLong().shl(26)
        return code
    }

    fun decode(snapshot: Snapshot) {
        val code = snapshot.curr
        val one: Long = 1
        bridgesAndSwitches.bridgesState.forEach { c, state -> bridgesAndSwitches.bridgesState.put(c, if (code.and(one.shl(c - 'a')) == 0L) CLOSED else OPENED) }
        val size: Long = (width * height).toLong()
        var code2 = code.shr(26)
        if (code2 < size) {
            block.rotation = Rotation.X
        } else if (code2 < 2 * size) {
            block.rotation = Rotation.Y
            code2 -= size
        } else {
            block.rotation = Rotation.Z
            code2 -= 2 * size
        }
        block.x = (code2 / width + 2).toInt()
        block.y = (code2 % width + 2).toInt()
    }

    fun makeMove(curr: Snapshot, visited: MutableSet<Snapshot>, queue: Queue<Snapshot>, direction: Direction) {
        processMove(direction)
        val snapshot = Snapshot(encode(), curr, direction)
        if (visited.add(snapshot)) {
            queue.add(snapshot)
        }
        decode(curr)
    }
}

