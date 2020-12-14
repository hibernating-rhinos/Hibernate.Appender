//-----------------------------------------------------------------------
// <copyright file="SimpleEvictListener.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.spring;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.EvictEvent;
import org.hibernate.event.spi.EvictEventListener;

public class SimpleEvictListener implements EvictEventListener {

    /**
     * @see org.hibernate.event.EvictEventListener#onEvict(org.hibernate.event.EvictEvent)
     */
    public void onEvict(EvictEvent event) throws HibernateException {
        System.out.println(event);
    }

}
