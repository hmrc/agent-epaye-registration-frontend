package uk.gov.hmrc.agentepayeregistrationfrontend.controllers

import org.scalatest.EitherValues
import play.api.data.FormError
import uk.gov.hmrc.play.test.UnitSpec

class FieldMappingsSpec extends UnitSpec with EitherValues {

  "postcode bind" should {
    val postcodeMapping = FieldMappings.postcode.withPrefix("testKey")

    def bind(fieldValue: String) = postcodeMapping.bind(Map("testKey" -> fieldValue))

    def shouldAcceptFieldValue(fieldValue: String): Unit = {
      bind(fieldValue) shouldBe Right(fieldValue)
    }

    def shouldRejectFieldValueAsInvalid(fieldValue: String): Unit = {
      bind(fieldValue) should matchPattern { case Left(List(FormError("testKey", List("error.postcode.invalid"), _))) => }
    }

    "accept valid postcodes" in {
      shouldAcceptFieldValue("AA1 1AA")
      shouldAcceptFieldValue("AA1M 1AA")
      shouldAcceptFieldValue("A11 1AA")
      shouldAcceptFieldValue("A1A 1AA")
    }

    "give \"error.required\" error when it is not supplied" in {
      postcodeMapping.bind(Map.empty).left.value should contain only FormError("testKey", "error.required")
    }

    "give \"error.required\" error when it is empty" in {
      bind("").left.value should contain only FormError("testKey", "error.required")
    }

    "give \"error.required\" error when it only contains a space" in {
      bind(" ").left.value should contain only FormError("testKey", "error.required")
    }

    "reject postcodes containing invalid characters" in {
      shouldRejectFieldValueAsInvalid("_A1 1AA")
      shouldRejectFieldValueAsInvalid("A.1 1AA")
      shouldRejectFieldValueAsInvalid("AA/ 1AA")
      shouldRejectFieldValueAsInvalid("AA1#1AA")
      shouldRejectFieldValueAsInvalid("AA1 ~AA")
      shouldRejectFieldValueAsInvalid("AA1 1$A")
      shouldRejectFieldValueAsInvalid("AA1 1A%")
    }

    "accept postcodes with 2 characters in the outbound part" in {
      shouldAcceptFieldValue("A1 1AA")
    }

    "accept postcodes with 4 characters in the outbound part" in {
      shouldAcceptFieldValue("AA1A 1AA")
      shouldAcceptFieldValue("AA11 1AA")
    }

    "reject postcodes where the 1st character of the outbound part is a number" in {
      shouldRejectFieldValueAsInvalid("1A1 1AA")
    }

    "reject postcodes where the length of the inbound part is not 3" in {
      shouldRejectFieldValueAsInvalid("AA1 1A")
      shouldRejectFieldValueAsInvalid("AA1 1AAA")
    }

    "reject postcodes where the 1st character of the inbound part is a letter" in {
      shouldRejectFieldValueAsInvalid("AA1 AAA")
    }

    "reject valid start of postcode but invalid after" in {
      shouldRejectFieldValueAsInvalid("AA1 AAA PPRRD")
    }

    "accept postcodes without spaces" in {
      shouldAcceptFieldValue("AA11AA")
    }

    "accept postcodes with extra spaces" in {
      shouldAcceptFieldValue(" A A 1 1 A A ")
    }
  }

  "telephoneNumber and faxNumber bind" should {
    val telephoneMapping = FieldMappings.telephone.withPrefix("testKey")

    def bind(fieldValue: String) = telephoneMapping.bind(Map("testKey" -> fieldValue))

    def shouldRejectFieldValueAsInvalid(fieldValue: String): Unit = {
      bind(fieldValue) should matchPattern { case Left(List(FormError("testKey", List("error.telephone.invalid"), _))) => }
    }

    def shouldRejectFieldValueAsTooLong(fieldValue: String): Unit = {
      bind(fieldValue) should matchPattern { case Left(List(FormError("testKey", List("error.maxLength"), _))) => }
    }

    def shouldAcceptFieldValue(fieldValue: String): Unit = {
      bind(fieldValue) shouldBe Right(Some(fieldValue))
    }

    "reject telephone numbers" when {

      "more than 35 characters" in {
        shouldRejectFieldValueAsTooLong("9999999999999999999999999999999999999999999999999")
      }

      "valid telephone number then invalid characters" in {
        shouldRejectFieldValueAsInvalid("0207 567 8554dbvv")
      }

      "there is text in the field" in {
        shouldRejectFieldValueAsInvalid("0123 456 7890 EXT 123")
      }
    }

    "accept telephone numbers" when {

      "there are 3 digits" in {
        shouldAcceptFieldValue("123")
      }

      "there are valid symbols in the input" in {
        shouldAcceptFieldValue("+441234567890")
        shouldAcceptFieldValue("#441234567890")
        shouldAcceptFieldValue("(44)1234567890")
        shouldAcceptFieldValue("++441234567890")
      }

      "there is whitespace in the field" in {
        shouldAcceptFieldValue("0123 456 7890")
      }
    }
  }

