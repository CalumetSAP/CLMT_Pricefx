def defineEmailSubjectAndBody(quote, workflowHistory, approvable){
    def emails = getEmails(quote)

    if(workflowHistory.steps.findAll{it.approvalStep}.every{it.approved}){
        sendEmail(approvable, emails, "Workflow Approved", "approved")
    } else if(workflowHistory.steps.findAll{it.approvalStep}.any{it.denied}){
        sendEmail(approvable, emails, "Workflow Denied", "denied")
    } else if (workflowHistory?.steps) {
        if (workflowHistory?.activeStep?.approved) {
            sendEmail(approvable, emails, "Workflow Step Approved", "approved")
        } else if (workflowHistory?.activeStep?.denied) {
            sendEmail(approvable, emails, "Workflow Step Denied", "denied")
        }
    }
}

def defineDealEmailSubjectAndBody (quote) {
    def approvable = [:]

    approvable.put("uniqueName", quote?.uniqueName)
    approvable.put("label", quote?.label)
    approvable.put("typedId", quote?.typedId)
    approvable.put("createdByName", quote?.createdByName)

    def emails = getEmails(quote)

    sendConvertedToDealEmail(approvable, emails)
}

private getEmails(quote) {
    def params = getParamsFromQuote(quote)

    def userGroup = params?.userGroup
    def creationUser = params?.creationUser
    def divisions = params?.divisions

    if (userGroup == libs.QuoteConstantsLibrary.General.USER_GROUP_SALES) return [libs.QuoteLibrary.Query.findEmailByUser(creationUser)]

    def filters = []

    filters.add(Filter.equal("key1", userGroup))

    if (divisions) filters.add(Filter.in("key2", divisions))

    return api.findLookupTableValues(libs.QuoteConstantsLibrary.Tables.WORKFLOW_EMAILS, ["attribute1"], null, *filters)?.attribute1
}

private getParamsFromQuote(quote) {
    final headerConstants = libs.QuoteConstantsLibrary.HeaderConfigurator
    final calculations = libs.QuoteLibrary.Calculations
    final lineItemConstants = libs.QuoteConstantsLibrary.LineItem
    final constants = libs.QuoteConstantsLibrary.General

    def quoteInputConfigurator = quote?.inputs?.find { it.name == headerConstants.INPUTS_NAME }?.value

    def creationUser = quote?.createdByName

    def isSalesCreationUser = api.isUserInGroup(constants.USER_GROUP_SALES, creationUser)
    def isPricingCreationUser = api.isUserInGroup(constants.USER_GROUP_PRICING, creationUser)

    def userGroup = isSalesCreationUser ? constants.USER_GROUP_SALES : constants.USER_GROUP_PRICING

    def divisions = []

    if (quote?.get("quoteType") == "New Contract" || quote?.get("quoteType") == "NewContract") {
        divisions.add(quoteInputConfigurator?.get(headerConstants.DIVISION_ID))
    }else{
        for (lineItem in quote?.lineItems) {
            if (lineItem.folder) continue

//            if (calculations.getInputValue(lineItem, lineItemConstants.REJECTION_REASON_ID)) continue

            def dsData = calculations.getInputValue(lineItem, lineItemConstants.DATA_SOURCE_VALUES_HIDDEN_ID)
            def division = dsData?.get("Division")
            if (!divisions.contains(division)) divisions.add(division)
        }
    }

    return [
            "divisions"   : divisions,
            "userGroup"   : userGroup,
            "creationUser": creationUser
    ]
}

