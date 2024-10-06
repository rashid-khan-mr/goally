package com.android.goally.ui.home

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Bundle
import android.os.Looper
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.goally.BaseActivity
import com.android.goally.R
import com.android.goally.constants.FilterOption
import com.android.goally.databinding.ActivityHomeBinding
import com.android.goally.databinding.InternetdialogBinding
import com.android.goally.ui.adapters.CopiletListAdapter
import com.android.goally.ui.sheet.FilterOptionsRightSheet
import com.android.goally.util.AppUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import java.util.logging.Handler


@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var intenetDialog: Dialog
    private lateinit var copilotListAdapter: CopiletListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupViews()
        setupObservers()
        fetchCopilet()


    }


    private fun fetchCopilet() {
        generalViewModel.fetchCopilot(
            preferenceUtil.getToken("Authentication").toString(),
            onLoading = {
                binding.run {
                    if (it) {
                        binding.progressBar.visibility = View.VISIBLE
                    } else {
                        binding.progressBar.visibility = View.GONE

                    }
                }
            }, onError = {
                errorDialog(reason = it)
                if (generalViewModel.copilotList.value.isNullOrEmpty()) {
                    binding.noCopiletsLayout.visibility = View.VISIBLE
                    fadeinEmptyLayout(1)
                }
            })

    }

    fun onCopiletClick(copiletId: String) {
        startActivity(
            Intent(this@HomeActivity, CopiletDetailsActivity::class.java)
                .putExtra(
                    "copiletId", copiletId
                )
        )
    }

    private fun setupViews() {
        binding.run {

            //val batteryDrawable = batteryIcon.drawable as LevelListDrawable
            //batteryDrawable.setLevel(batteryLevel)


            copiletListRV.layoutManager =
                LinearLayoutManager(this@HomeActivity) // Set LayoutManager
            copilotListAdapter = CopiletListAdapter(this@HomeActivity, ::onCopiletClick)
            copiletListRV.adapter = copilotListAdapter

            homeTopBar.copiletTitleTv.setText(getString(R.string.longNameTitle))
            homeTopBar.timeTv.setText(DateFormat.getTimeFormat(this@HomeActivity).format(Date()))
            binding.homeTopBar.batteryIcon.setImageResource(AppUtil.updateBatteryLevel())

            homeTopBar.backBtn.setOnClickListener(View.OnClickListener {
                this@HomeActivity.onBackPressedDispatcher.onBackPressed()
            })

            filterScheduleLayout.setOnClickListener(View.OnClickListener {
                openFilterDialog(FilterOption.SCHEDULE)
                /*val dialog = FilterOptionsRightSheet(
                   FilterOption.SCHEDULE,
                   onOptionSelected = { schedule ->
                       generalViewModel.filterFolder(
                           FilterOption.SCHEDULE,
                           schedule,
                           filteredCopilet = {filteredlist ->
                               if (filteredlist.isEmpty())
                                   copilotListAdapter.updateList(filteredlist)
                           },
                           visibilityFilterTitle = { visible->
                               binding.copiletFilteredTv.isVisible = visible
                           }
                       )
                   })
                dialog.show(supportFragmentManager, "FilterOptionsRightSheet")*/
            })

            filterFolderLayout.setOnClickListener(View.OnClickListener {
                openFilterDialog(FilterOption.FOLDER)

            })


        }

    }

    private fun setupObservers() {
        //observer goes here
        generalViewModel.getAuthenticationLive().observe(this) {
            it?.let {
            }
        }

        generalViewModel.copilotList.observe(this) { routines ->


            routines?.let {
                if (it.isEmpty()) {
                    binding.noCopiletsLayout.visibility = View.VISIBLE
                    binding.copiletListRV.visibility = View.GONE
                    fadeinEmptyLayout(1)

                } else {
                    binding.noCopiletsLayout.visibility = View.GONE
                    binding.copiletListRV.visibility = View.VISIBLE
                    copilotListAdapter.updateList(routines)
                    binding.copiletListRV.invalidate()
                    binding.copiletListRV.requestLayout()

                }
            }
        }

    }


    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, HomeActivity::class.java)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }


    private fun errorDialog(reason: String) {
        val internetDialogBinding = InternetdialogBinding.inflate(layoutInflater)
        intenetDialog = Dialog(this@HomeActivity)
        //intenetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        intenetDialog.window?.setGravity(Gravity.CENTER)
        intenetDialog.setContentView(internetDialogBinding.root)
        intenetDialog.getWindow()?.setBackgroundDrawable(getDrawable(R.drawable.rounded_dialog_bg));


        val width = (resources.displayMetrics.widthPixels * 0.6).toInt()
        intenetDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        //  val window: Window = intenetDialog?.getWindow()
        //   window.setLayout((getWidth() / 2), WindowManager.LayoutParams.WRAP_CONTENT)
        intenetDialog.setCancelable(true)

        internetDialogBinding.errorHeading.text = reason
        internetDialogBinding.okayBtn.setOnClickListener(
            View.OnClickListener {
                intenetDialog.dismiss()
            }
        )

        intenetDialog.show()
    }



    private fun openFilterDialog(filterOption: FilterOption) {
        val dialog = FilterOptionsRightSheet(
            filterOption,
            preferenceUtil.getString(filterOption.name) ?: "",
            onOptionSelected = { optionSelected ->
                preferenceUtil.saveString(filterOption.name, optionSelected)
                generalViewModel.filterFolder(
                    filterOption,
                    optionSelected,
                    filteredCopilet = { filteredlist ->
                        if (!filteredlist.isEmpty()) {
                            copilotListAdapter.updateList(filteredlist)
                            binding.copiletListRV.visibility = View.VISIBLE
                            binding.noCopiletsLayout.visibility = View.GONE
                        } else {
                            binding.copiletListRV.visibility = View.GONE
                            binding.noCopiletsLayout.visibility = View.VISIBLE
                            fadeinEmptyLayout(1)
                        }
                    },
                    visibilityFilterTitle = { visible ->
                        binding.copiletFilteredTv.isVisible = visible
                    }
                )
            }
        )
        dialog.show(supportFragmentManager, "FilterOptionsRightSheet")

    }

    private fun fadeinEmptyLayout(time: Int) {
        binding.emptycircle.animate().alpha(1f).setDuration(500).start()
        binding.emptyTitle.animate().alpha(1f).setDuration(500).start()
        if (time < 4) android.os.Handler(Looper.getMainLooper()).postDelayed({ fadeOutEmptyLayout(time + 1) }, 500)

    }

    private fun fadeOutEmptyLayout(time: Int) {

        binding.emptycircle.animate().alpha(0f).setDuration(500).start()
        binding.emptyTitle.animate().alpha(0f).setDuration(500).start()
        android.os.Handler(Looper.getMainLooper()).postDelayed({ fadeinEmptyLayout(time ) }, 500)
    }
}