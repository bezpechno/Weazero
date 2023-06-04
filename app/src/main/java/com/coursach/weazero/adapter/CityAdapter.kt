package com.coursach.weazero.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coursach.Weazero.R

class CityAdapter(
    private val onCityClick: (String) -> Unit,
    private val onCityLongClick: (String) -> Boolean,
    private val onAddCityClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val cities = mutableListOf<String>()
    private var selectedCity: String? = null
    private var locationCity: String? = null
    companion object {
        private const val VIEW_TYPE_LOCATION = 0
        private const val VIEW_TYPE_CITY = 1
        private const val VIEW_TYPE_ADD_BUTTON = 2
    }

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.location_city_name)

        init {
            itemView.setOnClickListener {
                locationCity?.let {
                    setLocationCity(it)
                    onCityClick(it)
                }
            }
        }
    }

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityName: TextView = itemView.findViewById(R.id.city_name)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?: return@setOnClickListener

                if (position - 1 >= 0 && position - 1 < cities.size) {
                    val city = cities[position - 1]
                    onCityClick(city)
                    selectedCity = city
                    notifyDataSetChanged()
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                    ?: return@setOnLongClickListener false

                if (position == 0) return@setOnLongClickListener false

                if (position - 1 >= 0 && position - 1 < cities.size) {
                    val city = cities[position - 1]
                    onCityLongClick(city)
                } else {
                    false
                }
            }


        }
    }


    inner class AddCityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onAddCityClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOCATION -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.location_city_item, parent, false)
                LocationViewHolder(view)
            }
            VIEW_TYPE_CITY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.city_item, parent, false)
                CityViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.add_city_item, parent, false)
                AddCityViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LocationViewHolder -> {
                holder.cityName.text = if (locationCity != null) "Now in $locationCity" else "Loading..."
                holder.cityName.gravity = Gravity.CENTER
                holder.cityName.setTextColor(Color.BLACK)
                holder.cityName.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                holder.itemView.setBackgroundColor(Color.TRANSPARENT) // добавляем прозрачный фон
            }
            is CityViewHolder -> {
                if (position - 1 >= 0 && position - 1 < cities.size) {
                    val city = cities[position - 1] // корректируем позицию
                    holder.cityName.text = city
                    holder.cityName.gravity = Gravity.CENTER
                    holder.cityName.setTextColor(Color.BLACK)
                    holder.cityName.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                    holder.itemView.setBackgroundColor(if (city == selectedCity) Color.LTGRAY else Color.TRANSPARENT)
                }
            }

            is AddCityViewHolder -> {
                // Здесь можно обновить вид кнопки "+", если это необходимо
            }
            else -> throw IllegalArgumentException("Unknown view holder type")
        }
    }


    override fun getItemCount(): Int {
        return cities.size + 2 // добавляем 1 для элемента геолокации и ещё одну для кнопки "+"
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> VIEW_TYPE_LOCATION
            position < cities.size + 1 -> VIEW_TYPE_CITY // добавляем 1 для элемента геолокации
            else -> VIEW_TYPE_ADD_BUTTON
        }
    }
    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return when (getItemViewType(position)) {
            VIEW_TYPE_LOCATION -> 0L
            VIEW_TYPE_CITY -> (position - 1).toLong() // корректируем позицию
            VIEW_TYPE_ADD_BUTTON -> Long.MAX_VALUE
            else -> super.getItemId(position)
        }
    }
    fun setLocationCity(city: String) {
        if (locationCity != city) {
            locationCity = city
            selectedCity = city
            notifyItemChanged(0)
        }
    }

    fun updateCity(oldCity: String, newCity: String): Boolean {
        val position = cities.indexOf(oldCity)
        if (position != -1) {
            cities[position] = newCity
            notifyItemChanged(position + 1) // Обратите внимание, что мы добавляем 1 к позиции, учитывая элемент геолокации
            return true
        }
        return false
    }


    fun addCity(city: String) {
        if (!cities.contains(city)) {
            cities.add(city)
            notifyItemInserted(cities.size - 1)
        }
    }


    fun removeCity(city: String) {
        val position = cities.indexOf(city)
        if (position != -1) {
            cities.removeAt(position)
            notifyItemRemoved(position + 1) // Обратите внимание, мы добавляем 1 к позиции, учитывая элемент геолокации
        }
    }


    fun getCities(): List<String> {
        return cities.toList()
    }

}
