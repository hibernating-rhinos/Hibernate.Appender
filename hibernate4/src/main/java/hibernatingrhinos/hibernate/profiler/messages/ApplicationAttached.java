//-----------------------------------------------------------------------
// <copyright file="ApplicationAttached.java" company="Hibernating Rhinos LTD">
//     Copyright (c) Hibernating Rhinos LTD. All rights reserved.
// </copyright>
//-----------------------------------------------------------------------
package hibernatingrhinos.hibernate.profiler.messages;

public class ApplicationAttached implements IProfilerMessage {

	private String applicationName;
	private String guid;

	public ApplicationAttached(String applicationName, String guid) {
		this.applicationName = applicationName;
		this.guid = guid;
	}

	public String toXml() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<ApplicationAttached>");
        buffer.append("<Applicaton><![CDATA[").append(applicationName).append("]]</Applicaton>");
        buffer.append("<Guid>").append(guid).append("</Guid>");
        buffer.append("</ApplicationAttached>");
        return buffer.toString();
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getGuid() {
		return guid;
	}
}
