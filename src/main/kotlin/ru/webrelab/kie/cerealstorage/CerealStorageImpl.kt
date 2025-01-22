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
    return when (storage.containsKey(cereal)) {
      true -> addCerealToExistingContainer(cereal, amount)
      false -> addCerealToNotExistingContainer(cereal, amount)
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
    return storage.getOrDefault(cereal, zeroCapacity)
  }

  override fun getSpace(cereal: Cereal): Float {
    return storage[cereal]?.let { cerealAmount -> containerCapacity.minus(cerealAmount) }
      ?: containerCapacity
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

  private fun addCerealToExistingContainer(cereal: Cereal, amount: Float): Float {
    val containerFreeSpace: Float = getSpace(cereal)
    when (amount <= containerFreeSpace) {
      true -> storage += cereal to getAmount(cereal).plus(amount)
      false -> error("Container with [$cereal] cereal already exists. New container can't be added")
    }
    return zeroCapacity
  }

  private fun addCerealToNotExistingContainer(cereal: Cereal, amount: Float): Float {
    check(storage.size.plus(1) * containerCapacity < storageCapacity) {
      "Storage is full. New container can't be added"
    }
    when (amount <= containerCapacity) {
      true -> storage[cereal] = amount
      false -> return amount.minus(containerCapacity)
    }
    return zeroCapacity
  }
}