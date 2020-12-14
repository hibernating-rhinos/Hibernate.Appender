//-----------------------------------------------------------------------
// <copyright file="ProfilerGatewayFactory.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.appender;

/**
 * Simple delegate to gateway to make it testable.
 */
public abstract class ProfilerGatewayFactory {
       
    public abstract IProfilerGateway newInstance(String address, int port);
    
    private static ProfilerGatewayFactory Factory = new DefaultSocketGateway();
    
    public static void setGatewayFactory(ProfilerGatewayFactory profilerGatewayFactory) {
        Factory = profilerGatewayFactory;
    }
    
    public static IProfilerGateway getGateway(String address, int port) {
        return Factory.newInstance(address, port);
    }
    
    private static class DefaultSocketGateway extends ProfilerGatewayFactory {

        public IProfilerGateway newInstance(String address, int port) {
            throw new UnsupportedOperationException();
        }
        
    }
    
}
