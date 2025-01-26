package ru.webrelab.kie.cerealstorage

class CerealStorageImpl(
  override val containerCapacity: Float,
  override val storageCapacity: Float
) : CerealStorage {

  init {
    require(containerCapacity >= 0) {
      "Container capacity can't be negative"
    }
    require(storageCapacity >= containerCapacity) {
      "Storage capacity shouldn't be less than the capacity of one container"
    }
  }

  private val storage: MutableMap<Cereal, Float> = mutableMapOf()
  private val zeroCapacity = 0f

  override fun addCereal(cereal: Cereal, amount: Float): Float {
    verifyCerealAmountIsNotNegative(amount)
    prepareCerealContainerIfNotExist(cereal)
    val containerFreeSpace: Float = getSpace(cereal)
    when (amount <= containerFreeSpace) {
      true -> {
        storage += cereal to getAmount(cereal).plus(amount)
        return zeroCapacity
      }
      false -> {
        storage += cereal to containerCapacity
        return amount.minus(containerFreeSpace)
      }
    }
  }

  override fun getCereal(cereal: Cereal, amount: Float): Float {
    verifyCerealAmountIsNotNegative(amount)
    val actualCerealAmount: Float = getAmount(cereal)
    return when {
      amount < actualCerealAmount -> amount.also { storage[cereal] = actualCerealAmount.minus(it) }
      else -> actualCerealAmount.also { storage[cereal] = zeroCapacity }
    }
  }

  override fun removeContainer(cereal: Cereal): Boolean {
    return (getSpace(cereal) == containerCapacity).also { isContainerEmpty: Boolean ->
      if (isContainerEmpty) storage.remove(cereal)
    }
  }

  override fun getAmount(cereal: Cereal): Float {
    prepareCerealContainerIfNotExist(cereal)
    return storage.getValue(cereal)
  }

  override fun getSpace(cereal: Cereal): Float {
    prepareCerealContainerIfNotExist(cereal)
    return containerCapacity.minus(storage.getValue(cereal))
  }

  override fun toString(): String {
    return """
      ${this.javaClass.simpleName}(
        containerCapacity = $containerCapacity (type = ${containerCapacity.javaClass.simpleName}),
        storageCapacity = $storageCapacity (type = ${storageCapacity.javaClass.simpleName})
      )
    """.trimIndent()
  }

  private fun verifyCerealAmountIsNotNegative(amount: Float) {
    require(amount >= 0) { "Amount shouldn't be negative. Current value is [$amount]" }
  }

  private fun prepareCerealContainerIfNotExist(cereal: Cereal) {
    if (!storage.containsKey(cereal)) {
      verifyStorageCapacity()
      storage[cereal] = zeroCapacity
    }
  }

  private fun verifyStorageCapacity() {
    check(storage.size.plus(1) * containerCapacity <= storageCapacity) {
      "Storage is full. New container can't be added"
    }
  }
}