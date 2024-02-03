package com.muzo.sitesupervisor.feature.fragment.userFragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.auth.User
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.common.hide
import com.muzo.sitesupervisor.core.common.show
import com.muzo.sitesupervisor.core.common.toastMessage
import com.muzo.sitesupervisor.core.constans.Constants
import com.muzo.sitesupervisor.core.data.model.UserInfo
import com.muzo.sitesupervisor.databinding.FragmentUserBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserBinding
    private val viewModel: UserFragmentViewModel by viewModels()
    private lateinit var sp: SharedPreferences
    private lateinit var list: UserInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUserBinding.inflate(layoutInflater, container, false)
        initViews()
        observeData()
        logOutEvent()
        return binding.root
    }
    private fun initViews(){
        viewModel.getSiteSuperVisorInfo(viewModel.currentUser)
    }

    private fun logOutEvent() {
        binding.btn.setOnClickListener {
            viewModel.logOut()
            sp = requireActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putBoolean(Constants.KEY_IS_ENTERED, false)
            editor.apply()
            toastMessage("Uygulamadan çıkılıyor tekrar giriş yapın",requireContext())
            requireActivity().finish()
        }
    }



    private fun observeData() {
        lifecycleScope.launch {
            viewModel.infoState.collect {infoState->
                when{
                    infoState.loading->{
                        binding.progressBar.show()
                        binding.cvWhite.hide()
                    }
                    infoState.userInfoList!=null-> {
                        list=infoState.userInfoList
                        setText(list)
                        binding.progressBar.hide()
                        binding.cvWhite.show()
                    }
                }

            }
        }
    }

    private fun setText(list:UserInfo){
        val name=list.name
        val eMail=list.email
        binding.name.text=name
        binding.mail.text=eMail
    }

}