if(api.isSyntaxCheck()){
    api.inlineConfigurator("Inputs", "ListPriceMaintenanceConfigurator")
}else{
    return api.getParameter("Inputs")
}
