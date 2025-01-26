package ru.webrelab.kie.cerealstorage

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.webrelab.kie.cerealstorage.Cereal.BUCKWHEAT
import ru.webrelab.kie.cerealstorage.Cereal.BULGUR
import ru.webrelab.kie.cerealstorage.Cereal.MILLET
import ru.webrelab.kie.cerealstorage.Cereal.PEAS
import ru.webrelab.kie.cerealstorage.Cereal.RICE

class CerealStorageImplTest {
  private val storage = CerealStorageImpl(10f, 20f)
  private val comparisonDelta = 0.01f

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
    storage.addCereal(BUCKWHEAT, expectedCerealAmount)
    val actualCerealAmount: Float = storage.getAmount(BUCKWHEAT)
    Assertions.assertEquals(
      expectedCerealAmount, actualCerealAmount, comparisonDelta,
      "Cereal amount should be equal to [$expectedCerealAmount]"
    )
  }

  @Test
  fun `Verify amount of different types of cereal added to the container`() {
    val expectedCerealAmount = 4f
    storage.addCereal(RICE, expectedCerealAmount)
    storage.addCereal(PEAS, expectedCerealAmount)
    val actualRiseCerealAmount: Float = storage.getAmount(RICE)
    val actualPeasCerealAmount: Float = storage.getAmount(PEAS)
    Assertions.assertAll(
      {
        Assertions.assertEquals(
          expectedCerealAmount, actualRiseCerealAmount, comparisonDelta,
          "Cereal amount should be equal to [$expectedCerealAmount]"
        )
      },
      {
        Assertions.assertEquals(
          expectedCerealAmount, actualPeasCerealAmount, comparisonDelta,
          "Cereal amount should be equal to [$expectedCerealAmount]"
        )
      }
    )
  }

  @Test
  fun `Verify cereal amount remaining if the container is full with one-time addition`() {
    val cerealAmount = 14f
    val expectedRemainingCerealAmount: Float = cerealAmount.minus(storage.containerCapacity)
    val actualRemainingCerealAmount: Float = storage.addCereal(RICE, cerealAmount)
    Assertions.assertEquals(
      expectedRemainingCerealAmount, actualRemainingCerealAmount, comparisonDelta,
      "Remaining cereal amount should be equal [$expectedRemainingCerealAmount]"
    )
  }

  @Test
  fun `Verify cereal amount remaining if the container is full with double addition`() {
    val firstAdditionCerealAmount = 8f
    val secondAdditionCerealAmount = 6f
    val expectedRemainingCerealAmount: Float =
      firstAdditionCerealAmount.plus(secondAdditionCerealAmount).minus(storage.containerCapacity)
    storage.addCereal(RICE, firstAdditionCerealAmount)
    val actualRemainingCerealAmount: Float = storage.addCereal(RICE, secondAdditionCerealAmount)
    Assertions.assertEquals(
      expectedRemainingCerealAmount, actualRemainingCerealAmount, comparisonDelta,
      "Remaining cereal amount should be equal [$expectedRemainingCerealAmount]"
    )
  }

  @Test
  fun `Should throw if amount is negative when add cereal`() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      storage.addCereal(MILLET, -1f)
    }
  }

  @Test
  fun `Should throw when add another container to storage`() {
    val cerealAmount = 8f
    storage.addCereal(MILLET, cerealAmount)
    storage.addCereal(BULGUR, cerealAmount)
    Assertions.assertThrows(IllegalStateException::class.java) {
      storage.addCereal(PEAS, cerealAmount)
    }
  }

  @Test
  fun `Verify receipt of cereal if it is more than in container`() {
    val containerCerealAmount = 3f
    val cerealAmountToReceive = 5f
    storage.addCereal(BULGUR, containerCerealAmount)
    val actualCerealAmount: Float = storage.getCereal(BULGUR, cerealAmountToReceive)
    val expectedContainerCerealAmount = 0f
    val actualContainerCerealAmount: Float = storage.getAmount(BULGUR)
    Assertions.assertAll(
      {
        Assertions.assertEquals(
          containerCerealAmount, actualCerealAmount, comparisonDelta,
          "Cereal amount should be equal [$containerCerealAmount]"
        )
      },
      {
        Assertions.assertEquals(
          expectedContainerCerealAmount, actualContainerCerealAmount, comparisonDelta,
          "Container should be empty"
        )
      }
    )
  }

  @Test
  fun `Verify receipt of cereal if it is less than in container`() {
    val containerCerealAmount = 8f
    val cerealAmountToReceive = 5f
    storage.addCereal(BUCKWHEAT, containerCerealAmount)
    val actualCerealAmount: Float = storage.getCereal(BUCKWHEAT, cerealAmountToReceive)
    val expectedContainerCerealAmount: Float = containerCerealAmount.minus(cerealAmountToReceive)
    val actualContainerCerealAmount: Float = storage.getAmount(BUCKWHEAT)
    Assertions.assertAll(
      {
        Assertions.assertEquals(
          cerealAmountToReceive, actualCerealAmount, comparisonDelta,
          "Cereal amount should be equal [$cerealAmountToReceive]"
        )
      },
      {
        Assertions.assertEquals(
          expectedContainerCerealAmount, actualContainerCerealAmount, comparisonDelta,
          "Container cereal amount should be equal [$expectedContainerCerealAmount]"
        )
      }
    )
  }

  @Test
  fun `Should throw if amount is negative when receipt of cereal`() {
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      storage.getCereal(RICE, -1f)
    }
  }

  @Test
  fun `Verify container removal if it is empty`() {
    val cerealAmount = 0f
    storage.addCereal(MILLET, cerealAmount)
    Assertions.assertTrue(storage.removeContainer(MILLET), "Container isn't empty")
  }

  @Test
  fun `Verify container removal if it isn't empty`() {
    val cerealAmount = 4f
    storage.addCereal(MILLET, cerealAmount)
    Assertions.assertFalse(storage.removeContainer(MILLET), "Container is empty")
  }

  @Test
  fun `Verify container removal if it isn't empty with receipt of cereal`() {
    val cerealAmount = 4f
    storage.apply {
      addCereal(MILLET, cerealAmount)
      getCereal(MILLET, cerealAmount)
    }
    Assertions.assertTrue(storage.removeContainer(MILLET), "Container isn't empty")
  }

  @Test
  fun `Verify cereal amount if container is empty`() {
    val expectedCerealAmount = 0f
    val actualCerealAmount: Float = storage.getAmount(MILLET)
    Assertions.assertEquals(
      expectedCerealAmount, actualCerealAmount, comparisonDelta,
      "Cereal amount should be equal to [$expectedCerealAmount]"
    )
  }

  @Test
  fun `Verify free space of container if it is empty`() {
    val expectedContainerFreeSpace: Float = storage.containerCapacity
    val actualContainerFreeSpace: Float = storage.getSpace(RICE)
    Assertions.assertEquals(
      expectedContainerFreeSpace, actualContainerFreeSpace, comparisonDelta,
      "Container free space should be equal to [$expectedContainerFreeSpace]"
    )
  }

  @Test
  fun `Verify free space of container if it isn't empty`() {
    val cerealAmount = 4f
    storage.addCereal(MILLET, cerealAmount)
    val expectedContainerFreeSpace: Float = storage.run { containerCapacity.minus(getAmount(MILLET)) }
    val actualContainerFreeSpace: Float = storage.getSpace(MILLET)
    Assertions.assertEquals(
      expectedContainerFreeSpace, actualContainerFreeSpace, comparisonDelta,
      "Container free space should be equal to [$expectedContainerFreeSpace]"
    )
  }
}