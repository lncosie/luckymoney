package com.miscell.lucky
class NodeNotFoundException(val node:String):Throwable(node,null){

}
class EndOfReplay():Throwable(null,null){

}