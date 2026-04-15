//
//def validate(email) {
//    def emailPattern = /[_A-Za-z0-9-]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})/
//
//    return (email ==~ emailPattern)
//}
//
//String variantToEmails = api.global.selectedVariant?.ToEmails
//
//def entry = libs.BdpLib.UserInputs.createInputString(
//        libs.DashboardConstantsLibrary.PricePublishing.TO_EMAILS_INPUT_KEY,
//        libs.DashboardConstantsLibrary.PricePublishing.TO_EMAILS_INPUT_LABEL,
//        false,
//        false
//)
//
//def values = entry.getFirstInput().getValue()
//def invalidEmails = []
//
//if(values) values?.replaceAll("\\s","")?.tokenize(",")?.each{ it ->
//    if(!validate(it)){
//        invalidEmails.add(it)
//    }
//}
//if((api.global.variantChanged && variantToEmails) || (!entry.getFirstInput().getValue() && variantToEmails)) {
//    entry.getFirstInput().setValue(variantToEmails)
//}
//
//if(invalidEmails){
//    entry.setMessage("<span style='color:red;font-weight:bold;'>Invalid Emails: ${invalidEmails?.join(", ")}</span>")
//}
//
//return entry