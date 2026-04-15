void track(Map dashboardProperties) {
    Map constConfig = libs.SIP_Dashboards_Commons.ConstConfig.MIX_PANEL_INFO

    String logicName = dashboardProperties.LOGIC_NAME
    String acceleratorName = constConfig.ACCELERATOR_NAME
    String moduleName = constConfig.MODULE_NAME
    Map properties = createTrackingProperties(dashboardProperties)

    libs.SharedAccLib.TrackingUtils.track(acceleratorName, logicName, moduleName, properties)
}

protected Map createTrackingProperties(Map dashboardProperties) {
    Map constConfig = libs.SIP_Dashboards_Commons.ConstConfig.MIX_PANEL_INFO
    String dashboardLabel = dashboardProperties.DASHBOARD_LABEL
    String dashboardName = dashboardProperties.DASHBOARD_NAME

    return [(constConfig.FIELD_NAME.DASHBOARD_LABEL): dashboardLabel,
            (constConfig.FIELD_NAME.DASHBOARD_NAME) : dashboardName]
}