package _2StateMachine.moreTestsForTask2

import _2StateMachine.task2.*
import org.junit.Test
import kotlin.test.assertEquals

class TestDifferentStateMachines {
    @Test
    fun testStateMachine1() {
        val stateMachine = createStateMachine1()
        val (commands, finalState) = startMachine(stateMachine, "whistle", "finishLine")
        assertEquals("finish", finalState)
        assertEquals(listOf("run", "stop"), commands)
    }

    @Test
    fun testStateMachine2() {
        val stateMachine = createStateMachine2()
        val (commands, finalState) = startMachine(stateMachine, "on", "off")
        assertEquals("switch_off", finalState)
        assertEquals(listOf(), commands)
    }

    @Test
    fun testStateMachine3_1() {
        val stateMachine = createStateMachine3()
        val actions = listOf("on", "off")
        val (commands, finalState) = startMachine(stateMachine, actions)
        assertEquals("switch_off", finalState)
        assertEquals(actions, commands)
    }

    @Test
    fun testStateMachine3_2() {
        val stateMachine = createStateMachine3()
        val actions = listOf("on", "off", "on")
        val (commands, finalState) = startMachine(stateMachine, actions)
        assertEquals("switch_on", finalState)
        assertEquals(actions, commands)
    }

    @Test
    fun testStateMachine3_3() {
        val stateMachine = createStateMachine3()
        val actions = listOf("on", "off", "on", "off")
        val (commands, finalState) = startMachine(stateMachine, actions)
        assertEquals("switch_off", finalState)
        assertEquals(actions, commands)
    }
}