package com.natureweather.sound.temperature.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natureweather.sound.temperature.Adapter.TipsAdapter
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Model.TipsModel
import com.natureweather.sound.temperature.databinding.ActivityTipsBinding


class TipsActivity : AppCompatActivity() {
    
    lateinit var binding: ActivityTipsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.makeStatusBarTransparent2(this)
        binding = ActivityTipsBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        binding.tipsRv.setLayoutManager(LinearLayoutManager(this, RecyclerView.VERTICAL, false))
        fillList()
        binding.backbt.setOnClickListener(View.OnClickListener { onBackPressedDispatcher.onBackPressed() })
    }

    private fun fillList() {
        val tipsModelArrayList: ArrayList<TipsModel>
        tipsModelArrayList = ArrayList<TipsModel>()
        tipsModelArrayList.add(
            TipsModel(
                "Tip ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "Tip of ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "Tip of the ",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "Tip of the day",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "day",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "the day",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "of the day",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        tipsModelArrayList.add(
            TipsModel(
                "Tip of the day",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut et massa mi. Aliquam in hendrerit urna. Pellentesque sit amet sapien fringilla."
            )
        )
        val tipsAdapter = TipsAdapter(this, tipsModelArrayList, "tips", object : TipsAdapter.OnTipClickListener{
            override fun onTipclick() {
            }

        })
        binding.tipsRv.setAdapter(tipsAdapter)
    }
}