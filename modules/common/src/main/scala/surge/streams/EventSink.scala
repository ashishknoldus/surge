// Copyright © 2017-2020 UKG Inc. <https://www.ukg.com>

package surge.streams

import akka.NotUsed
import akka.stream.scaladsl.Flow
import surge.internal.akka.streams.FlowConverter
import surge.internal.streams.DefaultDataSinkExceptionHandler

import scala.concurrent.{ ExecutionContext, Future }

case class EventPlusStreamMeta[Key, Value, Meta](messageKey: Key, messageBody: Value, streamMeta: Meta, headers: Map[String, Array[Byte]])

abstract class EventSinkExceptionHandler[Evt] extends DataSinkExceptionHandler[String, Evt]

trait EventHandler[Event] {
  def eventHandler[Meta]: Flow[EventPlusStreamMeta[String, Event, Meta], Meta, NotUsed]
  def nullEventFactory(key: String, headers: Map[String, Array[Byte]]): Option[Event] = None
  def sinkExceptionHandler: DataSinkExceptionHandler[String, Event] = new DefaultDataSinkExceptionHandler[String, Event]
}

trait EventSink[Event] extends EventHandler[Event] {
  def parallelism: Int = 8
  def handleEvent(key: String, event: Event, headers: Map[String, Array[Byte]]): Future[Any]
  def partitionBy(key: String, event: Event, headers: Map[String, Array[Byte]]): String

  override def eventHandler[Meta]: Flow[EventPlusStreamMeta[String, Event, Meta], Meta, NotUsed] = {
    FlowConverter.flowFor(handleEvent, partitionBy, sinkExceptionHandler, parallelism)(ExecutionContext.global)
  }
}
