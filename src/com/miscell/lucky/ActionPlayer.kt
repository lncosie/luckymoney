package com.miscell.lucky

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

open class ActionPlayer(val actions: List<Action>) {
    var index = 0
    fun start(){
        index=0
    }
    fun onEvent(service: AccessibilityService, event: AccessibilityEvent) {
        val act = event.source.actions
        var repaly = actions.get(index)
        if (act == repaly.action) {

            index = index + 1
            if(index==actions.size)
            {
                return
            }
            fire_next(service, actions.get(index))
        }
    }

    private fun fire_next(service: AccessibilityService, action: Action) {
        val fire = {
            val node = seekTo(service.rootInActiveWindow, action)
            node.performAction(action.action)
        }
        val scroll = {
            val node = seekTo(service.rootInActiveWindow, action)
            throw NotImplementedError()
            node.performAction(AccessibilityEvent.TYPE_VIEW_SCROLLED)
        }
        when (action.action) {
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> scroll
            AccessibilityEvent.TYPE_VIEW_CLICKED -> fire
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> fire
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