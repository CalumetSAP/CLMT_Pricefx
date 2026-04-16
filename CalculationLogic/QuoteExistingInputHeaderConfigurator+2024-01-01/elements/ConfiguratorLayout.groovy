final userInputs = libs.BdpLib.UserInputs

def layout = api.createConfiguratorEntry()

userInputs.with {

    setEntryInputsInColumns(layout, 1, [
            InputContractNumber.input,
            InputSoldTo.input,
            InputShipTo.input,
            InputContractPO.input
    ])

}

return layout