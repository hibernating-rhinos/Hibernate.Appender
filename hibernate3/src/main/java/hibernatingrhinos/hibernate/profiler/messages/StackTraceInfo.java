//-----------------------------------------------------------------------
// <copyright file="StackTraceInfo.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.messages;

public class StackTraceInfo implements IProfilerMessage {
    
    private StackTraceFrame[] frames = new StackTraceFrame[] {};

    /**
     * @return the frames
     */
    public StackTraceFrame[] getFrames() {
        return this.frames;
    }

    /**
     * @param frames the frames to set
     */
    public void setFrames(StackTraceFrame[] frames) {
        this.frames = frames;
    }

    /**
     * @see hibernatingrhinos.hibernate.profiler.messages.IProfilerMessage#toXml()
     */
    public String toXml() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<StackTraceInfo>");
        for (int i = 0; i < frames.length; i++) {
            buffer.append(frames[i].toXml());
        }
        buffer.append("</StackTraceInfo>");
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
