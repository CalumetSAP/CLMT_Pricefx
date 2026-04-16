def condRate = null
if(api.local.secondaryKey.split('-')?.size() > 1){//has Scales in the SecKey
    def pl = api.local.secondaryKey.split('-')[0]
    def moq = api.local.secondaryKey.split('-')[1]
    api.global.ZDGS?.get(api.local.material + '|' + pl)?.each{ condRecNo, values ->
        values?.each{
            if(it.ScaleQuantity == moq){
                condRate = it.ConditionRate / 100000
            }
        }
    }
}
return condRate