private sendEmail(approvable, toEmails, status, bodyStatus) {
    def quoteNumber = approvable.uniqueName
    def quoteName = approvable.label
    def quoteTypedId = approvable.typedId
    def subject = "Pricefx - ${status} - Quote: " + quoteNumber + " - " + quoteName
    def link = api.getBaseURL() + "/pricefx/" + api.currentPartitionName() + "/saml/signon/?RelayState=Partition--targetPage=quotes--targetPageState=" + quoteTypedId
    def emailTemplate = """
        <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
        
        <html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
        
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
            <meta name="viewport" content="width=device-width">
            <title>Pricefx - Workflow Step ${status} - Quote: ${quoteNumber} - ${quoteName}</title>
            <style>
                @media only screen {
                    html {
                        min-height: 100%;
                        background: #F5FAFE
                    }
                }
                @media only screen and (max-width:620px) {
                    table.body img {
                        width: auto;
                        height: auto
                    }
                    table.body center {
                        min-width: 0!important
                    }
                    table.body .container {
                        width: 95%!important
                    }
                    table.body .columns {
                        height: auto!important;
                        -moz-box-sizing: border-box;
                        -webkit-box-sizing: border-box;
                        box-sizing: border-box;
                        padding-left: 20px!important;
                        padding-right: 20px!important
                    }
                    th.small-10 {
                        display: inline-block!important;
                        width: 83.33333%!important
                    }
                    th.small-12 {
                        display: inline-block!important;
                        width: 100%!important
                    }
                    table.menu {
                        width: 100%!important
                    }
                    table.menu td,
                    table.menu th {
                        width: auto!important;
                        display: inline-block!important
                    }
                    table.menu[align=center] {
                        width: auto!important
                    }
                }
            </style>
        </head>
        <body style="-moz-box-sizing:border-box;-ms-text-size-adjust:100%;-webkit-box-sizing:border-box;-webkit-text-size-adjust:100%;Margin:0;box-sizing:border-box;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0;min-width:100%;padding:0;text-align:left;width:100%!important"><span class="preheader" style="color:#F5FAFE;display:none!important;font-size:1px;line-height:1px;max-height:0;max-width:0;mso-hide:all!important;opacity:0;overflow:hidden;visibility:hidden"></span>
            <table class="body" style="Margin:0;background:#F5FAFE;border-collapse:collapse;border-spacing:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;height:100%;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;width:100%">
                <tr style="padding:0;text-align:left;vertical-align:top">
                    <td class="center" align="center" valign="top" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                        <center data-parsed="" style="min-width:600px;width:100%">
                            <table class="spacer float-center" style="Margin:0 auto;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:100%">
                                <tbody>
                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td height="24px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:24px;font-weight:400;hyphens:auto;line-height:24px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                    </tr>
                                </tbody>
                            </table>
                            <table align="center" class="container header float-center" style="Margin:0 auto;background:#fefefe;background-color:#F5FAFE;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:600px">
                                <tbody>
                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                                            <center data-parsed="" style="min-width:600px;width:100%"><img src="https://static.pricefx.com/logo/classic/pricefx_logo_black_whitebg_134x36.png" alt="Pricefx" class="pfx float-center" align="center" style="-ms-interpolation-mode:bicubic;Margin:0 auto;clear:both;display:block;float:none;margin:0 auto;max-width:100%;outline:0;text-align:center;text-decoration:none;width:110px">
                                                <center align="center" class="float-center" data-parsed="" style="min-width:600px;width:100%"></center>
                                            </center>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                            <table class="spacer float-center" style="Margin:0 auto;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:100%">
                                <tbody>
                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td height="24px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:24px;font-weight:400;hyphens:auto;line-height:24px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                    </tr>
                                </tbody>
                            </table>
                            <table align="center" class="container body-drip float-center" style="Margin:0 auto;background:#fefefe;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:600px">
                                <tbody>
                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                                            <table class="spacer" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                <tbody>
                                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                                        <td height="48px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:48px;font-weight:400;hyphens:auto;line-height:48px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                            <table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
                                                <tbody>
                                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                                        <th class="small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:20px;padding-right:20px;text-align:left;width:580px">
                                                            <table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                                <tr style="padding:0;text-align:left;vertical-align:top">
                                                                    <th style="Margin:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">
                                                                        <h2 class="text-center" style="Margin:0;Margin-bottom:10px;color:inherit;font-family:Helvetica,Arial,sans-serif;font-size:30px;font-weight:400;line-height:1.3;margin:0;margin-bottom:10px;padding:0;text-align:center;word-wrap:normal">${approvable.uniqueName} - ${approvable.label}</h2>
                                                                        <h4 class="text-center" style="Margin:0;Margin-bottom:10px;color:inherit;font-family:Helvetica,Arial,sans-serif;font-size:24px;font-weight:400;line-height:1.3;margin:0;margin-bottom:10px;padding:0;text-align:center;word-wrap:normal">Quote has been ${bodyStatus}</h4>
                                                                        <table class="spacer" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                                            <tbody>
                                                                                <tr style="padding:0;text-align:left;vertical-align:top">
                                                                                    <td height="16px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:16px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                                                                </tr>
                                                                            </tbody>
                                                                        </table>
                                                                        <p class="text-center" style="Margin:0;Margin-bottom:10px;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.8;margin:0;margin-bottom:10px;padding:0;text-align:center">Created by: ${approvable.createdByName}</p>
                                                                        <p class="text-center" style="Margin:0;Margin-bottom:10px;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.8;margin:0;margin-bottom:10px;padding:0;text-align:center">Workflow step: ${workflowHistory?.activeStep?.uniqueName}</p>
                                                                    </th>
                                                                    <th class="expander" style="Margin:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
                                                                </tr>
                                                            </table>
                                                        </th>
                                                    </tr>
                                                </tbody>
                                            </table>
                                            <table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
                                                <tbody>
                                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                                        <th class="small-12 large-4 columns first" style="Margin:0 auto;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:20px;padding-right:10px;text-align:left;width:180px">
                                                            <table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                                <tr style="padding:0;text-align:left;vertical-align:top">
                                                                    <th style="Margin:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">
                                                                        <table class="button radius large expanded" style="Margin:0 0 16px 0;border-collapse:collapse;border-radius:3px;border-spacing:0;margin:0 0 16px 0;margin-bottom:8px;padding:0;text-align:left;vertical-align:top;width:100%!important">
                                                                            <tr style="padding:0;text-align:left;vertical-align:top">
                                                                                <td style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                                                                                    <table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                                                        <tr style="padding:0;text-align:left;vertical-align:top">
                                                                                            <td style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;background:#0070D2;border:none;border-collapse:collapse!important;border-radius:3px;color:#fefefe;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                                                                                                <center data-parsed="" style="min-width:0;width:100%"><a href="${link}" align="center" class="float-center" style="Margin:0;border:0 solid #0070D2;border-radius:3px;color:#fefefe;display:inline-block;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:700;line-height:1.3;margin:0;padding:15px 25px 15px 25px;padding-left:0;padding-right:0;text-align:center;text-decoration:none;width:100%">View Quote</a></center>
                                                                                            </td>
                                                                                        </tr>
                                                                                    </table>
                                                                                </td>
                                                                                <td class="expander" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0!important;text-align:left;vertical-align:top;visibility:hidden;width:0;word-wrap:break-word"></td>
                                                                            </tr>
                                                                        </table>
                                                                    </th>
                                                                </tr>
                                                            </table>
                                                        </th>
                                                    </tr>
                                                </tbody>
                                            </table>
                                            <table class="spacer" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                <tbody>
                                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                                        <td height="36px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:36px;font-weight:400;hyphens:auto;line-height:36px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                            <spaceer size="24"></spaceer>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                            <table class="spacer float-center" style="Margin:0 auto;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:100%">
                                <tbody>
                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td height="48px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:48px;font-weight:400;hyphens:auto;line-height:48px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                    </tr>
                                </tbody>
                            </table>
                            <table class="spacer float-center" style="Margin:0 auto;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:100%">
                                <tbody>
                                   <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td height="48px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:48px;font-weight:400;hyphens:auto;line-height:48px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                    </tr>
                                </tbody>
                            </table>
                        </center>
                    </td>
                </tr>
            </table>
            <!-- prevent Gmail on iOS font size manipulation -->
            <div style="display:none;white-space:nowrap;font:15px courier;line-height:0">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</div>
        </body> 
        </html>
    """

    toEmails?.each {
        api.sendEmail(it, subject, emailTemplate)
    }
}

