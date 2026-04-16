import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ValidFromDate")
api.local.validFromDate = store.getFirstInput().getValue()

return store