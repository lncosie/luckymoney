package com.miscell.lucky

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

open class ActionPlayer(val actions: List<Action>) {
    var index = 0
    fun start() {
        index = 0
    }

    fun onEvent(service: AccessibilityService, event: AccessibilityEvent) {
        val act = event.source.actions
        var repaly = actions.get(index)
        if (AccessibilityEvent.TYPE_VIEW_SCROLLED == act) {
            fire_scroll(service,repaly)
            return
        } else if (act == repaly.action) {
            index = index + 1
            if (index == actions.size) {
                throw EndOfReplay()
            }
            repaly = actions.get(index)
            fire_next(service, repaly)
        }
    }

    private fun fire_scroll(service: AccessibilityService, action: Action) {

        val node = seekTo(service.rootInActiveWindow, action)
        node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
    }

    private fun fire_next(service: AccessibilityService, action: Action) {
        val fire = {
            e: Int ->
            val node = seekTo(service.rootInActiveWindow, action)
            node.performAction(e)
        }

        when (action.action) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> fire(AccessibilityNodeInfo.ACTION_CLICK)
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> fire(AccessibilityNodeInfo.ACTION_LONG_CLICK)
        }
    }

    fun seekTo(root: AccessibilityNodeInfo, action: Action): AccessibilityNodeInfo {
        val message = lazy { action.text?.toString() ?: action.seek.toString() }
        try {
            if (action.text != null) {
                return root.findAccessibilityNodeInfosByText(action.text).get(0)
            }
            val seek = action.seek?.iterator()!!
            var node = root
            for (i in seek) {
                node = node.getChild(i)
            }
            if (node.text?.toString() != action.text) {
                throw NodeNotFoundException(message.value)
            }
            return node
        } catch(e: Exception) {
            throw NodeNotFoundException(message.value)
        }
    }
}