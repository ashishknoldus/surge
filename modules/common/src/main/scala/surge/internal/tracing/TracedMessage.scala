// Copyright © 2017-2021 UKG Inc. <https://www.ukg.com>

package surge.internal.tracing

import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.opentelemetry.api.trace.{ Span, Tracer }
import org.slf4j.MDC
import surge.akka.cluster.JacksonSerializable

object TracedMessage {

  def apply[T](message: T, parentSpan: Span)(implicit tracer: Tracer): TracedMessage[T] =
    TracedMessage(message, TracePropagation.asHeaders(parentSpan))

  def apply[T](message: T, headers: Map[String, String] = Map.empty): TracedMessage[T] = {
    TracedMessage(message, headers, Option(MDC.getCopyOfContextMap))
  }
}

final case class TracedMessage[T](
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "messageType", visible = true) message: T,
    headers: Map[String, String],
    mdcContextMap: Option[java.util.Map[String, String]])
    extends JacksonSerializable
