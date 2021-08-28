package pro.apir.tko.presentation.ui.blocked

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.fragment_blocked.view.*
import pro.apir.tko.R
import pro.apir.tko.core.exception.Failure
import pro.apir.tko.presentation.platform.BaseFragment

/**
 * Created by antonsarmatin
 * Date: 28/08/2021
 * Project: tko
 */
class BlockedFragment: BaseFragment() {


    override fun layoutId() = R.layout.fragment_blocked

    override fun handleFailure() = MutableLiveData<Failure>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.colorPrimary)

        view.btnExit.setOnClickListener {
            requireActivity().finish()
        }

    }


}