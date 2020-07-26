package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}

import scala.concurrent.duration.FiniteDuration

object Customer{

  case object FinnishedEating
  def props(restaurant: ActorRef, waiter:ActorRef, food: Food, finishEatingDuration: FiniteDuration): Props
  = Props(new Customer(restaurant, waiter, food, finishEatingDuration))
}

class Customer(restaurant: ActorRef, waiter: ActorRef, food: Food, finishEatingDuration:FiniteDuration) extends Actor with ActorLogging with Timers {
  import Customer._

  OrderFood()

  override def postStop(): Unit = {
    log.info("Goodbye!")
    super.postStop()
  }

  override def receive: Receive = {
    case Waiter.ServeFood(`food`) =>
      log.info(s"eating ordered food $food")
      timers.startSingleTimer("eat-timer", FinnishedEating,  finishEatingDuration)
    case Waiter.ServeFood(anotherFood) =>
      log.info(s"ordered $food but got $anotherFood, return to Waiter")
      waiter ! Waiter.ReOrderFood(food)
    case FinnishedEating =>
      log.info("I've finished eating. Now leaving")
      context.stop(self)
  }

  def OrderFood():Unit = {
    waiter ! Waiter.FoodOrder(food)
  }

}
