if (api.isInputGenerationExecution()) return

final soldToConstants = libs.QuoteConstantsLibrary.SoldToQuote

def rejectionReason = InputRejectionReason?.input?.getValue()
def inputMatrix = InputShipToMatrix?.input
def readOnlyColumns = inputMatrix?.getParameterConfig()?.readOnlyColumns ?: []

if (rejectionReason) {
    def rejectionReasonMap = api.local.dropdownOptions && !api.isInputGenerationExecution() ? api.local.dropdownOptions["RejectionReason"] as Map : [:]
    def inputValues = inputMatrix?.getValue() ?: []

    def rejectionReasonValue = rejectionReasonMap?.getOrDefault(rejectionReason, rejectionReason)
    def newValue = inputValues.collect {
        it + [(soldToConstants.MATRIX_REJECTION_REASON_ID): rejectionReasonValue]
    }

    InputShipToMatrix?.input?.setValue(newValue)

    readOnlyColumns.add(soldToConstants.MATRIX_REJECTION_REASON_ID)
} else {
    readOnlyColumns.remove(soldToConstants.MATRIX_REJECTION_REASON_ID)
}