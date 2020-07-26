package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object Waiter {
  case object AssignTable
  case class FoodOrder(food: Food)
  case class ServeFood(food: Food)
  case class ReOrderFood(food: Food)
  case class WrongFoodException(customer: ActorRef, food: Food) extends IllegalStateException

  def props(chef: ActorRef):Props = Props(new Waiter(chef))

}

class Waiter(chef: ActorRef) extends Actor with ActorLogging{
  import Waiter._

  override def receive: Receive = {
    case FoodOrder(food) =>
      log.info(s"customer $sender() ordered $food")
      chef ! Chef.PrepareFood(food, sender())

    case Chef.FoodIsReady(food, customer) =>
      log.info(s"serving $food to customer $customer")
      customer ! ServeFood(food)

    case ReOrderFood(food) =>
      log.warning(s"re-order $food for customer $sender()")
      throw WrongFoodException(sender(), food)
  }


}
