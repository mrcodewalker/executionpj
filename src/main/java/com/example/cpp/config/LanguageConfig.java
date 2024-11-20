package com.example.cpp.config;

import com.example.cpp.provider.CompilerVersion;
import com.example.cpp.provider.Language;

import java.util.HashMap;
import java.util.Map;

public class LanguageConfig {
    private static final Map<Language, String> FILE_EXTENSIONS = new HashMap<>() {{
        put(Language.C, ".c");
        put(Language.CPP, ".cpp");
        put(Language.JAVA, ".java");
        put(Language.PYTHON, ".py");
        put(Language.JAVASCRIPT, ".js");
        put(Language.GOLANG, ".go");
        put(Language.RUST, ".rs");
        put(Language.RUBY, ".rb");
    }};

    private static final Map<CompilerVersion, String[]> COMPILER_COMMANDS = new HashMap<>() {{
        // C Versions
        put(CompilerVersion.C89, new String[]{"gcc", "-std=c89", "-Wall", "-o", "Solution", "Solution.c"});
        put(CompilerVersion.C99, new String[]{"gcc", "-std=c99", "-Wall", "-o", "Solution", "Solution.c"});
        put(CompilerVersion.C11, new String[]{"gcc", "-std=c11", "-Wall", "-o", "Solution", "Solution.c"});
        put(CompilerVersion.C17, new String[]{"gcc", "-std=c17", "-Wall", "-o", "Solution", "Solution.c"});

        // C++ Versions
        put(CompilerVersion.CPP_11, new String[]{"g++", "-std=c++11", "Solution.cpp", "-o", "Solution"});
        put(CompilerVersion.CPP_14, new String[]{"g++", "-std=c++14", "Solution.cpp", "-o", "Solution"});
        put(CompilerVersion.CPP_17, new String[]{"g++", "-std=c++17", "Solution.cpp", "-o", "Solution"});
        put(CompilerVersion.CPP_20, new String[]{"g++", "-std=c++20", "Solution.cpp", "-o", "Solution"});

        // Java Versions
        put(CompilerVersion.JAVA_8, new String[]{"javac", "-source", "1.8", "-target", "1.8", "Solution.java"});
        put(CompilerVersion.JAVA_11, new String[]{"javac", "--release", "11", "Solution.java"});
        put(CompilerVersion.JAVA_17, new String[]{"javac", "--release", "17", "Solution.java"});
        put(CompilerVersion.JAVA_21, new String[]{"javac", "--release", "21", "Solution.java"});

        // Python Versions
        put(CompilerVersion.PYTHON_2, new String[]{"python2", "Solution.py"});
        put(CompilerVersion.PYTHON_3, new String[]{"python3", "Solution.py"});
        put(CompilerVersion.PYTHON_3_8, new String[]{"python3.8", "Solution.py"});
        put(CompilerVersion.PYTHON_3_9, new String[]{"python3.9", "Solution.py"});
        put(CompilerVersion.PYTHON_3_10, new String[]{"python3.10", "Solution.py"});
        put(CompilerVersion.PYTHON_3_11, new String[]{"python3.11", "Solution.py"});

        // Node.js Versions
        put(CompilerVersion.NODE_14, new String[]{"node14", "Solution.js"});
        put(CompilerVersion.NODE_16, new String[]{"node16", "Solution.js"});
        put(CompilerVersion.NODE_18, new String[]{"node18", "Solution.js"});
        put(CompilerVersion.NODE_20, new String[]{"node20", "Solution.js"});

        // Go Versions
        put(CompilerVersion.GO_1_17, new String[]{"go1.17", "run", "Solution.go"});
        put(CompilerVersion.GO_1_18, new String[]{"go1.18", "run", "Solution.go"});
        put(CompilerVersion.GO_1_19, new String[]{"go1.19", "run", "Solution.go"});
        put(CompilerVersion.GO_1_20, new String[]{"go1.20", "run", "Solution.go"});

        // Rust Versions
        put(CompilerVersion.RUST_1_54, new String[]{"rustc", "--edition=2021", "Solution.rs"});
        put(CompilerVersion.RUST_1_60, new String[]{"rustc", "--edition=2021", "Solution.rs"});
        put(CompilerVersion.RUST_1_70, new String[]{"rustc", "--edition=2021", "Solution.rs"});

        // Ruby Versions
        put(CompilerVersion.RUBY_2_7, new String[]{"ruby2.7", "Solution.rb"});
        put(CompilerVersion.RUBY_3_0, new String[]{"ruby3.0", "Solution.rb"});
        put(CompilerVersion.RUBY_3_2, new String[]{"ruby3.2", "Solution.rb"});
    }};

    private static final Map<Language, String[]> RUN_COMMANDS = new HashMap<>() {{
        put(Language.C, new String[]{"./Solution"});
        put(Language.CPP, new String[]{"./Solution"});
        put(Language.JAVA, new String[]{"java", "Solution"});
        put(Language.PYTHON, new String[]{"python3", "Solution.py"});
        put(Language.JAVASCRIPT, new String[]{"node", "Solution.js"});
        put(Language.GOLANG, new String[]{"./Solution"});
        put(Language.RUST, new String[]{"./Solution"});
        put(Language.RUBY, new String[]{"ruby", "Solution.rb"});
    }};

    public static String getFileExtension(Language language) {
        return FILE_EXTENSIONS.get(language);
    }

    public static String[] getCompilerCommand(CompilerVersion version) {
        return COMPILER_COMMANDS.get(version);
    }

    public static String[] getRunCommand(Language language) {
        return RUN_COMMANDS.get(language);
    }
}