private sendConvertedToDealEmail(approvable, toEmails) {
    def quoteNumber = approvable.uniqueName
    def quoteName = approvable.label
    def quoteTypedId = approvable.typedId
    def subject = "Pricefx - Quote Converted to Deal"
    def link = api.getBaseURL() + "/pricefx/" + api.currentPartitionName() + "/saml/signon/?RelayState=Partition--targetPage=quotes--targetPageState=" + quoteTypedId
    def emailTemplate = """
        <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
        
        <html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
        
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
            <meta name="viewport" content="width=device-width">
            <title>Pricefx - Quote Converted to Deal</title>
            <style>
                @media only screen {
                    html {
                        min-height: 100%;
                        background: #F5FAFE
                    }
                }
                @media only screen and (max-width:620px) {
                    table.body img {
                        width: auto;
                        height: auto
                    }
                    table.body center {
                        min-width: 0!important
                    }
                    table.body .container {
                        width: 95%!important
                    }
                    table.body .columns {
                        height: auto!important;
                        -moz-box-sizing: border-box;
                        -webkit-box-sizing: border-box;
                        box-sizing: border-box;
                        padding-left: 20px!important;
                        padding-right: 20px!important
                    }
                    th.small-10 {
                        display: inline-block!important;
                        width: 83.33333%!important
                    }
                    th.small-12 {
                        display: inline-block!important;
                        width: 100%!important
                    }
                    table.menu {
                        width: 100%!important
                    }
                    table.menu td,
                    table.menu th {
                        width: auto!important;
                        display: inline-block!important
                    }
                    table.menu[align=center] {
                        width: auto!important
                    }
                }
            </style>
        </head>
        <body style="-moz-box-sizing:border-box;-ms-text-size-adjust:100%;-webkit-box-sizing:border-box;-webkit-text-size-adjust:100%;Margin:0;box-sizing:border-box;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0;min-width:100%;padding:0;text-align:left;width:100%!important"><span class="preheader" style="color:#F5FAFE;display:none!important;font-size:1px;line-height:1px;max-height:0;max-width:0;mso-hide:all!important;opacity:0;overflow:hidden;visibility:hidden"></span>
            <table class="body" style="Margin:0;background:#F5FAFE;border-collapse:collapse;border-spacing:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;height:100%;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;width:100%">
                <tr style="padding:0;text-align:left;vertical-align:top">
                    <td class="center" align="center" valign="top" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                        <center data-parsed="" style="min-width:600px;width:100%">
                            <table class="spacer float-center" style="Margin:0 auto;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:100%">
                                <tbody>
                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td height="24px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:24px;font-weight:400;hyphens:auto;line-height:24px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                    </tr>
                                </tbody>
                            </table>
                            <table align="center" class="container header float-center" style="Margin:0 auto;background:#fefefe;background-color:#F5FAFE;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:600px">
                                <tbody>
                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                                            <center data-parsed="" style="min-width:600px;width:100%"><img src="https://static.pricefx.com/logo/classic/pricefx_logo_black_whitebg_134x36.png" alt="Pricefx" class="pfx float-center" align="center" style="-ms-interpolation-mode:bicubic;Margin:0 auto;clear:both;display:block;float:none;margin:0 auto;max-width:100%;outline:0;text-align:center;text-decoration:none;width:110px">
                                                <center align="center" class="float-center" data-parsed="" style="min-width:600px;width:100%"></center>
                                            </center>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                            <table class="spacer float-center" style="Margin:0 auto;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:100%">
                                <tbody>
                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td height="24px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:24px;font-weight:400;hyphens:auto;line-height:24px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                    </tr>
                                </tbody>
                            </table>
                            <table align="center" class="container body-drip float-center" style="Margin:0 auto;background:#fefefe;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:600px">
                                <tbody>
                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                                            <table class="spacer" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                <tbody>
                                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                                        <td height="48px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:48px;font-weight:400;hyphens:auto;line-height:48px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                            <table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
                                                <tbody>
                                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                                        <th class="small-12 large-12 columns first last" style="Margin:0 auto;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:20px;padding-right:20px;text-align:left;width:580px">
                                                            <table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                                <tr style="padding:0;text-align:left;vertical-align:top">
                                                                    <th style="Margin:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">
                                                                        <h2 class="text-center" style="Margin:0;Margin-bottom:10px;color:inherit;font-family:Helvetica,Arial,sans-serif;font-size:30px;font-weight:400;line-height:1.3;margin:0;margin-bottom:10px;padding:0;text-align:center;word-wrap:normal">${approvable.uniqueName} - ${approvable.label}</h2>
                                                                        <h4 class="text-center" style="Margin:0;Margin-bottom:10px;color:inherit;font-family:Helvetica,Arial,sans-serif;font-size:24px;font-weight:400;line-height:1.3;margin:0;margin-bottom:10px;padding:0;text-align:center;word-wrap:normal">Quote has been converted to Deal</h4>
                                                                        <table class="spacer" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                                            <tbody>
                                                                                <tr style="padding:0;text-align:left;vertical-align:top">
                                                                                    <td height="16px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:16px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                                                                </tr>
                                                                            </tbody>
                                                                        </table>
                                                                        <p class="text-center" style="Margin:0;Margin-bottom:10px;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.8;margin:0;margin-bottom:10px;padding:0;text-align:center">Created by: ${approvable.createdByName}</p>
                                                                    </th>
                                                                    <th class="expander" style="Margin:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0;padding:0!important;text-align:left;visibility:hidden;width:0"></th>
                                                                </tr>
                                                            </table>
                                                        </th>
                                                    </tr>
                                                </tbody>
                                            </table>
                                            <table class="row" style="border-collapse:collapse;border-spacing:0;display:table;padding:0;position:relative;text-align:left;vertical-align:top;width:100%">
                                                <tbody>
                                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                                        <th class="small-12 large-4 columns first" style="Margin:0 auto;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0 auto;padding:0;padding-bottom:16px;padding-left:20px;padding-right:10px;text-align:left;width:180px">
                                                            <table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                                <tr style="padding:0;text-align:left;vertical-align:top">
                                                                    <th style="Margin:0;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;line-height:1.3;margin:0;padding:0;text-align:left">
                                                                        <table class="button radius large expanded" style="Margin:0 0 16px 0;border-collapse:collapse;border-radius:3px;border-spacing:0;margin:0 0 16px 0;margin-bottom:8px;padding:0;text-align:left;vertical-align:top;width:100%!important">
                                                                            <tr style="padding:0;text-align:left;vertical-align:top">
                                                                                <td style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                                                                                    <table style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                                                        <tr style="padding:0;text-align:left;vertical-align:top">
                                                                                            <td style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;background:#0070D2;border:none;border-collapse:collapse!important;border-radius:3px;color:#fefefe;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">
                                                                                                <center data-parsed="" style="min-width:0;width:100%"><a href="${link}" align="center" class="float-center" style="Margin:0;border:0 solid #0070D2;border-radius:3px;color:#fefefe;display:inline-block;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:700;line-height:1.3;margin:0;padding:15px 25px 15px 25px;padding-left:0;padding-right:0;text-align:center;text-decoration:none;width:100%">View Quote</a></center>
                                                                                            </td>
                                                                                        </tr>
                                                                                    </table>
                                                                                </td>
                                                                                <td class="expander" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:16px;font-weight:400;hyphens:auto;line-height:1.3;margin:0;padding:0!important;text-align:left;vertical-align:top;visibility:hidden;width:0;word-wrap:break-word"></td>
                                                                            </tr>
                                                                        </table>
                                                                    </th>
                                                                </tr>
                                                            </table>
                                                        </th>
                                                    </tr>
                                                </tbody>
                                            </table>
                                            <table class="spacer" style="border-collapse:collapse;border-spacing:0;padding:0;text-align:left;vertical-align:top;width:100%">
                                                <tbody>
                                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                                        <td height="36px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:36px;font-weight:400;hyphens:auto;line-height:36px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                            <spaceer size="24"></spaceer>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                            <table class="spacer float-center" style="Margin:0 auto;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:100%">
                                <tbody>
                                    <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td height="48px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:48px;font-weight:400;hyphens:auto;line-height:48px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                    </tr>
                                </tbody>
                            </table>
                            <table class="spacer float-center" style="Margin:0 auto;border-collapse:collapse;border-spacing:0;float:none;margin:0 auto;padding:0;text-align:center;vertical-align:top;width:100%">
                                <tbody>
                                   <tr style="padding:0;text-align:left;vertical-align:top">
                                        <td height="48px" style="-moz-hyphens:auto;-webkit-hyphens:auto;Margin:0;border-collapse:collapse!important;color:#0a0a0a;font-family:Helvetica,Arial,sans-serif;font-size:48px;font-weight:400;hyphens:auto;line-height:48px;margin:0;mso-line-height-rule:exactly;padding:0;text-align:left;vertical-align:top;word-wrap:break-word">&#xA0;</td>
                                    </tr>
                                </tbody>
                            </table>
                        </center>
                    </td>
                </tr>
            </table>
            <!-- prevent Gmail on iOS font size manipulation -->
            <div style="display:none;white-space:nowrap;font:15px courier;line-height:0">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</div>
        </body> 
        </html>
    """

    toEmails?.each {
        api.sendEmail(it, subject, emailTemplate)
    }
}