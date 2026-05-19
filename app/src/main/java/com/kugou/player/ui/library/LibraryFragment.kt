package com.kugou.player.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.kugou.player.R
import com.kugou.player.databinding.FragmentLibraryBinding
import com.kugou.player.model.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeData()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            showLoginDialog()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoggedIn.collect { loggedIn ->
                        binding.loginButton.isVisible = !loggedIn
                    }
                }
                launch {
                    viewModel.loginState.collect { state ->
                        when (state) {
                            is UiState.Error -> {
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                            }
                            is UiState.Success -> {
                                Snackbar.make(binding.root, state.data, Snackbar.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun showLoginDialog() {
        val options = arrayOf("手机号登录", "二维码登录")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("选择登录方式")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showPhoneLoginDialog()
                    1 -> showQrLoginDialog()
                }
            }
            .show()
    }

    private fun showPhoneLoginDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_login, null)
        val etPhone = dialogView.findViewById<android.widget.EditText>(R.id.phone_input)
        val etCode = dialogView.findViewById<android.widget.EditText>(R.id.code_input)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("手机号登录")
            .setView(dialogView)
            .setPositiveButton("登录") { _, _ ->
                val phone = etPhone?.text.toString().trim()
                val code = etCode?.text.toString().trim()
                if (phone.isNotEmpty() && code.isNotEmpty()) {
                    viewModel.loginByPhone(phone, code)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showQrLoginDialog() {
        viewModel.loadQrCode()

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_lyrics, null)
        val tvStatus = dialogView.findViewById<TextView>(android.R.id.text1)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("扫码登录")
            .setView(dialogView)
            .setNegativeButton("取消") { d, _ -> d.dismiss() }
            .create()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.qrCheckStatus.collect { status ->
                    tvStatus?.text = status
                }
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
