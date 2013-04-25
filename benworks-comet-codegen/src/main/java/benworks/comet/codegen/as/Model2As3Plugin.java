package benworks.comet.codegen.as;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.generama.QDoxCapableMetadataProvider;
import org.generama.VelocityTemplateEngine;
import org.generama.WriterMapper;

import benworks.comet.codegen.ModelPlugin;

import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;

/**
 * 生成as model代码的插件
 * 
 * @author benworks
 */
public class Model2As3Plugin extends ModelPlugin {

	private final static String DEFAULT_PACKAGE_NAME = "benworks.comet.model";

	private VelocityTemplateEngine templateEngine;

	public Model2As3Plugin(VelocityTemplateEngine velocityTemplateEngine,
			QDoxCapableMetadataProvider metadataProvider,
			WriterMapper writerMapper) {
		super(velocityTemplateEngine, metadataProvider, writerMapper);
		this.templateEngine = velocityTemplateEngine;
		setMultioutput(true);
		setFileregex("\\.java");
		setFilereplace(".as");
		setPackageregex(".+");
		setPackagereplace(DEFAULT_PACKAGE_NAME);
	}

	public String getFullyQualifiedPropertyType(BeanProperty property) {
		return toActionScriptType(property.getType());
	}

	protected void processCollect(JavaClass jc) {
		super.processCollect(jc);
	}

	public String toActionScriptType(Type type) {
		return As3TypeMap.toActionScriptType(type);
	}

	public String getPackage(JavaClass jc) {
		return packagereplace;
	}

	public String getOverride(JavaClass jc) {
		if (jc.getSuperJavaClass().getFullyQualifiedName()
				.equals("java.lang.Object")
				|| !isAcceptable(jc.getSuperJavaClass()))
			return "";
		else
			return "override ";
	}

	@Override
	public void start() {
		try {
			collect();
			super.start();
			createClassAliasRegister();
			if (!ignorebase)
				createGeneralRequest();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void createClassAliasRegister() {
		try {
			Writer writer = getWriter("ClassAliasRegister.as");
			Map<String, Map<?, ?>> contextObjects = getContextObjects();
			contextObjects.put("classAliasMap", classAliasMap);
			templateEngine.generate(writer, contextObjects, getEncoding(),
					AsClassAliasRegisterPlugin.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void createGeneralRequest() {
		try {
			Writer writer = getWriter("GeneralRequest.as");
			Map<String, Map<?, ?>> contextObjects = getContextObjects();
			templateEngine.generate(writer, contextObjects, getEncoding(),
					GeneralRequestPlugin.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Writer getWriter(String filename) throws IOException {
		String pakkage = packagereplace;
		String packagePath = pakkage.replace('.', '/');
		File dir = new File(getDestdirFile(), packagePath);
		dir.mkdirs();

		File out = new File(dir, filename);
		try {
			return new OutputStreamWriter(new FileOutputStream(out),
					getEncoding());
		} catch (Exception e) {
			throw new IOException(e.toString());
		}

	}
}
