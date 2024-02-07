package com.muzo.sitesupervisor.feature.fragment.webView

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.muzo.sitesupervisor.R
import com.muzo.sitesupervisor.core.constans.Constants.Companion.URL
import com.muzo.sitesupervisor.databinding.FragmentPdfBinding
import com.muzo.sitesupervisor.databinding.FragmentVebViewBinding

class VebViewFragment : Fragment() {
    private lateinit var binding: FragmentVebViewBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentVebViewBinding.inflate(layoutInflater, container, false)
        observeView()
        navigateRegister()
        return binding.root
    }

    private fun observeView(){
        val webView = binding.webView
        val progressBar = binding.progressBar

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.INVISIBLE
            }
        }
        webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(URL)
    }

    private fun navigateRegister(){
        binding.fabBtn.setOnClickListener {
            findNavController().popBackStack(R.id.registerFragment,false)
        }
    }

}