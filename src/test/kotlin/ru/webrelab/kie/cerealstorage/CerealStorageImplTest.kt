package ru.webrelab.kie.cerealstorage

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.webrelab.kie.cerealstorage.Cereal.Companion.getRandomCereal

class CerealStorageImplTest {
  private val storage = CerealStorageImpl(10f, 20f)
  private val randomCereal: Cereal by lazy { getRandomCereal() }

  @Test
  fun `Should throw if containerCapacity is negative`() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      CerealStorageImpl(-4f, 10f)
    }
  }

  @Test
  fun `Should throw if storageCapacity is less than containerCapacity`() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      CerealStorageImpl(20f, 10f)
    }
  }

  @Test
  fun `Verify cereal amount addition to container`() {
    val expectedCerealAmount = 4f
    storage.addCereal(randomCereal, expectedCerealAmount)
    val actualCerealAmount: Float = storage.getAmount(randomCereal)
    Assertions.assertEquals(
      expectedCerealAmount, actualCerealAmount,
      "Cereal amount should be equal to [$expectedCerealAmount]"
    )
  }

  @Test
  fun `Verify cereal amount remaining if the container is full`() {
    val cerealAmount = 14f
    val expectedRemainingCerealAmount: Float = cerealAmount.minus(storage.containerCapacity)
    val actualRemainingCerealAmount: Float = storage.addCereal(randomCereal, cerealAmount)
    Assertions.assertEquals(
      expectedRemainingCerealAmount, actualRemainingCerealAmount,
      "Remaining cereal amount should be equal [$expectedRemainingCerealAmount]"
    )
  }

  @Test
  fun `Should throw if amount is negative when add cereal`() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      storage.addCereal(randomCereal, -1f)
    }
  }

  @Test
  fun `Should throw when add another container to storage`() {
    val cerealAmount = 8f
    storage.addCereal(randomCereal, cerealAmount)
    Assertions.assertThrows(IllegalStateException::class.java) {
      storage.addCereal(randomCereal, cerealAmount)
    }
  }

  @Test
  fun `Verify receipt of cereal if it is more than in container`() {
    val containerCerealAmount = 3f
    val cerealAmountToReceive = 5f
    storage.addCereal(randomCereal, containerCerealAmount)
    val actualCerealAmount: Float = storage.getCereal(randomCereal, cerealAmountToReceive)
    val expectedContainerCerealAmount = 0f
    val actualContainerCerealAmount: Float = storage.getAmount(randomCereal)
    Assertions.assertAll(
      {
        Assertions.assertEquals(
          containerCerealAmount, actualCerealAmount,
          "Cereal amount should be equal [$containerCerealAmount]"
        )
      },
      {
        Assertions.assertEquals(
          expectedContainerCerealAmount, actualContainerCerealAmount,
          "Container should be empty"
        )
      }
    )
  }

  @Test
  fun `Verify receipt of cereal if it is less than in container`() {
    val containerCerealAmount = 8f
    val cerealAmountToReceive = 5f
    storage.addCereal(randomCereal, containerCerealAmount)
    val actualCerealAmount: Float = storage.getCereal(randomCereal, cerealAmountToReceive)
    val expectedContainerCerealAmount: Float = containerCerealAmount.minus(cerealAmountToReceive)
    val actualContainerCerealAmount: Float = storage.getAmount(randomCereal)
    Assertions.assertAll(
      {
        Assertions.assertEquals(
          cerealAmountToReceive, actualCerealAmount,
          "Cereal amount should be equal [$cerealAmountToReceive]"
        )
      },
      {
        Assertions.assertEquals(
          expectedContainerCerealAmount, actualContainerCerealAmount,
          "Container cereal amount should be equal [$expectedContainerCerealAmount]"
        )
      }
    )
  }

  @Test
  fun `Should throw if amount is negative when receipt of cereal`() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      storage.getCereal(randomCereal, -1f)
    }
  }

  @Test
  fun `Verify container removal if it is empty`() {
    val cerealAmount = 0f
    storage.addCereal(randomCereal, cerealAmount)
    Assertions.assertTrue(storage.removeContainer(randomCereal), "Container isn't empty")
  }

  @Test
  fun `Verify container removal if it isn't empty`() {
    val cerealAmount = 4f
    storage.addCereal(randomCereal, cerealAmount)
    Assertions.assertFalse(storage.removeContainer(randomCereal), "Container is empty")
  }

  @Test
  fun `Verify cereal amount if container is empty`() {
    val expectedCerealAmount = 0f
    val actualCerealAmount: Float = storage.getAmount(randomCereal)
    Assertions.assertEquals(
      expectedCerealAmount, actualCerealAmount,
      "Cereal amount should be equal to [$expectedCerealAmount]"
    )
  }

  @Test
  fun `Verify free space of container if it is empty`() {
    val expectedContainerFreeSpace: Float = storage.containerCapacity
    val actualContainerFreeSpace: Float = storage.getSpace(randomCereal)
    Assertions.assertEquals(
      expectedContainerFreeSpace, actualContainerFreeSpace,
      "Container free space should be equal to [$expectedContainerFreeSpace]"
    )
  }

  @Test
  fun `Verify free space of container if it isn't empty`() {
    val cerealAmount = 4f
    storage.addCereal(randomCereal, cerealAmount)
    val expectedContainerFreeSpace: Float = storage.run { containerCapacity.minus(getAmount(randomCereal)) }
    val actualContainerFreeSpace: Float = storage.getSpace(randomCereal)
    Assertions.assertEquals(
      expectedContainerFreeSpace, actualContainerFreeSpace,
      "Container free space should be equal to [$expectedContainerFreeSpace]"
    )
  }
}