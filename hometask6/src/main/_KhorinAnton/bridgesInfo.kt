package _KhorinAnton

import bridges.*
import bridges.BridgeState.CLOSED
import bridges.SwitchAction.CHANGE
import bridges.SwitchType.HALF_BLOCK
import java.util.*

// Your solution should live in this folder/package only (rename _SurnameName accordingly.)
// You may add as many subpackages as you need, but the function 'bridgesInfo' below should live in the root _SurnameName package.
// Please DON'T copy the class 'BridgesInfo' and others here.

fun bridgesInfo(init: Info.() -> Unit): BridgesInfo {
    val info = Info(HashMap(), HashMap())
    info.init()
    return BridgesInfo(info.bridges, info.switches)
}

// These functions need receivers to works correctly. The declarations below are only used to have the compiled code.

fun Info.bridge(name: Char, initialState: BridgeState = CLOSED, init: MyBridge.() -> Unit = {}) {
    val myBridge = MyBridge(Bridge(name, initialState), this)
    this.bridges.put(name, myBridge.bridge)
    myBridge.init()
}

fun MyBridge.switch(name: Char, action: SwitchAction = CHANGE, type: SwitchType = HALF_BLOCK) {
    this.info.switches.put(name, Switch(name, this.bridge, action, type))
}

data class Info(val bridges: MutableMap<Char, Bridge>, val switches: MutableMap<Char, Switch>)

data class MyBridge(val bridge: Bridge, val info: Info)