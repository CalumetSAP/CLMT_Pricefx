List<String> warnings = []

def cost = out.Cost

if (!cost) {
    warnings.add("Item cost is missing")
}

if(warnings){
    String warningMsg = warnings.join("; ")
    api.yellowAlert(warningMsg)
    return warningMsg
}

return null