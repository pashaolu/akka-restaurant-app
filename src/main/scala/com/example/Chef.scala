package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}

import scala.concurrent.duration.FiniteDuration
import scala.util.Random


object Chef{
  case class PrepareFood(food: Food, customer: ActorRef)
  case class FoodIsReady(food: Food, customer: ActorRef)


  def props(workDuration:FiniteDuration):Props = Props(new Chef(workDuration))
}

class Chef(workDuration:FiniteDuration) extends Actor with ActorLogging with Timers {
  import Chef._

  override def receive: Receive = {
    case PrepareFood(orderedFood, customer) =>
      Thread.sleep(workDuration.toMillis)
      val preparedFood = prepareFood(orderedFood)
      log.info(s"preparing $preparedFood for customer $customer")
      sender() ! FoodIsReady(preparedFood, customer)
  }

  def prepareFood(food: Food) = {
    //simulate failure in food preparation
    if (Random.nextInt(100) < 10)
      food
    else
      Food.anyOtherFood(food)
      food
  }


}
