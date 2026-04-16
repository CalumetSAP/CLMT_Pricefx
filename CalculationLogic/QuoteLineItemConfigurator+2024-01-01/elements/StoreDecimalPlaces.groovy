import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("DecimalPlaces")
api.local.decimalPlaces = store.getFirstInput().getValue()

return store