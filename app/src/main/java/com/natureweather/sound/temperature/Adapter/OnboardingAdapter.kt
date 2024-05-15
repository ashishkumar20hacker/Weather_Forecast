package com.natureweather.sound.temperature.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.natureweather.sound.temperature.Model.OnboardModel
import com.natureweather.sound.temperature.databinding.ViewOnboardScreenBinding

class OnboardingAdapter(private val onNextBtnClickListener: OnNextBtnClickListener) :
    ListAdapter<OnboardModel, OnboardingAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewOnboardScreenBinding =
            ViewOnboardScreenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: OnboardModel = getItem(position) ?: return

        if (position == 1) {
            holder.binding.gif.visibility = View.VISIBLE
        }
        holder.binding.dots.setImageResource(model.dotsImage)
        holder.binding.title.text = model.title
        holder.binding.desc.text = model.description
        holder.binding.mainLayout.setBackgroundResource(model.image)
        holder.binding.nextBtn.setOnClickListener {
            onNextBtnClickListener.onClick()
        }
    }

    class ViewHolder(val binding: ViewOnboardScreenBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnNextBtnClickListener {
        fun onClick()
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<OnboardModel>() {
            override fun areItemsTheSame(oldItem: OnboardModel, newItem: OnboardModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: OnboardModel, newItem: OnboardModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}