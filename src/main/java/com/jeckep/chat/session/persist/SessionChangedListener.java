package com.jeckep.chat.session.persist;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

public class SessionChangedListener implements HttpSessionAttributeListener {
    public static final String ATTR_NAME = "SESSION_CHANGED";

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        setChanged(event);
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        setChanged(event);
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        setChanged(event);
    }

    private void setChanged(HttpSessionBindingEvent event){
        if(!ATTR_NAME.equals(event.getName())){
            event.getSession().setAttribute(ATTR_NAME,Boolean.TRUE);
        }
    }
}
