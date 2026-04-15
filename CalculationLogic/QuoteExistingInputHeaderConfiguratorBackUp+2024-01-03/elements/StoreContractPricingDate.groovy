import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ContractPricingDate")
api.local.contractPricingDate = store.getFirstInput().getValue()

return store