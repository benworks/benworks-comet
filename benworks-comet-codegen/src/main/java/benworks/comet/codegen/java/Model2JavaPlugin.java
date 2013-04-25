/**
 * 
 */
package benworks.comet.codegen.java;

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

/**
 * 生成java model代码的插件
 * 
 * @author benworks
 */
public class Model2JavaPlugin extends ModelPlugin {

	private VelocityTemplateEngine templateEngine;
	private final static String DEFAULT_PACKAGE_NAME = "benworks.comet.test.model";

	public Model2JavaPlugin(VelocityTemplateEngine velocityTemplateEngine,
			QDoxCapableMetadataProvider metadataProvider,
			WriterMapper writerMapper) {
		super(velocityTemplateEngine, metadataProvider, writerMapper);
		this.templateEngine = velocityTemplateEngine;
		setMultioutput(true);
		setPackageregex(".+");
		setPackagereplace(DEFAULT_PACKAGE_NAME);
	}

	public String getPackage(JavaClass jc) {
		return packagereplace;
	}

	/**
	 * 属性的类型
	 * 
	 * @param property
	 * @return
	 */
	public String getPropertyType(BeanProperty property) {
		String type = super.getPropertyType(property);
		String generic = property.getType().toGenericString();
		int begin = generic.indexOf("<");
		if (begin > 0) {
			generic = generic.substring(begin + 1);
			String[] generics = generic.split(",");

			for (int i = 0; i < generics.length; i++) {
				int lastIndex = generics[i].lastIndexOf(".");
				if (lastIndex > 0)
					generics[i] = generics[i].substring(lastIndex + 1);
			}
			StringBuilder builder = new StringBuilder("<");
			for (int i = 0; i < generics.length; i++) {
				builder.append(generics[i]);
				if (i < generics.length - 1)
					builder.append(",");
			}
			type = type + builder;
		}
		if (property.getType().isArray())
			type = type + "[]";
		return type;
	}

	protected void processCollect(JavaClass jc) {
		super.processCollect(jc);
	}

	@Override
	public void start() {
		try {
			collect();
			super.start();
			createClassAliasRegister();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void createClassAliasRegister() {
		try {
			Writer writer = getWriter("ClassAliasRegister.java");
			Map<String, Object> contextObjects = getContextObjects();
			contextObjects.put("classAliasMap", classAliasMap);
			contextObjects.put("packageName", packagereplace);
			templateEngine.generate(writer, contextObjects, getEncoding(),
					JavaClassAliasRegisterPlugin.class);
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
