Acelerator.getFilters()?.each{ element ->
	if(element.field && !'Y'.equalsIgnoreCase(element.hide)){
		def options = [
				label: element?.label,
				field: element?.field,
				datamart: api.local.datamart,
				multiselect: element?.multiselect == "Y" ? true : false
		]
		return Acelerator.dropdown(
				options,
				true
		)
	}
}
