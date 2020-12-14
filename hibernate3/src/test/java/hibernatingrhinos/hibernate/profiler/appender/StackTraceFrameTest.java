//-----------------------------------------------------------------------
// <copyright file="StackTraceFrameTests.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import static org.junit.Assert.*;

import hibernatingrhinos.hibernate.profiler.messages.StackTraceFrame;
import hibernatingrhinos.hibernate.profiler.messages.StackTraceInfo;

import org.junit.Test;

public class StackTraceFrameTest {

    @Test
    public void can_generate_proper_stack_frame() {
        StackTraceInfoGenerator generator = new StackTraceInfoGenerator();
        StackTraceInfo info = generator.getStackTrace();

        boolean foundFrame = false;
        for (StackTraceFrame frame : info.getFrames()) {
            if (!"StackTraceFrameTest".equals(frame.getType()))
                continue;

            assertEquals("hibernatingrhinos.hibernate.profiler.appender", frame.getNamespace());
            assertEquals("StackTraceFrameTest", frame.getType());
            assertEquals("StackTraceFrameTest.java", frame.getFilename());
            assertEquals("can_generate_proper_stack_frame", frame.getMethod());
            assertTrue(frame.getLine() > 0);

            foundFrame = true;
            break;
        }
        assertTrue("Could not find expected frame", foundFrame);
    }

}

