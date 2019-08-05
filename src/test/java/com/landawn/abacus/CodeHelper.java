package com.landawn.abacus;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.landawn.abacus.util.Fn;
import com.landawn.abacus.util.IOUtil;
import com.landawn.abacus.util.function.Predicate;
import com.landawn.abacus.util.stream.Stream;
import com.landawn.abacus.util.stream.Stream.StreamEx;

public class CodeHelper {

    @Test
    public void remove_comments() {
        File file = new File("./src/com/landawn/abacus/util/function/");

        for (File javaFile : file.listFiles()) {
            List<String> lines = IOUtil.readLines(javaFile);
            lines = StreamEx.of(lines).filter(new Predicate<String>() {
                @Override
                public boolean test(String line) {
                    return !(line.startsWith("// ") || line.startsWith("    //"));
                }
            }).toList();

            if (lines.get(lines.size() - 1).equals("}") && lines.get(lines.size() - 2).trim().equals("")) {
                lines.remove(lines.size() - 2);
            }
            IOUtil.writeLines(javaFile, lines);
        }
    }

    @Test
    public void test_02() {
        File file = new File("./config/abacus-entity-manager.properties");

        Map<String, File> map = Stream.listFiles(new File("./src/test/java"), true).toMap(f -> f.getName(), f -> f, Fn.ignoringMerger());

        List<String> lines = Stream.lines(file).map(Fn.trim()).map(line -> {
            if (line.trim().endsWith(".xml")) {
                String fileName = line.substring(line.indexOf('=') + 1);
                if (map.containsKey(fileName)) {
                    return line.replace(fileName, map.get(fileName).getAbsolutePath());
                }
                return null;
            } else {
                return line;
            }
        }).filter(Fn.notNull()).toList();

        IOUtil.writeLines(file, lines);
    }

}
