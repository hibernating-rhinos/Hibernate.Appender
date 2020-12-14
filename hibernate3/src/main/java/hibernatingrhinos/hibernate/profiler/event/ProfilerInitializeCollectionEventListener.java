//-----------------------------------------------------------------------
// <copyright file="ProfilerInitializeCollectionEventListener.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.event;

import org.apache.log4j.NDC;
import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.CollectionEntry;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.event.InitializeCollectionEvent;
import org.hibernate.event.def.DefaultInitializeCollectionEventListener;
import org.hibernate.pretty.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilerInitializeCollectionEventListener extends DefaultInitializeCollectionEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(ProfilerInitializeCollectionEventListener.class);

    /**
     * @see org.hibernate.event.def.DefaultInitializeCollectionEventListener#onInitializeCollection(org.hibernate.event.InitializeCollectionEvent)
     */
    @Override
    public void onInitializeCollection(InitializeCollectionEvent event) throws HibernateException {
        PersistentCollection collection = event.getCollection();
        SessionImplementor source = event.getSession();

        CollectionEntry ce = source.getPersistenceContext().getCollectionEntry(collection);
        
        if ( !collection.wasInitialized() && log.isTraceEnabled()) {
            NDC.push(MessageHelper.collectionInfoString(ce.getLoadedPersister(), ce.getLoadedKey(), source.getFactory()));
            super.onInitializeCollection(event);
            NDC.pop();
        } else {
            super.onInitializeCollection(event);
        }
    }
        
    
}
