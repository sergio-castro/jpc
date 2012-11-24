package org.jpc;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.util.Properties;
import org.minitoolbox.Preferences;

public class JpcPreferences extends Preferences {

	public static final String JPC_NAME = "The Java-Prolog Connectivity Library";
	public static final String JPC_SHORT_NAME = "JPC";
	
	//Properties configuring the behaviour of JPC
	public final static String LOGTALK_HOME_ENV_VAR = "LOGTALKHOME";  //needed by the framework to find the integration scripts with Logtalk
	public final static String LOGTALK_USER_ENV_VAR = "LOGTALKUSER"; //logtalk user directory environment variable (used as a tmp directory if available)
	public final static String SYSTEM_TEMP_DIRECTORY_ENV_VAR = "tmp"; //the tmp environment variable
	
	public JpcPreferences() {
	}
	
	public JpcPreferences(Properties properties) {
		super(properties);
	}
	
	public String logtalkIntegrationScript(String engineName) {
		checkNotNull(engineName);
		checkArgument(!engineName.isEmpty());
		engineName = engineName.toLowerCase();
		String logtalkHome = getVarOrDie(LOGTALK_HOME_ENV_VAR);
		String scriptPath = logtalkHome + "/integration/";
		String fileName = "logtalk_" + engineName + ".pl";
		scriptPath += fileName;
		File file = new File(scriptPath);
		if(!file.exists())
			throw new RuntimeException("The Logtalk installation at " + logtalkHome + " does not support the Prolog engine " + engineName);
		return scriptPath;
	}
	
	public String getTmpDirectory() {
		String tmp = getVar(LOGTALK_USER_ENV_VAR);
		if(tmp == null)
			tmp = getVar(SYSTEM_TEMP_DIRECTORY_ENV_VAR);
		return tmp;
	}
	
	public String getTmpSubdirectoryName() {
		return getVar(JPC_SHORT_NAME);
	}
	
}
