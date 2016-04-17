package com.miscell.lucky

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEventSource
import android.view.accessibility.AccessibilityNodeInfo
import java.util.*

open class ActionRecorder{
    var action=-1
    val actions=ArrayList<Action>()

    var scrolled=false/*List will scroll when init,record only when re-scrolled */
    fun onAction(service: AccessibilityService, event: AccessibilityEvent){

        val record={
            val root=service.rootInActiveWindow
            val node=event.source
            actions.add(actionOf(event.action,root,node))
        }

        val scroll={
            if(scrolled) {
                val root = service.rootInActiveWindow
                val node = event.source
                actions.add(actionOf(event.action, root, node))
            }
            scrolled=true
        }
        val reset={
            scrolled=false;
            action=-1
        }
        when(event.action){
            AccessibilityEvent.TYPE_VIEW_CLICKED->record
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED->record
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED->record
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED->record
            AccessibilityEvent.TYPE_VIEW_SCROLLED->{scroll}
            else->reset
        }
    }

    fun actionOf(action: Int, root: AccessibilityNodeInfo, node:AccessibilityNodeInfo):Action{
        if(node.viewIdResourceName!=null)
            return Action(action,node.text?.toString(),null);
        val path=ArrayList<Int>()
        path.run {
            var seek=node
            do{
                val parent=seek.parent
                i@for(i in 0..parent.childCount){
                    if(seek===parent.getChild(i))
                    {
                        add(i)
                        break@i
                    }
                    assert(false,{"Unable find target node"})
                }
                seek=parent
            }while(seek!=root)
        }
        path.reverse()
        return Action(action,node.text?.toString(),path)
    }
}
