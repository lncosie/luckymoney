package com.miscell.lucky

import android.accessibilityservice.AccessibilityService
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

open class ReplayService:AccessibilityService(){
    var recorder:ActionRecorder? = null
    lateinit var player:ActionPlayer
    lateinit var actions:List<Action>

    var loop=true
    var wait=true
    lateinit var start:()->Unit
    lateinit var end:()->Unit

    fun start_record(){
        recorder=ActionRecorder()
        wait=false
    }
    fun stop_record(){
        if(recorder!=null) {
            actions = recorder?.actions!!
            recorder = null
        }
        wait=true
    }
    fun replay(){
        player= ActionPlayer(actions)
        wait=false
    }
    override fun onInterrupt() {

    }
    override fun onServiceConnected() {
        super.onServiceConnected()
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if(wait==true)
            return
        recorder?.onAction(this,event!!)
        try{
            player.onEvent(this,event!!)
        }catch(e:NodeNotFoundException) {
            wait=true
            recovery()
            e.printStackTrace()
        }
    }
    fun recovery(){
        end()
        if(loop)
        {
            start()
            player.start()
        }
    }
}