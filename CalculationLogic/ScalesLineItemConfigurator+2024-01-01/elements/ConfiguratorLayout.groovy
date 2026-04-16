final userInputs = libs.BdpLib.UserInputs

def layout = api.createConfiguratorEntry()

userInputs.with {

    setEntryInputsInColumns(layout, 1, [
            InputScales.input
    ])

}

return layout