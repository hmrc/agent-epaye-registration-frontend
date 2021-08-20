/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms.mappings

import models.Enumerable
import play.api.data.FormError
import play.api.data.format.Formatter
import uk.gov.hmrc.emailaddress.EmailAddress

import scala.util.control.Exception.nonFatalCatch

trait Formatters extends Constraints with Transforms {

  private[mappings] def emailFormatter(requiredKey: String,
                                       invalidKey: String,
                                       maxLengthKey: String) =
    new Formatter[String] {

      private val emailLength = 129 // API#1337 states email has a max length of 129

      private val dataFormatter: Formatter[String] = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
        dataFormatter
          .bind(key, data)
          .right.flatMap {
          case email if email.trim.length > emailLength =>
            Left(Seq(FormError(key, maxLengthKey)))
          case email if EmailAddress.isValid(email) =>
            Right(email)
          case _ =>
            Left(Seq(FormError(key, invalidKey)))
        }
      }

      override def unbind(key: String, value: String): Map[String, String] =
        dataFormatter.unbind(key, value.toString)
    }

  private[mappings] def telephoneFormatter(requiredKey: String, invalidKey: String, maxLengthKey: String) =
    new Formatter[String] {

      private val telephoneLength = 35

      private val telephoneNumberRegex = "^[0-9 ]*$"

      private val dataFormatter: Formatter[String] = stringFormatter(requiredKey)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
        dataFormatter
          .bind(key, data)
          .right.flatMap {
          case telephone if telephone.trim.length > telephoneLength =>
            Left(Seq(FormError(key, maxLengthKey)))
          case telephone if telephone.matches(telephoneNumberRegex) =>
            Right(telephone)
          case _ =>
            Left(Seq(FormError(key, invalidKey)))
        }
      }

      override def unbind(key: String, value: String): Map[String, String] =
        dataFormatter.unbind(key, value.toString)
    }

  protected def addressPostcodeFormatter(requiredKey: String = "error.postCode.uk.empty", invalidKey: String ): Formatter[String] =
    new Formatter[String] {

    private val regexPostcode = """^[A-Za-z]{1,2}[0-9Rr][0-9A-Za-z]?\s?[0-9][ABD-HJLNP-UW-Zabd-hjlnp-uw-z]{2}$"""

    private val dataFormatter: Formatter[String] = stringFormatter(requiredKey)

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      dataFormatter
        .bind(key, data)
        .right.flatMap {
        case pc
          if pc.trim == "" =>
          Left(Seq(FormError(key, requiredKey)))
        case pc
          if !stripSpaces(pc).matches(regexPostcode) =>
          Left(Seq(FormError(key, invalidKey)))
        case pc
          if stripSpaces(pc).matches(regexPostcode) =>
          Right(validPostCodeFormat(stripSpaces(pc)))
        case pc
          if !stripSpaces(pc).isEmpty =>
          Right(pc)
      }
    }

    override def unbind(key: String, value: String): Map[String, String] = {
      Map(key -> value)
    }
  }

  private[mappings] def stringFormatter(errorKey: String, args: Seq[String] = Seq.empty): Formatter[String] = new Formatter[String] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] =
      data.get(key) match {
        case None => Left(Seq(FormError(key, errorKey, args)))
        case Some(s) if s.trim.isEmpty => Left(Seq(FormError(key, errorKey, args)))
        case Some(s) => Right(s)
      }

    override def unbind(key: String, value: String): Map[String, String] =
      Map(key -> value)
  }

  private[mappings] def booleanFormatter(requiredKey: String, invalidKey: String, args: Seq[String] = Seq.empty): Formatter[Boolean] =
    new Formatter[Boolean] {

      private val baseFormatter = stringFormatter(requiredKey, args)

      override def bind(key: String, data: Map[String, String]) =
        baseFormatter
          .bind(key, data)
          .right.flatMap {
          case "true" => Right(true)
          case "false" => Right(false)
          case _ => Left(Seq(FormError(key, invalidKey, args)))
        }

      def unbind(key: String, value: Boolean) = Map(key -> value.toString)
    }

  private[mappings] def intFormatter(requiredKey: String, wholeNumberKey: String, nonNumericKey: String, args: Seq[String] = Seq.empty): Formatter[Int] =
    new Formatter[Int] {

      val decimalRegexp = """^-?(\d*\.\d*)$"""

      private val baseFormatter = stringFormatter(requiredKey, args)

      override def bind(key: String, data: Map[String, String]) =
        baseFormatter
          .bind(key, data)
          .right.map(_.replace(",", ""))
          .right.flatMap {
          case s if s.matches(decimalRegexp) =>
            Left(Seq(FormError(key, wholeNumberKey, args)))
          case s =>
            nonFatalCatch
              .either(s.toInt)
              .left.map(_ => Seq(FormError(key, nonNumericKey, args)))
        }

      override def unbind(key: String, value: Int) =
        baseFormatter.unbind(key, value.toString)
    }


  private[mappings] def enumerableFormatter[A](requiredKey: String, invalidKey: String, args: Seq[String] = Seq.empty)(implicit ev: Enumerable[A]): Formatter[A] =
    new Formatter[A] {

      private val baseFormatter = stringFormatter(requiredKey, args)

      override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] =
        baseFormatter.bind(key, data).right.flatMap {
          str =>
            ev.withName(str)
              .map(Right.apply)
              .getOrElse(Left(Seq(FormError(key, invalidKey, args))))
        }

      override def unbind(key: String, value: A): Map[String, String] =
        baseFormatter.unbind(key, value.toString)
    }
}
