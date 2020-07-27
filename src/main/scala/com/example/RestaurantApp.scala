package com.example

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import com.example.Food._
import kamon.Kamon

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.io.StdIn

object RestaurantApp {

  class RestaurantApp(system: ActorSystem) {
    private val log = Logging(system, getClass.getName)

    private val restaurant = createRestaurant()

    protected def createRestaurant():ActorRef = system.actorOf(Restaurant.props(15),"restaurant")

    def run():Unit = {
      createCustomer(5, Hamburger)
      Await.ready(system.whenTerminated, Duration.Inf)
      system.terminate()
    }

    def createCustomer(count:Int, food: Food):Unit = {
      (1 to count).foreach{
        _ => restaurant ! Restaurant.CreateCustomer(food: Food)
      }
    }

  }

  def main(args: Array[String]): Unit = {
    Kamon.init()
    val system = ActorSystem("system")
    val application = new RestaurantApp(system)
    application.run()

  }

}
