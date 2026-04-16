def average = out.AverageLinehaulRate
if (!average) return null

def assessorialsMap = api.global.assessorials as Map

def vehicleGroup = out.MeansOfTransportation ?: ""
def wash = assessorialsMap["WASH"]?.get(vehicleGroup)?.Rate

return wash