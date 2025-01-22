package ru.webrelab.kie.cerealstorage

@Suppress("unused")
enum class Cereal(val local: String) {
  BUCKWHEAT("Гречка"),
  RICE("Рис"),
  MILLET("Пшено"),
  PEAS("Горох"),
  BULGUR("Булгур");

  companion object {
    fun getRandomCereal(): Cereal = entries.random()
  }
}