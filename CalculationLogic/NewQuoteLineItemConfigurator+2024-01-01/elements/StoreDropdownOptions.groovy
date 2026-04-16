import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("DropdownOptions")
api.local.dropdownOptions = store.getFirstInput().getValue()

return store