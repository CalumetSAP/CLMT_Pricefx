import net.pricefx.common.api.InputType

def ce = api.createConfiguratorEntry(InputType.TEXTUSERENTRY,"TermsInput")
ce.getFirstInput().setLabel("Terms")

return ce