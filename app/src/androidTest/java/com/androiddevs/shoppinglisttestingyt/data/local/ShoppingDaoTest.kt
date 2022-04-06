package com.androiddevs.shoppinglisttestingyt.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.androiddevs.shoppinglisttestingyt.util.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ShoppingDaoTest {

   @get:Rule
   var instantTaskExecutorRule = InstantTaskExecutorRule()

   private lateinit var database: ShoppingItemDatabase
   private lateinit var dao: ShoppingDao

   @Before
   fun setup(){
      database = Room.inMemoryDatabaseBuilder(
         ApplicationProvider.getApplicationContext(),
         ShoppingItemDatabase::class.java
      ).allowMainThreadQueries()
         .build()

      dao = database.shoppingDao()
   }


   @After
   fun teardown(){
      database.close()
   }


   @Test
   fun insertShoppingItem() = runBlockingTest {
      val shoppingItem = ShoppingItem(id = 1, name = "Danone", amount = 1, price = 1f, imageUrl = "")
      dao.insertShoppingItem(shoppingItem)

      val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

      assertThat(allShoppingItems).contains(shoppingItem)
   }

   @Test
   fun deleteShoppingItem() = runBlockingTest {
      val shoppingItem = ShoppingItem(id = 1, name = "Danone", amount = 1, price = 1f, imageUrl = "")
      dao.insertShoppingItem(shoppingItem)
      dao.deleteShoppingItem(shoppingItem)


      val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

      assertThat(allShoppingItems).doesNotContain(shoppingItem)
   }

   @Test
   fun observeTotalPriceSum() = runBlockingTest {
      val shoppingItem1 = ShoppingItem(id = 1, name = "Danone", amount = 1, price = 1f, imageUrl = "")
      val shoppingItem2 = ShoppingItem(id = 2, name = "Yakult", amount = 4, price = 0.5f, imageUrl = "")
      val shoppingItem3 = ShoppingItem(id = 3, name = "Toddynho", amount = 2, price = 1.5f, imageUrl = "")

      dao.insertShoppingItem(shoppingItem1)
      dao.insertShoppingItem(shoppingItem2)
      dao.insertShoppingItem(shoppingItem3)



      val totalPriceSum = dao.observeTotalPrice().getOrAwaitValue()

      assertThat(totalPriceSum).isEqualTo(6f)
   }
}