  "addressLine 1 and 2 bind" should {
    val addressLine1Mapping = FieldMappings.addressLine12.withPrefix("testKey")

    def bind(fieldValue: String) = addressLine1Mapping.bind(Map("testKey" -> fieldValue))

    def shouldRejectFieldValueAsTooLong(fieldValue: String): Unit = {
      bind(fieldValue) should matchPattern { case Left(List(FormError("testKey", List("error.maxLength"), _))) => }
    }

    def shouldAcceptFieldValue(fieldValue: String): Unit = {
      if (fieldValue.isEmpty) bind(fieldValue) shouldBe Right(None)
      else bind(fieldValue) shouldBe Right(fieldValue)
    }

    "reject address Line 1" when {
      "field is not present" in {
        addressLine1Mapping.bind(Map.empty).left.value should contain only FormError("testKey", "error.required")
      }

      "input is empty" in {
        bind("").left.value should contain(FormError("testKey", "error.required"))
      }

      "input is only whitespace" in {
        bind("    ").left.value should contain(FormError("testKey", "error.required"))
      }

      "more than 35 characters" in {
        shouldRejectFieldValueAsTooLong("1234567891123456789212345678931234567")
      }
    }

    "accept address Line 1" when {
      "there is text and numbers" in {
        shouldAcceptFieldValue("99 My Agency address")
      }

      "there are valid symbols in the input" in {
        shouldAcceptFieldValue("My Agency address/Street ")
        shouldAcceptFieldValue("Tester's Agency address/Street")
      }

      "there is a valid address" in {
        shouldAcceptFieldValue("My Agency address")
      }
    }
  }

  "address Line 3 and 4 bind" should {
    val addressLine23Mapping = FieldMappings.addressLine34.withPrefix("testKey")

    def bind(fieldValue: String) = addressLine23Mapping.bind(Map("testKey" -> fieldValue))

    def shouldRejectFieldValueAsTooLong(fieldValue: String): Unit = {
      bind(fieldValue) should matchPattern { case Left(List(FormError("testKey", List("error.maxLength"), _))) => }
    }

    def shouldAcceptFieldValue(fieldValue: String): Unit = {
      if (fieldValue.isEmpty) bind(fieldValue) shouldBe Right(None)
      else bind(fieldValue) shouldBe Right(Some(fieldValue))
    }

    "reject addressLine 2 and 3" when {
      "more than 35 characters" in {
        shouldRejectFieldValueAsTooLong("1234567891123456789212345678931234567")
      }
    }

    "accept address Line 2 and 3" when {
      "there is text and numbers" in {
        shouldAcceptFieldValue("99 My Agency address")
      }

      "there are valid symbols in the input" in {
        shouldAcceptFieldValue("My Agency address/Street ")
        shouldAcceptFieldValue("Tester's Agency address/Street")
      }

      "there is a valid address" in {
        shouldAcceptFieldValue("My Agency address")
      }

      "field is empty" in {
        shouldAcceptFieldValue("")
      }
    }
  }

  "agencyName and contactName bind" should {

    val agencyNameMapping = FieldMappings.name.withPrefix("testKey")

    def bind(fieldValue: String) = agencyNameMapping.bind(Map("testKey" -> fieldValue))

    def shouldRejectFieldValueAsInvalid(fieldValue: String): Unit = {
      bind(fieldValue) should matchPattern { case Left(List(FormError("testKey", List("error.name.invalid"), _))) => }
    }

    def shouldRejectFieldValueAsTooLong(fieldValue: String): Unit = {
      bind(fieldValue) should matchPattern { case Left(List(FormError("testKey", List("error.maxLength"), _))) => }
    }

    def shouldAcceptFieldValue(fieldValue: String): Unit = {
      bind(fieldValue) shouldBe Right(fieldValue)
    }

    "reject Agency name" when {

      "there is an ampersand character" in {
        bind("My Agency & Co") should matchPattern { case Left(List(FormError("testKey", List("error.name.invalid"), _))) => }
      }

      "there is an invalid character" in {
        shouldRejectFieldValueAsInvalid("My Agency; His Agency #1")
      }

      "there are more than 56 characters" in {
        shouldRejectFieldValueAsTooLong("123456789112345678921234567893123456789412345671234567893123456712345")
      }

      "input is empty" in {
        bind("").left.value should contain(FormError("testKey", "error.required"))
      }

      "input is only whitespace" in {
        bind("    ").left.value should contain only FormError("testKey", "error.required")
      }

      "field is not present" in {
        agencyNameMapping.bind(Map.empty).left.value should contain only FormError("testKey", "error.required")
      }
    }

    "accept Agency name" when {
      "there are valid characters" in {
        shouldAcceptFieldValue("My Agency")
        shouldAcceptFieldValue("My()Agency")
        shouldAcceptFieldValue("My--Agency")
        shouldAcceptFieldValue("My,Agency@")
        shouldAcceptFieldValue("My ! Agency.")
      }

      "there are numbers and letters" in {
        shouldAcceptFieldValue("The 100 Agency")
      }
    }
  }
}
