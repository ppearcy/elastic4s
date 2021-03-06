package com.sksamuel.elastic4s.http

import java.util

import com.sksamuel.elastic4s.JsonFormat
import com.sksamuel.elastic4s.locks.{AcquireGlobalLockDefinition, ReleaseGlobalLockDefinition}
import org.apache.http.entity.StringEntity
import org.elasticsearch.client.{ResponseException, RestClient}

import scala.concurrent.Future

trait LocksImplicits {

  implicit object AcquireGlobalLockHttpExecutable extends HttpExecutable[AcquireGlobalLockDefinition, Boolean] {

    val endpoint = "/fs/lock/global/_create"
    val emptyParams = new util.HashMap[String, String]()
    val emptyEntity = new StringEntity("{}")

    override def execute(client: RestClient,
                         request: AcquireGlobalLockDefinition,
                         format: JsonFormat[Boolean]): Future[Boolean] = {
      try {
        val result = client.performRequest("PUT", endpoint, emptyParams, emptyEntity)
        Future.successful(result.getStatusLine.getStatusCode == 201)
      } catch {
        case _: ResponseException =>
          Future.successful(false)
      }
    }
  }

  implicit object ReleaseGlobalLockHttpExecutable extends HttpExecutable[ReleaseGlobalLockDefinition, Boolean] {
    override def execute(client: RestClient,
                         request: ReleaseGlobalLockDefinition,
                         format: JsonFormat[Boolean]): Future[Boolean] = {
      val result = client.performRequest("DELETE", "/fs/lock/global")
      Future.successful(result.getStatusLine.getStatusCode == 200)
    }
  }
}
