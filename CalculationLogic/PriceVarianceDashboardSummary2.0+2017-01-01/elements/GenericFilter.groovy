/**
 * Additional generic filter user entry, that allows miscellaneous filters to be added.
 * Example usage: Region = Europe
 * User default values are applied if applicable.
 */
String datamartName = api.findLookupTableValues("VarianceGeneralRules").collect { it.attribute1 }?.first()
api.local.datamart = datamartName
return libs.SIP_Dashboards_Commons.InputUtils.getDatamartFilterUserEntry(datamartName)