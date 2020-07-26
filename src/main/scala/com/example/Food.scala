package com.example

import scala.util.Random


sealed trait Food

object Food {
  case object Pasta extends Food
  case object Pizza extends Food
  case object Hamburger extends Food

  val foodList = Seq(Pasta, Pizza, Hamburger)

  def getFood():Food = {
    foodList(Random.nextInt(foodList.size))
  }

  def anyOtherFood(food: Food): Food = {
    val otherFoods = foodList.filter(_ != food)
    otherFoods(Random.nextInt(otherFoods.size))
  }

}



