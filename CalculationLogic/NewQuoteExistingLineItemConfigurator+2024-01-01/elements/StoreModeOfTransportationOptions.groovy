import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ModeOfTransportationOptions")
api.local.modeOfTransportationOptions = store.getFirstInput().getValue()

return store