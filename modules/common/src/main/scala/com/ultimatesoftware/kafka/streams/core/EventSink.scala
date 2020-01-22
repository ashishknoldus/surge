// Copyright © 2017-2019 Ultimate Software Group. <https://www.ultimatesoftware.com>

package com.ultimatesoftware.kafka.streams.core

import scala.concurrent.Future

trait EventSink[Event, EvtMeta] {
  def handleEvent(event: Event, eventProps: EvtMeta): Future[Any]
}
