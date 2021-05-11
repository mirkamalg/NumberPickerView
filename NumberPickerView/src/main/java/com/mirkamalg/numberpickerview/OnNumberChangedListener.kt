package com.mirkamalg.numberpickerview

/**
 * Created by Mirkamal on 10 May 2021
 */

/**
 * Interface definition for the callback to be fired when number is changed
 */
interface OnNumberChangedListener {

    /**
     * Called once a new number is chosen in the number picker
     */
    fun onChanged(newNumber: Int)
}