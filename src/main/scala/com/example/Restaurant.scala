package com.example

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.routing.FromConfig
import scala.concurrent.duration._

object Restaurant {
  case class CreateCustomer(food: Food)
  case class ApproveMenu(customer: ActorRef)

  def props(availableSeats: Int):Props = Props(new Restaurant(availableSeats))

}


class Restaurant(availableSeats: Int) extends Actor with ActorLogging{
  import Restaurant._

  override def supervisorStrategy: SupervisorStrategy = {
    val decider: SupervisorStrategy.Decider = {
      case Waiter.WrongFoodException(customer, food) =>
        chef.forward(Chef.PrepareFood(food, customer))
        SupervisorStrategy.Restart
    }
    OneForOneStrategy()(decider.orElse(super.supervisorStrategy.decider))
  }

  log.info("Restaurant is open for business")

  private val eatDuration: FiniteDuration =
    context.system.settings.config.getDuration("restaurant.customer.eat-duration", TimeUnit.MILLISECONDS).millis
  private val cookDuration: FiniteDuration =
    context.system.settings.config.getDuration("restaurant.chef.cook-duration", TimeUnit.MILLISECONDS).millis

  private var restaurantGuestBook: Map[ActorRef, Int] = Map.empty.withDefaultValue(0)

  private val chef:ActorRef = createChef()
  private val waiter:ActorRef = createWaiter()

  protected def createWaiter():ActorRef = context.actorOf(Waiter.props(chef))

  protected def createChef():ActorRef
  //= context.actorOf(Chef.props(cookDuration))
  = context.actorOf(FromConfig.props(Chef.props(cookDuration)),"chef")

  protected def createCustomer(food: Food):ActorRef = context.actorOf(Customer.props(self, waiter, food, eatDuration))


  override def receive = {
    case CreateCustomer(food) if restaurantGuestBook.size <= availableSeats =>
      val customer = createCustomer(food)
      restaurantGuestBook += customer -> 0
      context.watch(customer)
    case Terminated(customer) => log.info(s"Thank you for coming, customer $customer")
      restaurantGuestBook-=customer
    case _ => log.info("No more tables, We're full")
  }


}