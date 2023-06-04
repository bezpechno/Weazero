import com.coursach.weazero.model.Main
import com.coursach.weazero.model.Weather

data class ForecastData(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastListItem>,
    val city: City
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Int,
    val sunset: Int
)

data class Coord(
    val lat: Float,
    val lon: Float
)

data class ForecastListItem(
    val dt: Int,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Float,
    val rain: Rain?,
    val sys: Sys,
    val dt_txt: String
)

data class Main(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Float
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Wind(
    val speed: Float,
    val deg: Int,
    val gust: Float
)

data class Rain(
    val `3h`: Float?
)

data class Sys(
    val pod: String
)
