//-----------------------------------------------------------------------
// <copyright file="StackTraceInfoGenerator.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import hibernatingrhinos.hibernate.profiler.messages.StackTraceFrame;
import hibernatingrhinos.hibernate.profiler.messages.StackTraceInfo;

/**
 * Simple implementation for now 
 */
public class StackTraceInfoGenerator {

    public StackTraceInfo getStackTrace() {       
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        
        StackTraceFrame[] frames = new StackTraceFrame[stack.length];        
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement frame = stack[i];            
            if (frame == null) continue;
            frames[i] = convertToStackTraceFrame(frame);            
        }
        
        StackTraceInfo stackTraceInfo = new StackTraceInfo();
        stackTraceInfo.setFrames(frames);
        return stackTraceInfo;
    }

    private StackTraceFrame convertToStackTraceFrame(StackTraceElement stackTraceElement) {
        
        StackTraceFrame frame = new StackTraceFrame();
               
        frame.setFilename(stackTraceElement.getFileName());
        frame.setLine(stackTraceElement.getLineNumber());
        frame.setMethod(stackTraceElement.getMethodName());
        
        String className = stackTraceElement.getClassName();
        if (className != null) {
            int packageIndex = className.lastIndexOf('.');
            frame.setType(packageIndex != -1 ? className.substring(packageIndex + 1) : className);
            frame.setNamespace(packageIndex != -1 ? className.substring(0, packageIndex) : "");
        }
        return frame;
    }
    
}
