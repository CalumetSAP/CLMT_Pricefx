import net.pricefx.server.dto.calculation.ConfiguratorEntry

ConfiguratorEntry store = libs.BdpLib.UserInputs.store("Guardrails")
api.local.guardrails = store.getFirstInput().getValue()

return store