package pro.apir.tko.presentation.ui.dialog.addcontainer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_add_container.view.*
import pro.apir.tko.R

/**
 * Created by antonsarmatin
 * Date: 2020-02-03
 * Project: tko-android
 */
class AddContainerDialog(private var listener: AddContainerListener? = null) : DialogFragment(){


    interface AddContainerListener{

        //TODO CONTAINER MODEL
        fun onNewContainer()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
                R.layout.dialog_add_container,
                container,
                false
        );

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.btnDismiss.setOnClickListener {
            dismissAllowingStateLoss()
        }

        view.btnSubmit.setOnClickListener {
            listener?.onNewContainer()
            dismissAllowingStateLoss()
        }

    }

    override fun onDestroy() {
        listener = null
        super.onDestroy()
    }

}