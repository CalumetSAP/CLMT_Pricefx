def customerMap = out.FindCustomerData

def customerName = []
def address = []
def city = []
def state = []
def zipCode = []
def country = []

def customerData
api.local.soldTo?.each {
    customerData = customerMap[it]
    if (!customerData) return
    customerName.add(customerData?.Name)
    address.add(customerData?.Address)
    city.add(customerData?.City)
    state.add(customerData?.State)
    zipCode.add(customerData?.ZIP)
    country.add(customerData?.Country)
}

def data = []

data.add(["Title": "Bill to customer name", "Value": customerName.join(", ")])
data.add(["Title": "Address", "Value": address.join(", ")])
data.add(["Title": "City", "Value": city.join(", ")])
data.add(["Title": "State", "Value": state.join(", ")])
data.add(["Title": "ZIP Code", "Value": zipCode.join(", ")])
data.add(["Title": "Country", "Value": country.join(", ")])

return data