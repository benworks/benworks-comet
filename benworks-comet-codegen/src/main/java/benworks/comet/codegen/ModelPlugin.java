package benworks.comet.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.generama.QDoxCapableMetadataProvider;
import org.generama.VelocityTemplateEngine;
import org.generama.WriterMapper;
import org.generama.defaults.JavaGeneratingPlugin;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;

public class ModelPlugin extends JavaGeneratingPlugin {

	public final String BASE_PACKAGE = "benworks.comet";
	public final String ACCEPT_CLASS_TYPE_1 = "benworks.comet.broadcast.BroadcastMessage";
	public final String ACCEPT_CLASS_TYPE_2 = "benworks.comet.broadcast.ResponseMessage";
	public final String INCLUDE_ENUMS_CLASS_TYPE = "benworks.comet.broadcast.annotation.IncludeEnums";

	protected Map<String, JavaClass> enumMap = new HashMap<String, JavaClass>();
	protected Map<String, String> classAliasMap = new HashMap<String, String>();

	protected String packagereplace = "";
	protected boolean ignorebase = false;

	public ModelPlugin(VelocityTemplateEngine velocityTemplateEngine,
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

	public void setIgnorebase(boolean ignorebase) {
		this.ignorebase = ignorebase;
	}

	public boolean shouldGenerate(Object metadata) {
		JavaClass jc = (JavaClass) metadata;
		if (jc.isInterface() || jc.isAbstract())
			return false;
		if (ignorebase && jc.getPackageName().indexOf(BASE_PACKAGE) >= 0)
			return false;
		if (isAcceptable(jc)) {
			classAliasMap.put(jc.getFullyQualifiedName(), jc.getName());
			System.out.println("*** " + jc.getFullyQualifiedName());
			return true;
		}
		return false;
	}

	protected boolean isAcceptable(JavaClass jc) {
		return jc.isA(ACCEPT_CLASS_TYPE_1) || jc.isA(ACCEPT_CLASS_TYPE_2);
	}

	public boolean isSamePackage(JavaClass clazz1, JavaClass clazz2) {
		if (clazz1 == null || clazz2 == null)
			return false;
		if (clazz1.getPackage() == null || clazz2.getPackage() == null)
			return false;
		if (isAcceptable(clazz1) && isAcceptable(clazz2))
			return true;
		return clazz1.getPackage().getName()
				.equals(clazz2.getPackage().getName());
	}

	/**
	 * 收集class信息
	 */
	public void collect() {
		for (final Object element : getMetadata()) {
			final JavaClass jc = (JavaClass) element;
			processCollect(jc);
		}
	}

	protected void processCollect(JavaClass jc) {
		if (jc.isEnum()) {
			enumMap.put(jc.getFullyQualifiedName().replace('$', '.'), jc);
		}
	}

	/**
	 * 属性的类型
	 * 
	 * @param property
	 * @return
	 */
	public String getPropertyType(BeanProperty property) {
		String type = getFullyQualifiedPropertyType(property);
		int idx = type.lastIndexOf(".");

		if (idx < 0) {
			return type;
		} else {
			return type.substring(idx + 1);
		}
	}

	/**
	 * 属性类型的完整路径
	 * 
	 * @param property
	 * @return
	 */
	public String getFullyQualifiedPropertyType(BeanProperty property) {
		return property.getType().getValue();
	}

	/**
	 * 判断此BeanProperty是否适合输出
	 * 
	 * @param bp
	 * @return
	 */
	public boolean isSuitableProperty(BeanProperty bp) {
		if (bp == null) {
			return false;
		}
		JavaMethod jm = bp.getAccessor();
		if (jm == null) {
			return false;
		}
		boolean suitable = true;
		if (!jm.isPublic() || !jm.isPropertyAccessor()) {
			suitable = false;
		}

		if (suitable) {
			Type type = jm.getReturns();
			if (type.isVoid()) {
				suitable = false;
			}
		}
		return suitable;
	}

	/**
	 * 字符串首字母大写
	 * 
	 * @param name
	 * @return
	 */
	public String capitalize(String name) {
		return StringUtils.capitalize(name);
	}

	@SuppressWarnings("unchecked")
	public List<JavaClass> getIncludeEnums(JavaClass jc) {
		List<JavaClass> enums = new ArrayList<JavaClass>();
		Annotation[] annotations = jc.getAnnotations();
		if (annotations == null)
			return enums;
		for (int i = 0; i < annotations.length; i++) {
			Annotation annotation = annotations[i];
			if (!annotation.getType().getJavaClass()
					.isA(INCLUDE_ENUMS_CLASS_TYPE))
				continue;
			List<Annotation> res = (List<Annotation>) annotation
					.getNamedParameter("value");
			for (Annotation anno : res) {
				Object enumClass = anno.getNamedParameter("value");
				if (enumClass != null) {
					String typeString = enumClass.toString();
					typeString = typeString.substring(0,
							typeString.length() - 6);
					typeString = typeString.replace('$', '.');
					JavaClass enumType = enumMap.get(typeString);
					enums.add(enumType);
				}
			}
		}
		return enums;
	}

	public List<String> getImports(JavaClass jc) {
		List<String> imports = new ArrayList<String>();
		BeanProperty[] properties = jc.getBeanProperties();
		for (BeanProperty property : properties) {
			String typeValue = property.getType().getValue();
			if (typeValue.indexOf(".") >= 0
					&& !typeValue.equals("java.lang.Object")
					&& !isSamePackage(jc, property.getType().getJavaClass())) {
				imports.add(typeValue);
			}
		}
		return imports;
	}

	public String getExtends(JavaClass jc) {
		JavaClass superJc = jc.getSuperJavaClass();
		if (superJc.getFullyQualifiedName().equals("java.lang.Object")
				|| !isAcceptable(superJc)) {
			return "";
		}
		return "extends " + superJc.getName();
	}
}
