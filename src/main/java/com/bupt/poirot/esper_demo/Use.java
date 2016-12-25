package com.bupt.poirot.esper_demo;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class Use {
    public static void main(String[] args) {
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
        String expression = "select avg(price) from com.bupt.poirot.esper_demo.OrderEvent.win:time(30 sec)";
        EPStatement statement = epService.getEPAdministrator().createEPL(expression);

        MyListener listener = new MyListener();
        statement.addListener(listener);

        OrderEvent event = new OrderEvent("shirt", 74.50);
        epService.getEPRuntime().sendEvent(event);

        event = new OrderEvent("shirt", 100);
        epService.getEPRuntime().sendEvent(event);

    }
}
