package com.landawn.abacus;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.landawn.abacus.util.IOUtil;
import com.landawn.abacus.util.function.Predicate;
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

}
