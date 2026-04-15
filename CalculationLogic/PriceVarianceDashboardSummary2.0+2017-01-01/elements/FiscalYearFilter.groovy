def lastPeriod = Acelerator.getLastClosePeriod()

def element = Acelerator.getFilter("Year")
if(element){
	Acelerator.dropdown(
			label: element?.label,
			field: element?.field,
			datamart: api.local.datamart,
			defaultValue: lastPeriod?.year,
			multiselect: true
	)
}
