package com.fredymarleon.eventbuspattern.adapters

import com.fredymarleon.eventbuspattern.eventsBus.SportEvent

interface OnClickListener {
    fun onClick(result: SportEvent.ResultSuccess)
}