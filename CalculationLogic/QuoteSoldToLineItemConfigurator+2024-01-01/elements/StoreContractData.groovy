import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ContractData")
api.local.contractData = store.getFirstInput().getValue()

return store