//-----------------------------------------------------------------------
// <copyright file="StackTraceFrame.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.messages;

import java.io.File;

public class StackTraceFrame implements IProfilerMessage {

    private String type;
    private int line;
    private int column;
    private String filename;
    private String method;
    private String namespace;
      
    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the line
     */
    public int getLine() {
        return this.line;
    }

    /**
     * @param line the line to set
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * @return the column
     */
    public int getColumn() {
        return this.column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return this.namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public boolean getFileExists() {
        if (filename == null)
            return false;
        return new File(filename).exists();
    }

    /**
     * @see hibernatingrhinos.hibernate.profiler.messages.IProfilerMessage#toXml()
     */
    public String toXml() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<StacKTraceFrame>");
        buffer.append("<Type>").append(type).append("</Type>");
        buffer.append("<Line>").append(line).append("</Line>");
        buffer.append("<Column>").append(column).append("</Column>");
        buffer.append("<Filename>").append(filename).append("</Filename>");
        buffer.append("<Namespace>").append(namespace).append("</Namespace>");
        buffer.append("</StacKTraceFrame>");
        return buffer.toString();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toXml();
    }
    
}
