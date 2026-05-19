package com.kugou.player.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kugou.player.R
import com.kugou.player.databinding.FragmentSettingsBinding
import com.kugou.player.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSettings()
        setupClickListeners()
    }

    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

        val apiUrl = prefs.getString(Constants.KEY_API_BASE_URL, Constants.DEFAULT_API_BASE_URL)
            ?: Constants.DEFAULT_API_BASE_URL
        binding.apiUrlInput.setText(apiUrl)

        val quality = prefs.getInt(Constants.KEY_QUALITY, Constants.QUALITY_320)
        when (quality) {
            Constants.QUALITY_128 -> binding.quality128.isChecked = true
            Constants.QUALITY_320 -> binding.quality320.isChecked = true
            Constants.QUALITY_FLAC -> binding.qualityFlac.isChecked = true
        }
    }

    private fun setupClickListeners() {
        binding.apiUrlInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveApiUrl()
            }
        }

        binding.qualityRadio.setOnCheckedChangeListener { _, checkedId ->
            val quality = when (checkedId) {
                R.id.quality_128 -> Constants.QUALITY_128
                R.id.quality_320 -> Constants.QUALITY_320
                R.id.quality_flac -> Constants.QUALITY_FLAC
                else -> Constants.QUALITY_320
            }
            val prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().putInt(Constants.KEY_QUALITY, quality).apply()
        }

        binding.clearCacheButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("清除缓存")
                .setMessage("确定要清除所有缓存数据吗？")
                .setPositiveButton("确定") { _, _ ->
                    try {
                        requireContext().cacheDir.deleteRecursively()
                        Toast.makeText(requireContext(), "缓存已清除", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "清除失败: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun saveApiUrl() {
        val url = binding.apiUrlInput.text.toString().trim()
        if (url.isNotEmpty()) {
            val prefs = requireContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(Constants.KEY_API_BASE_URL, url).apply()
        }
    }

    override fun onDestroyView() {
        saveApiUrl()
        super.onDestroyView()
        _binding = null
    }
}
