package com.miscell.lucky

import android.accessibilityservice.AccessibilityService
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

open class ReplayService:AccessibilityService(){
    var recorder:ActionRecorder? = null
    var unworking =true
    lateinit var player:ActionPlayer
    lateinit var actions:List<Action>
    /**
     * loop replay when fail or success
     */
    var loop=true
    /**
     * will call this function when start replay
     */
    lateinit var start:()->Unit
    /**
     * will call this function when after replay
     */
    lateinit var end:()->Unit

    fun start_record(){
        recorder=ActionRecorder()
        unworking =false
    }

    fun stop_record(){
        if(recorder!=null) {
            actions = recorder?.actions!!
            recorder = null
        }
        unworking =true
    }
    fun replay(){
        player= ActionPlayer(actions)
        unworking =false
    }
    override fun onInterrupt() {

    }
    override fun onServiceConnected() {
        super.onServiceConnected()
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if(unworking ==true)
            return
        recorder?.onAction(this,event!!)
        try{
            player.onEvent(this,event!!)
        }catch(e:NodeNotFoundException) {
            unworking =true
            recovery()
            e.printStackTrace()
        }catch(e:EndOfReplay){
            unworking =true
            recovery()
        }
    }
    fun recovery(){
        end()
        if(loop)
        {
            start()
            player.start()
            unworking =false
        }
    }
}