package com.natureweather.sound.temperature.Adapter

import android.app.Activity
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.natureweather.sound.temperature.Model.SoundsModel
import com.natureweather.sound.temperature.R
import pl.droidsonroids.gif.GifImageView


class SoundsAdapter(
    var context: Activity,
    soundsModelArrayList: ArrayList<SoundsModel>,
    onClickListener: OnClickListener
) :
    RecyclerView.Adapter<SoundsAdapter.ViewHolder>() {
    var list: ArrayList<SoundsModel>
    var pos = -1
    var playPause = 0
    var mediaPlayer: MediaPlayer? = null
    var onClickListener: OnClickListener
    var audio = 0

    init {
        list = soundsModelArrayList
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_sounds, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.setText(list[position].soundName)
        Glide.with(context).load(list[position].soundGif).into(holder.gifImageView)
        holder.play.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                pos = position
                playPause = 1
                audio = list[pos].sound
                onClickListener.onplay(audio)
                notifyDataSetChanged()
            }
        })
        holder.pause.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                pos = position
                playPause = 0
                onClickListener.onpause()
                notifyDataSetChanged()
            }
        })
        if (pos == position && playPause == 1) {
            holder.play.setVisibility(View.GONE)
            holder.pause.setVisibility(View.VISIBLE)
        } else if (pos == position && playPause == 0) {
            holder.play.setVisibility(View.VISIBLE)
            holder.pause.setVisibility(View.GONE)
        } else {
            holder.play.setVisibility(View.VISIBLE)
            holder.pause.setVisibility(View.GONE)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickListener {
        fun onplay(audio: Int)
        fun onpause()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var gifImageView: GifImageView
        var pause: GifImageView
        var play: ImageView
        var name: TextView

        init {
            gifImageView = itemView.findViewById<GifImageView>(R.id.sound_gif)
            pause = itemView.findViewById<GifImageView>(R.id.pause)
            play = itemView.findViewById<ImageView>(R.id.play)
            name = itemView.findViewById<TextView>(R.id.sound_name)
        }
    }
}

