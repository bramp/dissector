package net.bramp.dissector.java;

import com.google.common.collect.ImmutableList;
import net.bramp.dissector.NodePrinter;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.png.PngDissector;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static net.bramp.dissector.Helper.open;

/**
 * @author bramp
 */
public class JavaClassTest {

    @Test
    public void test() throws IOException {
        ExtendedRandomAccessFile in = open(getClass(), "JavaClassDissector.class" );
        JavaClassDissector dissector = new JavaClassDissector().read(in);

        new NodePrinter().print(dissector);
    }

	@Test
	public void testAll() throws IOException {

		List<ClassLoader> classLoadersList = ImmutableList.of(
			ClasspathHelper.contextClassLoader(),
			ClasspathHelper.staticClassLoader()
		);

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(false /* don't exclude Object.class */))
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
				.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("net.bramp.dissector")))
			);

		for (Class clazz : reflections.getSubTypesOf(Object.class)) {
			System.out.print(clazz);
			String c = clazz.getName();
			c = c.substring(c.lastIndexOf('.') + 1); // There might be a better way than this!
			ExtendedRandomAccessFile in = open( clazz, c + ".class" );
			new PngDissector().read(in); // Read but don't do anything with it
			System.out.println(" [OK]");
		}
	}
}
