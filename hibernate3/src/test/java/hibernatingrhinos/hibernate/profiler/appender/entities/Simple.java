//-----------------------------------------------------------------------
// <copyright file="Simple.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Simple {

    public int id;
    public String persistent;    

    @Id
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPersistent() {
        return this.persistent;
    }

    public void setPersistent(String persistent) {
        this.persistent = persistent;
    }
    
}
