//-----------------------------------------------------------------------
// <copyright file="SessionFactoryStats.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.messages;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SessionFactoryStats implements IProfilerMessage {
    public String name;
    public Map statistics = new HashMap();
    
    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the statistics
     */
    public Map getStatistics() {
        return this.statistics;
    }
    
    /**
     * @param statistics the statistics to set
     */
    public void setStatistics(Map statistics) {
        this.statistics = statistics;
    }

    /**
     * @see hibernatingrhinos.hibernate.profiler.messages.IProfilerMessage#toXml()
     */
    public String toXml() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<SessionFactoryStats>");
        buffer.append("<Name>").append(name).append("</Name>");
        
        if (statistics.entrySet().size() > 0) {
            buffer.append("<Statistics>");
        
            for (Iterator it = statistics.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Entry)it.next();
                
                Object value = entry.getValue();
                
                if (value instanceof String[]) {
                    createArrayEntry(buffer, entry.getKey(), (String[])value);
                } else {
                    createEntry(buffer, entry.getKey(), entry.getValue());
                }
            }
            buffer.append("</Statistics>");
        }
        buffer.append("</SessionFactoryStats>");
        
        return buffer.toString();
    }
    
    private void createArrayEntry(StringBuffer buffer, Object key, String[] value) {
        buffer.append("<Entry>");
        buffer.append("<Key>").append(key).append("</Key>");
        buffer.append("<Values>");
        Iterator it = Arrays.asList(value).iterator();
        while (it.hasNext()) {
            buffer.append("<Value><![CDATA[").append(it.next()).append("]]></Value>");
        }                            
        buffer.append("</Values>");
        buffer.append("</Entry>");        
    }

    private void createEntry(StringBuffer buffer, Object key, Object value) {
        buffer.append("<Entry>");
        buffer.append("<Key>").append(key).append("</Key>");
        buffer.append("<Value><![CDATA[").append(value).append("]]></Value>");
        buffer.append("</Entry>");
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return toXml();
    }
   
}
