def moq = null
if(api.local.secondaryKey.split('-')?.size() > 1){//has Scales in the SecKey
    moq = api.local.secondaryKey.split('-')[1]
}
return moq