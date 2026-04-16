final userInputs = libs.BdpLib.UserInputs

def layout = api.createConfiguratorEntry()

userInputs.with {

    setEntryInputsInColumns(layout, 1, [
            InputContractNumber.input,
            InputSoldTo.input,
            InputShipTo.input,
            InputMaterial.input,
            InputLineRejected.input,
            InputSalesPerson.input,
            InputAddContractLines.input,
            InputContractPO.input
    ])

}

return layout