package com.android.goally.ui.home

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.goally.BaseActivity
import com.android.goally.R
import com.android.goally.data.model.api.response.copilet.Routines
import com.android.goally.databinding.ActivityCopiletDetailsBinding
import com.android.goally.databinding.ActivityHomeBinding
import com.android.goally.ui.adapters.CopiletDetailsAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class CopiletDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityCopiletDetailsBinding
    private lateinit var copilotDetailsAdapter: CopiletDetailsAdapter

    private lateinit var copiletId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopiletDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        copiletId = intent.getStringExtra("copiletId")?: ""
        getCopiletDetails()
        setupViews()


    }


    fun setupViews(){
        binding.run {
            detailsTopBar.copiletTitleTv.setText(getString(R.string.copiletDetailsTitle))
            detailsTopBar.backBtn.setOnClickListener(View.OnClickListener {
                this@CopiletDetailsActivity.onBackPressedDispatcher.onBackPressed()
            })


            copiletActivitiesRV.layoutManager = LinearLayoutManager(this@CopiletDetailsActivity) // Set LayoutManager
            copilotDetailsAdapter = CopiletDetailsAdapter(this@CopiletDetailsActivity)
            copiletActivitiesRV.adapter = copilotDetailsAdapter

            detailsTopBar.timeTv.setText(DateFormat.getTimeFormat(this@CopiletDetailsActivity).format(Date()))


        }

    }

    fun getCopiletDetails(){

        generalViewModel.getCopiletDetails(
            copiletId, success = { copilet->
                displayCopiletDetails(copilet)
            }
        )
    }

    fun displayCopiletDetails(copilet: Routines){
        copilotDetailsAdapter.updateList(copilet.activities)

    }

}