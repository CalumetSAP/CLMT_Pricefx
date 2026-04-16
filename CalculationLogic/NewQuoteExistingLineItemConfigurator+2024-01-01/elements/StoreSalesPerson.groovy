import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("SalesPerson")
api.local.salesPersonOptions = store.getFirstInput().getValue()

return store