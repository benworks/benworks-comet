package benworks.comet.codegen.java;

import java.util.List;

import org.generama.QDoxCapableMetadataProvider;
import org.generama.VelocityTemplateEngine;
import org.generama.WriterMapper;

import benworks.comet.codegen.ControllerPlugin;
import benworks.comet.codegen.Parameter;

import com.thoughtworks.qdox.model.JavaClass;

public class Controller2JavaPlugin extends ControllerPlugin {
	private final static String DEFAULT_PACKAGE_NAME = "benworks.comet.test.actions";

	public Controller2JavaPlugin(VelocityTemplateEngine velocityTemplateEngine,
			QDoxCapableMetadataProvider metadataProvider,
			WriterMapper writerMapper) {
		super(velocityTemplateEngine, metadataProvider, writerMapper);
		setMultioutput(true);
		setFileregex("Controller\\.java");
		setFilereplace("Action.java");
		setPackageregex(".+");
		setPackagereplace(DEFAULT_PACKAGE_NAME);
	}

	public String getPackage(JavaClass jc) {
		return packagereplace;
	}

	public String getParameterString(List<Parameter> params, boolean needType) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			Parameter param = params.get(i);
			builder.append(param.getType() + " " + param.getKey());
			if (i < params.size() - 1)
				builder.append(",");
		}
		return builder.toString();
	}
}
