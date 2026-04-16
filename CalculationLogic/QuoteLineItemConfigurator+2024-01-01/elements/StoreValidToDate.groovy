import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ValidToDate")
api.local.validToDate = store.getFirstInput().getValue()

return store