package benworks.comet.codegen.as;

import java.util.List;

import org.generama.QDoxCapableMetadataProvider;
import org.generama.VelocityTemplateEngine;
import org.generama.WriterMapper;

import benworks.comet.codegen.ControllerPlugin;
import benworks.comet.codegen.Parameter;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;

public class Controller2As3Plugin extends ControllerPlugin {
	private final static String DEFAULT_PACKAGE_NAME = "benworks.comet.actions";

	public Controller2As3Plugin(VelocityTemplateEngine velocityTemplateEngine,
			QDoxCapableMetadataProvider metadataProvider,
			WriterMapper writerMapper) {
		super(velocityTemplateEngine, metadataProvider, writerMapper);
		setMultioutput(true);
		setFileregex("Controller\\.java");
		setFilereplace("Action.as");
		setPackageregex(".+");
		setPackagereplace(DEFAULT_PACKAGE_NAME);
	}

	public String toActionScriptType(Type type) {
		return As3TypeMap.toActionScriptType(type);
	}

	public String toType(String fullType) {
		return As3TypeMap.toActionScriptType(fullType);
	}

	public String getPackage(JavaClass jc) {
		return packagereplace;
	}

	public String getParameterString(List<Parameter> params, boolean needType) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			Parameter param = params.get(i);
			builder.append(param.getKey());
			if (needType) {
				builder.append(":" + param.getType());
			}
			if (i < params.size() - 1)
				builder.append(",");
		}
		return builder.toString();
	}
}
