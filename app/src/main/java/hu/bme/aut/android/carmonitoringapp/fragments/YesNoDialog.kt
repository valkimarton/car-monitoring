package hu.bme.aut.android.carmonitoringapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.TextView
import hu.bme.aut.android.carmonitoringapp.R
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
            val inflater = requireActivity().layoutInflater
            val innerView: View = inflater.inflate(R.layout.save_dialog_content, null)

            builder.setView(innerView)
                .setMessage("Would you like to save this lap?")
                .setPositiveButton("Save",
                    DialogInterface.OnClickListener { dialog, id ->
                        val nameView: TextView = innerView.findViewById(R.id.text_dialog_lapname)
                        val name: String = (nameView.text).toString()
                        listener.onSaveLap(name)
                    })
                .setNegativeButton("Dismiss",
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDismissLap()
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface OnDialogSaveLap {
        fun onSaveLap(name: String)
        fun onDismissLap()
    }
}