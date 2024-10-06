package com.android.goally.ui.sheet

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.android.goally.R
import com.android.goally.constants.FilterOption
import com.android.goally.databinding.ActivityHomeBinding
import com.android.goally.databinding.FilterOptionsLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterOptionsRightSheet(private val filterOption:FilterOption, private val  lastSelectedOption:String, private  val onOptionSelected:(String) -> Unit)  : BottomSheetDialogFragment() {
    private lateinit var binding: FilterOptionsLayoutBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FilterOptionsLayoutBinding.inflate(inflater, container, false)
        binding.sliderLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

        binding.run {
            when (filterOption) {
                FilterOption.SCHEDULE -> {
                    binding.scheduleOptionsLayout.visibility = View.VISIBLE
                    binding.folderOptionsLayout.visibility = View.GONE

                    if (lastSelectedOption.isNotEmpty()) {
                        for (i in 0 until radioGroupScheduleOptions.childCount) {
                            val radioButton = radioGroupScheduleOptions.getChildAt(i) as? RadioButton
                            if (radioButton?.text.toString() == lastSelectedOption) {
                                radioButton?.isChecked = true
                                break
                            }
                        }
                    }

                }
                FilterOption.FOLDER -> {
                    binding.scheduleOptionsLayout.visibility = View.GONE
                    binding.folderOptionsLayout.visibility = View.VISIBLE

                    if (lastSelectedOption.isNotEmpty()) {
                        for (i in 0 until radioGroupFolderOptions.childCount) {
                            val radioButton = radioGroupFolderOptions.getChildAt(i) as? RadioButton
                            if (radioButton?.text.toString() == lastSelectedOption) {
                                radioButton?.isChecked = true
                                break
                            }
                        }
                    }
                }
            }

        }



        binding.radioGroupScheduleOptions.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = binding.radioGroupScheduleOptions.findViewById<RadioButton>(checkedId)
            onOptionSelected(radioButton.text.toString())
            Handler(Looper.getMainLooper()).postDelayed({ dismiss() }, 100)
        }
        binding.radioGroupFolderOptions.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = binding.radioGroupFolderOptions.findViewById<RadioButton>(checkedId)
            onOptionSelected(radioButton.text.toString())
            Handler(Looper.getMainLooper()).postDelayed({ dismiss() }, 100)
        }

        return binding.root;

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.FullScreenDialog)
        dialog.window?.setGravity(Gravity.END)

        dialog.window?.setWindowAnimations(R.style.RightSlideDialogAnimation)

        dialog.setOnShowListener {

            dialog?.let { sheet ->

                sheet.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )

                dialog.window?.setGravity(Gravity.END) // Align to the right
                dialog.window?.setLayout(

                    (resources.displayMetrics.widthPixels * 0.4).toInt(),   // Set to 75% of the screen width
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

        }
        return dialog
    }
}