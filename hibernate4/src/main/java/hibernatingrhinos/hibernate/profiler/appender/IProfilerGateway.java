//-----------------------------------------------------------------------
// <copyright file="IProfilerGateway.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

import hibernatingrhinos.hibernate.profiler.messages.IProfilerMessage;

public interface IProfilerGateway {

    void initialize();
    void sendMessage(IProfilerMessage... messages);
    void shutdown();

}
