//import net.pricefx.server.dto.calculation.DashboardController
//
//clicmanager.sendemail
//
//DashboardController controller = api.newController()
//
//def configurator = out.Filters
//def pricingDate = configurator?.get(libs.DashboardConstantsLibrary.PricePublishing.PRICING_DATE_INPUT_KEY)
//
//Map parameters = [
//        "templateName":"Price Letter - SPS",
//        "attachmentType":"PDF",
//        "subject":"Quote P-849"
//]
// //       {"data":{"typedId":"849.Q","templateName":"Price Letter - SPS","attachmentType":"PDF","subject":"Quote P-849","emailText":"Additional Message","recipients":[{"name":"Mateo","email":"mateo@bigdatapricing.com"},{"name":"Mateo null","email":"mateo@bigdatapricing.com"}]}}
//
//
//controller.addDownloadButton('Download PDF',
//        """/formulamanager.executeformula/${templateLogicName}?templateName=${publishingTemplate}&output=pdf&fileName=${filename}""",
//        api.jsonEncode(parameters)?.toString())
//
//return controller