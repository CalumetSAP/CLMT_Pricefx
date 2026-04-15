def header = input["header"]
def data = input["data"]
def req = [data: [
        header: header,
        data : data
]]

def body = api.jsonEncode(req)?.toString()

api.boundCall("SystemUpdate", "/loaddata/JLTV", body, false)