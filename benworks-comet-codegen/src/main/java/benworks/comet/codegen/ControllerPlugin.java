package benworks.comet.codegen;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.generama.QDoxCapableMetadataProvider;
import org.generama.VelocityTemplateEngine;
import org.generama.WriterMapper;
import org.generama.defaults.JavaGeneratingPlugin;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

public class ControllerPlugin extends JavaGeneratingPlugin {
	public final String ACCEPT_CLASS_TYPE = "benworks.comet.broadcast.GeneralController";
	public final String REQUEST_TYPE = "benworks.comet.broadcast.GeneralRequest";
	public final String RESPONSE_TYPE = "benworks.comet.broadcast.GeneralResponse";
	public final String PARAMETERS_ANNOTATION_TYPE = "benworks.comet.broadcast.annotation.Parameters";

	protected String packagereplace = "";

	public ControllerPlugin(VelocityTemplateEngine velocityTemplateEngine,
			QDoxCapableMetadataProvider metadataProvider,
			WriterMapper writerMapper) {
		super(velocityTemplateEngine, metadataProvider, writerMapper);

	}

	public void setPackagereplace(String packagereplace) {
		if (packagereplace == null)
			throw new NullPointerException();
		this.packagereplace = packagereplace;
		super.setPackagereplace(packagereplace);
	}

	@Override
	public boolean shouldGenerate(Object metadata) {
		JavaClass jc = (JavaClass) metadata;
		if (jc.isInterface() || jc.isAbstract())
			return false;
		if (jc.isA(ACCEPT_CLASS_TYPE)) {
			System.out.println("*** " + jc.getFullyQualifiedName());
			return true;
		}
		return false;
	}

	public boolean isSuitableMethod(JavaMethod jm) {
		if (!jm.isPublic())
			return false;
		if (!jm.getReturns().isVoid()
				&& !jm.getReturns().getJavaClass().isA(RESPONSE_TYPE))
			return false;
		JavaParameter[] jps = jm.getParameters();
		if (jps == null || jps.length != 1)
			return false;
		return jps[0].getType().getJavaClass().isA(REQUEST_TYPE);
	}

	public String getParameterComment(Parameter param) {
		StringBuilder builder = new StringBuilder();
		builder.append(param.getKey());
		if (StringUtils.isNotEmpty(param.getDesc()))
			builder.append(", " + param.getDesc());
		if (param.isNullable())
			builder.append(", allow null or empty");
		if (StringUtils.isNotEmpty(param.getDefaultValue()))
			builder.append(", defaultValue is:" + param.getDefaultValue());
		return builder.toString();
	}

	public String getResponseComment(Response response) {
		StringBuilder builder = new StringBuilder();
		if (response.isBroadcast())
			builder.append("@broadcast ");
		else
			builder.append("@response ");
		builder.append(response.getType());
		if (StringUtils.isNotEmpty(response.getDesc()))
			builder.append(", " + response.getDesc());
		return builder.toString();
	}

	public String getAction(JavaMethod jm) {
		JavaClass jc = jm.getParentClass();
		String className = jc.getName();
		int index = className.indexOf("Controller");
		if (index > 0) {
			className = className.substring(0, index);
		}
		className = className.toLowerCase();
		return "/" + className + "/" + jm.getName();
	}

	public String trim(String str) {
		int begin = 0;
		int end = str.length();
		if (str.startsWith("\""))
			begin = begin + 1;
		if (str.endsWith("\""))
			end = end - 1;
		return str.substring(begin, end);
	}

	public String getClassName(JavaClass jc) {
		String className = jc.getName();
		int index = className.indexOf("Controller");
		if (index > 0) {
			className = className.substring(0, index);
		}
		return className + "Action";
	}

	@SuppressWarnings("unchecked")
	public List<Response> getResponses(JavaMethod jm) {
		List<Response> responses = new ArrayList<Response>();
		Annotation[] annotations = jm.getAnnotations();
		if (annotations == null)
			return responses;
		for (int i = 0; i < annotations.length; i++) {
			Annotation annotation = annotations[i];
			if (!annotation.getType().getJavaClass().isA(RESPONSE_TYPE))
				continue;
			List<Annotation> res = (List<Annotation>) annotation
					.getNamedParameter("value");
			for (Annotation anno : res) {
				Response response = new Response();
				responses.add(response);
				//
				Object broadcast = anno.getNamedParameter("broadcast");
				if (broadcast != null) {
					response.setBroadcast(Boolean.parseBoolean(broadcast
							.toString()));
				}
				//
				Object type = anno.getNamedParameter("type");
				if (type != null) {
					String typeString = type.toString();
					typeString = typeString.substring(0,
							typeString.length() - 6);
					int begin = typeString.lastIndexOf(".") + 1;
					response.setType(typeString.substring(begin));
				}
				//
				Object desc = anno.getNamedParameter("desc");
				if (desc != null) {
					response.setDesc(trim(desc.toString()));
				}
			}
		}
		return responses;
	}

	@SuppressWarnings("unchecked")
	public List<Parameter> getParameters(JavaMethod jm) {
		List<Parameter> params = new ArrayList<Parameter>();
		for (JavaParameter param : jm.getParameters()) {
			Parameter parameter = new Parameter();
			parameter.setKey(param.getName());
			parameter.setType(param.getType().getValue());
			params.add(parameter);
		}
		Annotation[] annotations = jm.getAnnotations();
		if (annotations == null)
			return params;
		for (int i = 0; i < annotations.length; i++) {
			Annotation annotation = annotations[i];
			if (!annotation.getType().getJavaClass()
					.isA(PARAMETERS_ANNOTATION_TYPE))
				continue;
			List<String> paramComments = (List<String>) annotation
					.getNamedParameter("value");
			for (int j = 0; j < paramComments.size(); j++) {
				if (j > params.size() - 1)
					break;
				Parameter parameter = params.get(j);
				if (parameter != null)
					parameter.setDesc(paramComments.get(j));
			}
			break;
		}
		return params;
	}

	public String toType(String fullType) {
		int idx = fullType.lastIndexOf(".");
		if (idx < 0) {
			return fullType;
		} else {
			return fullType.substring(idx + 1);
		}
	}

	@Override
	public void start() {
		try {
			super.start();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

}
