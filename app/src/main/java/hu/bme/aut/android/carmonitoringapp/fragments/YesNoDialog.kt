package hu.bme.aut.android.carmonitoringapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment


class YesNoDialog : DialogFragment() {

    companion object {
        fun newInstance(title: String): YesNoDialog{
            val dialog = YesNoDialog()
            val args = Bundle()
            // args.putString("title", title)
            // dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Would you like to save this lap?")
                .setPositiveButton("Save",
                    DialogInterface.OnClickListener { dialog, id ->
                        println("SAVE!")
                    })
                .setNegativeButton("Dismiss",
                    DialogInterface.OnClickListener { dialog, id ->
                        println("DISMISS!")
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}