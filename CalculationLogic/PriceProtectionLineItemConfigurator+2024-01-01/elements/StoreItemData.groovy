import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("ItemData")
api.local.itemData = store.getFirstInput().getValue()

return store