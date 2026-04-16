import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("DSData")
api.local.dataSourceData = store.getFirstInput().getValue()

return store