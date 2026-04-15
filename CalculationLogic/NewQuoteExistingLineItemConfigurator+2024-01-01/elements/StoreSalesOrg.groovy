import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("SalesOrg")
api.local.salesOrg = store.getFirstInput().getValue()

return store