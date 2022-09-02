package nl.dijkrosoft.snippets.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

public class ClassFinder {

	public static void main(String[] args) throws IOException {

		if (args == null || args.length < 2) {
			System.out.println("Usage: java -jar classfinder.jar <dir> <class-name>");
			return;
		}

		String dir = args[0];
		String classname = args[1];

		System.out.println("dir=" + dir + ", and class=" + classname);

		Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (attrs.isRegularFile() && file.toString().endsWith(".jar")) {

					processjar(file, classname);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private static void processjar(Path file, String classname) throws IOException {

		JarFile jarFile = null;

		try {
			jarFile = new JarFile(file.toFile(), false, ZipFile.OPEN_READ);
			List<JarEntry> hits = jarFile.stream().filter(e -> !e.isDirectory() && e.getName().indexOf(classname) != -1)
					.collect(Collectors.toList());
			
			if ( hits.size()> 0) {
				// only print JAR name if it's a hit ( i.e. contains the searched class)
				System.out.println("\nJAR file " + file);
			}
            hits.forEach(System.out::println);

		} finally {
			if (jarFile != null) {
				jarFile.close();
			}
		}
		
		

	}
}
