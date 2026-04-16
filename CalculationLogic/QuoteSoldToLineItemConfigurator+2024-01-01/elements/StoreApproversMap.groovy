import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ApproversMap")
api.local.approversMap = store.getFirstInput().getValue()

return store