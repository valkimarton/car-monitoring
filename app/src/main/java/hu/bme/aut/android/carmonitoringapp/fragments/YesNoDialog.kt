package hu.bme.aut.android.carmonitoringapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import hu.bme.aut.android.carmonitoringapp.RecordLapActivity
import hu.bme.aut.android.carmonitoringapp.model.Lap


class YesNoDialog : DialogFragment() {

    private lateinit var listener: OnDialogSaveLap

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnDialogSaveLap) {
            listener = context
        } else {
            throw ClassCastException(context.toString() + " doesnt implement OnDialogSaveLap interface." +
                    "DialogFragment can not send data back this way.")
        }
    }

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
                        listener.onDialogSaveLap(true)
                    })
                .setNegativeButton("Dismiss",
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDialogSaveLap(false)
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface OnDialogSaveLap {
        fun onDialogSaveLap(addLap: Boolean)
    }
}